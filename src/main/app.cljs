(ns main.app
  (:require [reagent.core :as reagent]
            [reagent.dom  :as reagent-dom] 
            [reagent.dom.client :as rdc]
            [re-frame.core :as rf :refer [subscribe dispatch]]
            ["react-error-boundary" :refer [ErrorBoundary]]
    
            ["@mantine/core" :refer [MantineProvider]]
            
            ["wagmi" :as wagmi]
            ["wagmi/chains" :refer [mainnet base]]
            ["wagmi/connectors" :refer [injected metaMask safe walletConnect]]
            ["@tanstack/react-query" :as tanstack]

            ["../ecmascript/config" :refer [Config]]

            ["@airstack/airstack-react" :as airstack] 
            
            [main.component :refer [main canvas-test]]))

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


(defonce query-client (new tanstack/QueryClient))

(defn bundle []
  [:> MantineProvider
   [:> wagmi/WagmiProvider {:config Config}
    [:> tanstack/QueryClientProvider {:client query-client}
     [:> airstack/AirstackProvider {:apiKey ""}
      [:f> main]]]]])

(defn error-handler [info]
  [:p {:style {:color :red}} (str (.-error info))])

(defn render-fn []
  [:> ErrorBoundary {:FallbackComponent (fn [info] (reagent.core/as-element [error-handler info]))}    
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

(defn ^:export init []
  (rf/dispatch-sync
    [:reset
     {:time (js/Date.)
      :time-color "orange"
      }])
  (render)
  )
