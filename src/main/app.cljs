(ns main.app
  (:require [reagent.core :as reagent]
            [reagent.dom  :as reagent-dom] 
            [reagent.dom.client :as rdc]
            [re-frame.core :as rf :refer [subscribe dispatch]]
            ["react-error-boundary" :refer [ErrorBoundary]]
    
            ["@mantine/core" :refer [MantineProvider]]
            
            ["ws" :as WebSocket]
            [cognitect.transit :as t]

            ["wagmi" :as wagmi]
            ["wagmi/chains" :refer [mainnet base]]
            ["wagmi/connectors" :refer [injected metaMask safe walletConnect]]
            ["@tanstack/react-query" :as tanstack]

            ["@airstack/airstack-react" :as airstack] 
            
            [main.component :refer [main]]))

(rf/reg-event-db :reset (fn [_ [_ value]] value))
(rf/reg-event-db :assoc-in (fn [db [_ path value]] (assoc-in db path value)))
(rf/reg-event-db :init-in (fn [db [_ path value]] (if-not (get-in db path) (let [] (js/console.log "init: "(str path)) (assoc-in db path value)) db)))
(rf/reg-sub :get-in (fn [db [_ path]] (get-in db path)))
(rf/reg-fx 
  :wait
  (fn [opts]
    (when (or (nil? (:when opts)) ((:when opts) (:db opts)))
      (-> ((:fn opts) (:db opts))
          (.then (fn [x] 
                   (when (:then opts) ((:then opts) x))
                   (when (:log opts) ((:log opts) x))))
          (.catch (fn [x] (when (:catch opts) ((:catch opts) x))))))))
(rf/reg-event-fx :wait (fn [{db :db} [_ opts]] {:wait (assoc opts :db db)}))

(defonce do-timer (js/setInterval #(dispatch [:assoc-in [:time] (js/Date.)]) 1000))


(defn clock []
  (let [colour @(subscribe [:get-in [:time-color]])
        time   (-> @(subscribe [:get-in [:time]])
                   .toTimeString
                   (clojure.string/split " ")
                   first)]
    [:div.example-clock {:style {:color colour}} time]))

(defn color-input []
  (let [gettext (fn [e] (-> e .-target .-value))
        emit    (fn [e] (dispatch [:assoc-in [:time-color] (gettext e)]))]
    [:div.color-input
     "Display color: "
     [:input {:type "text"
              :style {:border "1px solid #CCC"}
              :value @(subscribe [:get-in [:time-color]])
              :on-change emit}]]))

(defn test-ui
  []
  [:div
   [:h1 "The time is now:"]
   [clock]
   [color-input]])


(def wallet-connect-project-id "a1f553a67e9967aba78bc770c739bd61")
(defonce query-client (new tanstack/QueryClient))
(def config
  (wagmi/createConfig 
    #js {:chains #js [mainnet] 
         :connectors #js [(injected) (walletConnect #js {:projectId wallet-connect-project-id})]
         :transports #js {"1" (wagmi/http)}}))

(defn bundle []
  [:> MantineProvider
   [:> wagmi/WagmiProvider {:config config}
    [:> tanstack/QueryClientProvider {:client query-client}
     [:> airstack/AirstackProvider {:apiKey ""}
      [:f> main]]]]])

(defn react-error-handler [info]
  [:p {:style {:color :red}} (str (.-error info))])

(defn render-fn []
  [:> ErrorBoundary {:FallbackComponent (fn [info] (reagent.core/as-element [react-error-handler info]))}    
   [:f> bundle]
   ]
  )

(defn render []
  (reagent-dom/render [render-fn] (.getElementById js/document "app"))
  )

(defn ^:dev/after-load clear-cache-and-render!
  []
  (rf/clear-subscription-cache!)
  (render))

(def webs (atom nil))

(rf/reg-fx :send (fn [opts] (.send @webs;(get-in opts [:db :websocket "join"])
                                   (t/write (t/writer :json) (:message opts)))))
(rf/reg-event-fx :send (fn [{db :db} [_ opts]] {:send {:message opts :db db}}))

(defn ws-connect [path]
  (let [uri (str "ws://localhost:8080/"path)
        ws (new js/WebSocket uri)]
    (dispatch [:assoc-in [:websocket uri] ws])
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
  
  (rf/dispatch-sync
    [:reset
     {:environment {:map "Ethereum"}
      }])

  (render)

  (ws-connect "join")
  
  )
