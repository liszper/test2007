
(ns main.app
  (:require [main.wagmi :as wagmi]
            [main.guild :refer [Guild]]
            [main.models :refer [avatars]]
            
            [reagent.core :as reagent]
            [reagent.dom.client :as rdc]
            [re-frame.core :as rf :refer [subscribe dispatch]]
            ["react-error-boundary" :refer [ErrorBoundary]]
            
            ["@mantine/core" :refer [MantineProvider]]
    
            ["ws" :as WebSocket]
            [cognitect.transit :as t]

            [main.component :refer [main debug?]]))


; App db


(rf/reg-event-db :reset (fn [_ [_ value]] value))
(rf/reg-event-db :assoc-in (fn [db [_ path value]] (assoc-in db path value)))
(rf/reg-event-db :init-in (fn [db [_ path value]] (if-not (get-in db path) (let [] (js/console.log "init: "(str path)) (assoc-in db path value)) db)))

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
    [:f> wagmi/provider
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
