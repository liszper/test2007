(ns main.component
  (:require ["@mantine/core" :refer [AppShell Avatar Badge Burger Button createTheme Group SimpleGrid Grid Container Flex Stack Skeleton]]
            ["@mantine/hooks" :refer [useDisclosure]]
            ["@tabler/icons-react" :as icon]

            ["react" :as react]
            [re-frame.core :as rf :refer [subscribe dispatch reg-fx]]
            
            ["wagmi" :as wagmi]
            
            ["three" :as three]
            ["@react-three/fiber" :as fiber]
            ["@react-three/drei" :as drei]
            ["@react-three/rapier" :as rapier]
            ["ecctrl" :refer [Controller] :as ecc]
            
            ["@guildxyz/sdk" :as guild]
            
            ["@airstack/airstack-react" :as airstack] 
            
            ["../ecmascript/threejs" :refer [Box]]
            ["../ecmascript/ecctrl" :refer [App]]
            ["../ecmascript/config" :refer [signIntoGuild guildClient]]
            ))


(defonce guild-client (guild/createGuildClient "test"))
(defonce user-client (.-user guild-client))

(defn logged-in? [{:keys [address status guilds]}] 
  (and address (= status "connected") 
       (not guilds)
       ))

(defonce do-timer 
  (js/setInterval

    (fn []
       
      (dispatch 
        [:wait 
         {:when logged-in? 
          :fn (fn [{:keys [address signer-fn]}] (.getMemberships user-client address signer-fn))
          :then #(dispatch [:assoc-in [:guilds :joined] (js->clj % :keywordize-keys true)]) ;bad design pattern
          :log #(js/console.log (str "success:" (js->clj % :keywordize-keys true)))
          :catch #(js/console.log (str "Error: " (js->clj %)))}])

      (dispatch 
        [:wait 
         {:when logged-in? 
          :fn (fn [{:keys [address signer-fn]}] (.getPoints user-client address signer-fn))
          :then #(dispatch [:assoc-in [:guilds :points] (js->clj % :keywordize-keys true)]) ;bad design pattern
          :log #(js/console.log (str "success:" (js->clj % :keywordize-keys true)))
          :catch #(js/console.log (str "Error: " (js->clj %)))}])



      )
    
    10000))


(defn connect-kit []
  (react/useEffect
    (fn []
      (js/console.log "Connect kit rendered..")
     )) 
  (let [{:keys [address chain chainId]} (js->clj (wagmi/useAccount) :keywordize-keys true)
        _ (dispatch [:assoc-in [:address] address])
        sign-message (.-signMessageAsync (wagmi/useSignMessage))
        signer (.custom guild/createSigner (fn [message] (sign-message #js {:message message})) address)
        _ (when address (dispatch [:init-in [:signer-fn] signer]))
        connect (.-connect (wagmi/useConnect))
        ens-name (js->clj (wagmi/useEnsName #js {:address address}) :keywordize-keys true)
        disconnect (.-disconnect (wagmi/useDisconnect))
        connectors (.-connectors (wagmi/useConnect))
        signer (.-signMessageAsync (wagmi/useSignMessage))]    
    
    (if address
     
      [:> Flex {:gap "sm"}
       ;[:p (str (js->clj guild-client))]
       (or (:data ens-name) address)
       [:> Button 
          {:onClick #(disconnect)}
          "Disconnect"]
       ;[:f> signIntoGuild]
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
        [:> ecc/default {:maxVelLimit 5
                         :camInitDis -3}
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
        _ (dispatch [:assoc-in [:status] status])
        connect (.-connect (wagmi/useConnect))
        disconnect (.-disconnect (wagmi/useDisconnect))
        connectors (.-connectors (wagmi/useConnect))
        signer (.-signMessageAsync (wagmi/useSignMessage))
        guild-data @(subscribe [:get-in [:guilds]])
        points @(subscribe [:get-in [:guilds :points]])
        address-a @(subscribe [:get-in [:address]])
        ]
        [:> Grid
         [:> (.-Col Grid) {:span 8 :style {:position "absolute" :top 0 :left 0 :width "70vw" :background "grey" :height "100vh" :padding 0}}

          ;[canvas-test]
          (case status
            "connected" [canvas]
            "disconnected" [canvas-test]
            "connecting" [canvas-test]
            [:div "Sign-in First!"])

          ]
         [:> (.-Col Grid) {:span 4 :style {:position "absolute" :top 0 :right 0 :width "34vw"}}
          [:> Stack {:align "stretch" :h "100%" :justify "space-between"}
         
           [:h1 "Account"] 
           [:f> connect-kit]

           ;(str guild-data)

       
           [:h1 "Skills"] 
           [:> SimpleGrid {:cols 3 :style {:height "50vh"}}
          
            (when (= status "connected")
            (for [{:keys [totalPoints rank guildPlatformId]} (keep #(when (= (:guildId %) 67432) %) points)]
              [:div
               [:> Badge {:size "xl"} 
                (str totalPoints " ")
                (case guildPlatformId
                  34073 "XP"
                  34177 "Bounty"
                  34189 "Loyalty"
                  34178 "WHALE"
                  34376 "HUMAN"
                  34198 "PLAY"
                  "??")
                ]]
              ))
           
           ] 
           
           ]]]))
