
(ns main.app
  (:require [main.wagmi :refer [bundle]]
    
            [reagent.core :as reagent]
            [reagent.dom  :as reagent-dom] 
            [reagent.dom.client :as rdc]
            [re-frame.core :as rf :refer [subscribe dispatch]]
            ["react-error-boundary" :refer [ErrorBoundary]]
    
            ["ws" :as WebSocket]
            [cognitect.transit :as t]

            [main.component :refer [main debug?]]))





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





(defn react-error-handler [info] [:p {:style {:color :red}} (str (.-error info))])

(defn render-fn []
  [:> ErrorBoundary {:FallbackComponent (fn [info] (reagent.core/as-element [react-error-handler info]))}    
   [:f> bundle
    [:f> main]]])

(defn render [] (reagent-dom/render [render-fn] (.getElementById js/document "app")))

(defn ^:dev/after-load clear-cache-and-render! [] (rf/clear-subscription-cache!) (render))





(def webs (atom nil))

(rf/reg-fx :send (fn [opts] (.send @webs;(get-in opts [:db :websocket "join"])
                                   (t/write (t/writer :json) (:message opts)))))
(rf/reg-event-fx :send (fn [{db :db} [_ opts]] {:send {:message opts :db db}}))

(defn ws-connect [uri]
  (let [ws (new js/WebSocket uri)]
    ;(dispatch [:assoc-in [:websocket uri] ws])
    (reset! webs ws)
    (set! (.-onmessage ws)
          (fn [event]
            (let [{:keys [id player position quaternion] :as data} (t/read (t/reader :json) (.-data event))]
              (case id
                "movement"
                (dispatch [:assoc-in [:players (:located player) (:name player)]
                         {:position position
                          :quaternion quaternion
                          }])
                nil)
              )
            ;(.log js/console "received: %s" (t/read (t/reader :json) (.-data event)))
            ))
    (set! (.-onopen ws) (fn [] 
                          (.log js/console "Websocket connection established.")
                          ))
    (set! (.-onerror ws) (.-error js/console))
    ))





(defn ^:export init []
  
  (rf/dispatch-sync [:reset {}])

  (render)

  (ws-connect "/join")

  (when debug? (.addEventListener js/document "keydown" #(js/console.log %)))
  (when debug? (.addEventListener js/document "keyup" #(js/console.log %)))
  
  )
