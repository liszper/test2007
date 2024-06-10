(ns main.app
  (:require [reagent.core :as reagent]
            [reagent.dom  :as reagent-dom]   
            ["react-error-boundary" :refer [ErrorBoundary]]
    
            ["@mantine/core" :refer [MantineProvider]]
            
            ["wagmi" :as wagmi]
            ["wagmi/chains" :refer [mainnet base]]
            ["wagmi/connectors" :refer [injected metaMask safe walletConnect]]
            ["@tanstack/react-query" :as tanstack]

            ["../ecmascript/config" :refer [Config]]

            ["@airstack/airstack-react" :as airstack] 
            
            [main.component :refer [main canvas-test]]))

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

(defn ^:export init []
  (render)
  )
