
(ns main.wagmi 
  (:require ["react" :as react]
            [re-frame.core :as rf :refer [subscribe dispatch reg-fx]]
            
            ["@guildxyz/sdk" :as guild]
  
            ["wagmi" :as wagmi]
            ["wagmi/chains" :refer [mainnet base optimism polygon]]
            ["wagmi/connectors" :refer [injected metaMask safe walletConnect coinbaseWallet]]
            ["@tanstack/react-query" :as tanstack]
            ["@mantine/core" :refer [MantineProvider AppShell Avatar Badge Burger Button createTheme Group SimpleGrid Grid Container Flex Stack Skeleton]]
            ["@mantine/hooks" :refer [useDisclosure]]
            ["@tabler/icons-react" :as icon]

            
            ))

(def wallet-connect-project-id "a1f553a67e9967aba78bc770c739bd61")

(defonce query-client (new tanstack/QueryClient))

(def config
  (wagmi/createConfig 
    #js {:chains #js [mainnet base optimism polygon] 
         :connectors #js [(injected) (walletConnect #js {:projectId wallet-connect-project-id}) (coinbaseWallet)]
         :transports #js {"1" (wagmi/http)}}))

(defn bundle [child];& children]
  [:> MantineProvider
   [:> wagmi/WagmiProvider {:config config}
    [:> tanstack/QueryClientProvider {:client query-client}
     ;[:> airstack/AirstackProvider {:apiKey ""}
      child
      ;[:<> children]
      ;]
     ]]])


(defn connect-kit []
  ;(react/useEffect
  ;  (fn []
  ;    (js/console.log "Connect kit rendered..")
  ;   )) 
  (let [{:keys [address chain chainId]} (js->clj (wagmi/useAccount) :keywordize-keys true)
        sign-message (.-signMessageAsync (wagmi/useSignMessage))
        signer (.custom guild/createSigner (fn [message] (sign-message #js {:message message})) address)
        connect (.-connect (wagmi/useConnect))
        ens-name (js->clj (wagmi/useEnsName #js {:address address}) :keywordize-keys true)
        player-name (or (:data ens-name) address)
        disconnect (.-disconnect (wagmi/useDisconnect))
        connectors (.-connectors (wagmi/useConnect))
        signer (.-signMessageAsync (wagmi/useSignMessage))]    
        
    (dispatch [:assoc-in [:address] address])
    (when address (dispatch [:init-in [:signer-fn] signer]))
    (dispatch [:assoc-in [:player :nickname] player-name])
    
    (if address
     
      [:<>
       ;[:p (str (js->clj guild-client))]
       ;player-name 
       [:> Button 
          {:onClick #(disconnect)}
          "Disconnect"]
       ]
      
      [:<>
       [:center [:h2 "Connect your wallet:"]]
       (for [connector connectors]
         [:> Button 
          {:key (.-id connector)
           :onClick #(connect #js {:connector connector})}
          (.-name connector)])])
    ))
