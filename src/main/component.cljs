(ns main.component
  (:require ["@mantine/core" :refer [AppShell Avatar Burger Button createTheme Group SimpleGrid Grid Container Flex Stack Skeleton]]
            ["@mantine/hooks" :refer [useDisclosure]]
            ["@tabler/icons-react" :as icon]

            ["react" :as react]
            
            ["wagmi" :as wagmi]
            
            ["three" :as three]
            ["@react-three/fiber" :as fiber]
            ["@react-three/drei" :as drei]
            ["@react-three/rapier" :as rapier]
            ["ecctrl" :refer [Controller] :as ecc]
            
            ;["@guildxyz/sdk" :as guild]
            
            ["@airstack/airstack-react" :as airstack] 
            
            ["../ecmascript/threejs" :refer [Box]]
            ["../ecmascript/ecctrl" :refer [App]]
            ))

(defn connect-kit []
  (let [{:keys [address chain chainId]} (js->clj (wagmi/useAccount) :keywordize-keys true)
        connect (.-connect (wagmi/useConnect))
        ens-name (js->clj (wagmi/useEnsName #js {:address address}) :keywordize-keys true)
        disconnect (.-disconnect (wagmi/useDisconnect))
        connectors (.-connectors (wagmi/useConnect))
        signer (.-signMessageAsync (wagmi/useSignMessage))]    
    
    (if address
     
      [:> Flex {:gap "sm"}
       (or (:data ens-name) address)
       [:> Button 
          {:onClick #(disconnect)}
          "Disconnect"]
       ]
      
      [:> Flex {:gap "sm"}
       (for [connector connectors]
         [:> Button 
          {:key (.-id connector)
           :onClick #(connect #js {:connector connector})}
          (.-name connector)])])
    ))

(defn loading []
  [:> Box {:position [(- 3.6) 0 0]}])

(defn player []
  (let [
        keyboard-controls
        [{:keys ["ArrowUp" "KeyW"] :name "forward"}
         {:keys ["ArrowDown" "KeyS"] :name "backward"}
         {:keys ["ArrowLeft" "KeyA"] :name "leftward"}
         {:keys ["ArrowRight" "KeyD"] :name "rightward"}
         {:keys ["Space"] :name "jump"}
         {:keys ["Shift"] :name "run"}]
        ]
       [:> drei/KeyboardControls {:map keyboard-controls}
        [:> ecc/default {:maxVelLimit 5}
         [:> drei/Gltf
          {:castShadow "castShadow"
           :receiveShadow "receiveShadow"
           :position [0 (- 0.55) 0]
           :scale 0.315
           :src "/ghost_w_tophat-transformed.glb"}]
         ]
      ]
    )
  )

(defn environment []
  (let []
    [:> rapier/RigidBody {:colliders "trimesh" :type "fixed"}
     [:> drei/Gltf
      {:castShadow "castShadow" 
       :receiveShadow "receiveShadow"
       :position [0 0 0]
       ;:rotation [(/ (- (.-PI js/Math)) 2) 0 0]
       :scale 0.6
       :src "/terrain.glb"}]]
    )
  )

(defn canvas []
  (let [
        ]  
    [:> fiber/Canvas {:shadows "shadows" :onPointerDown (fn [e] (.requestPointerLock (.-target e)))}
     [:> react/Suspense {:fallback (reagent.core/as-element [loading])}
      [:> drei/Fisheye {:zoom 0.4}
       ;[:> drei/Environment {:files "/psy.hdr" :ground {:scale 100}}]
       [:directionalLight {:intensity 10 :castShadow "castShadow" :position [-20 20 20] :shadow-bias -0.0004}
        [:orthographicCamera {:args [-20 20 20 -20] :attach "shadow-camera"}]]
       [:ambientLight {:intensity 1}]
       [:> rapier/Physics {:timeStep "vary"}
        
        [player]
        [environment]
   
        ;[:> Box {:position [3.6 10 0]}]
        ;[:> Box {:position [(- 3.6) 3 3]}]
        ]]]]
    ))

(defn canvas-test-inner []
  (react/useEffect
    (fn [] 
      (js/console.log "Main component rendered..")
      )) 
  [:> fiber/Canvas
   [:ambientLight {:intensity (/ (.-PI js/Math) 2)}]
   [:spotLight {:angle 0.15 :decay 0 :intensity (.-PI js/Math) :penumbra 1 :position [10 10 10]}]
   [:pointLight {:decay 0 :intensity (.-PI js/Math) :position [(- 10) (- 10) (- 10)]}]
      
   
   [:> Box {:position [(- 1.2) 0 0]}]
   [:> Box {:position [1.2 0 0]}]
   ]
  )

(defn canvas-test []
  [:div
   [:f> canvas-test-inner]
   ])

(defn main []
  (react/useEffect
    (fn [] 
      (js/console.log "Main component rendered..")
      )) 
  (let [{:keys [address status chain chainId]} (js->clj (wagmi/useAccount) :keywordize-keys true)
        connect (.-connect (wagmi/useConnect))
        disconnect (.-disconnect (wagmi/useDisconnect))
        connectors (.-connectors (wagmi/useConnect))
        signer (.-signMessageAsync (wagmi/useSignMessage))
        ]
        [:> Grid
         [:> (.-Col Grid) {:span 8 :style {:background "grey" :height "100vh"}}

          ;[canvas-test]
          (case status
            "connected" [canvas]
            "disconnected" [canvas-test]
            "connecting" [canvas-test]
            [:div "Sign-in First!"])

          ]
         [:> (.-Col Grid) {:span 4}
          [:> Stack {:align "stretch" :h "100%" :justify "space-between"}
         
           [:f> connect-kit]

           [:> SimpleGrid {:cols 3}
           [:> Avatar {:radius "sm"} [:> icon/IconStar]]
           [:> Avatar {:radius "sm"} [:> icon/IconAlien]]
           [:> Avatar {:radius "sm"}]
           [:> Avatar {:radius "sm"}]
           [:> Avatar {:radius "sm"}]
           ] 
           
           ]]]))
