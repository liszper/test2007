(ns main.app
  (:require
   [main.wagmi :refer [provider]]
   ["wagmi" :as wagmi]
   [main.models :refer [avatars]]

   [reagent.core :as reagent]
   [reagent.dom.client :as rdc]
   [re-frame.core :as rf :refer [dispatch subscribe]]
   ["react" :as react]
   ["react-error-boundary" :refer [ErrorBoundary]]

   [cognitect.transit :as t]

   ["@mantine/core" :refer [MantineProvider AppShell Modal Avatar Badge Burger Button createTheme Group Center SimpleGrid SimpleGrid Grid Container Flex Stack Skeleton]]

   ["../ecmascript/threejs" :refer [Box]]

   [main.views.lobby :refer [lobby welcome-modal]]
   [main.views.canvas :refer [canvas]]
   [main.components.dashboard :refer [dashboard]]))


(set! *warn-on-infer* false)

(defonce debug? false)

(defn use-account-effect [status chain located]
  (react/useEffect
   (fn []
     (dispatch [:assoc-in [:status] status])
     (dispatch [:assoc-in [:chain] chain])
     (dispatch [:assoc-in [:player :located] (:name chain)])
     (when-not located (dispatch [:assoc-in [:player :located] [0 0]]))
     (fn []))
   #js [status chain located]))

(defn left-column [status]
  [:> (.-Col Grid) {:span 8 :style {:position "fixed" :top 0 :left 0 :width "70vw" :background "black" :height "100vh" :padding 0}}
   (case status
     "connected" [:f> canvas debug?]
     "disconnected" [:f> lobby debug?]
     "connecting" [:f> lobby debug?]
     [:div "Sign-in First!"])])

(defn right-column [tos? status]
  [:> (.-Col Grid) {:span 4 :style {:position "fixed" :top 0 :right 0 :width "34vw"}}
   (welcome-modal tos? status)
   [dashboard]])

(defn main []
  (let [{:keys [status chain]} (js->clj (wagmi/useAccount) :keywordize-keys true)
        tos? @(subscribe [:get-in [:tos?]])
        {:keys [located]} @(subscribe [:get-in [:player]])]
    (use-account-effect status chain located)
    [:> Grid
     [left-column status]
     [right-column tos? status]]))

; App db


(rf/reg-event-db :reset (fn [_ [_ value]] value))
(rf/reg-event-db :assoc-in (fn [db [_ path value]] (assoc-in db path value)))
(rf/reg-event-db :init-in (fn [db [_ path value]]
  (if-not (get-in db path)
    (do
      (js/console.log "init: " (str path))
      (assoc-in db path value))
    db)))

(rf/reg-sub :get-in (fn [db [_ path]] (get-in db path)))

(rf/reg-fx :wait
  (fn [opts]
    (when (or (nil? (:when opts)) ((:when opts) (:db opts)))
      (-> ((:fn opts) (:db opts))
          (.then (fn [x] 
                   (when (:then opts) ((:then opts) x))
                   (when (:log opts) ((:log opts) x))))
          (.catch (fn [x] (when (:catch opts) ((:catch opts) x))))))))
(rf/reg-event-fx :wait (fn [{db :db} [_ opts]] {:wait (assoc opts :db db)}))


; App providers, render, reload


(defn app []
  [:> ErrorBoundary {:FallbackComponent (fn [info] (reagent.core/as-element [:p {:style {:color :red}} (str (.-error info))]))}    
   [:> MantineProvider
    [:f> provider
     [:f> main]]]])

(defn render [] (-> "app" js/document.getElementById rdc/create-root (rdc/render [app])))

(defn ^:dev/after-load clear-cache-and-render! [] (rf/clear-subscription-cache!) (render))


; Websocket handler


(def websocket (atom nil))
(rf/reg-fx :send (fn [opts [_ _]] (when (and @websocket (.-readyState @websocket)) (.send @websocket (t/write (t/writer :json) (:message opts))))))
(rf/reg-event-fx :send (fn [{db :db} [_ opts]] {:send {:message opts :db db}}))

(defn ws-handler [{:keys [id players] :as message}]
  (case id
    "movement" (dispatch [:assoc-in [:players] players])
    (js/console.log (str message))))

(defn ws-connect [uri]
  (let [ws (new js/WebSocket uri)]
    (reset! websocket ws)
    (set! (.-onmessage ws) (fn [event] (ws-handler (t/read (t/reader :json) (.-data event)))))
    (set! (.-onopen ws) (fn [] (js/console.log "Websocket connection established.")))
    (set! (.-onerror ws) (.-error js/console))))


; App init


(def default-db-state 
  {:player {:avatar (:wizard avatars)}})

(defn ^:export init []
  
  (rf/dispatch-sync [:reset default-db-state])

  (render)

  (ws-connect "/join")

  ;(js/console.log Guild)
  ;(fetch-guild)

  (when debug? (.addEventListener js/document "keydown" #(js/console.log %)))
  (when debug? (.addEventListener js/document "keyup" #(js/console.log %)))
  
  )
