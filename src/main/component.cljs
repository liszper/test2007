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

(defonce Guild (guild/createGuildClient "test"))
(defonce user-client (.-user Guild))
(defonce guild-client (.-guild Guild))
(defonce role-client (.-role Guild))
(defonce reward-client (.-reward Guild))
(defonce admin-client (.-admin Guild))

(defn logged-in? [{:keys [address status guilds]}] 
  (and address (= status "connected") 
       (not guilds)
       ))

(defonce do-timer 
  (js/setInterval

    (fn []
       
      (when false
         
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
        player-name (or (:data ens-name) address)
        _ (dispatch [:assoc-in [:player :name] player-name])
        disconnect (.-disconnect (wagmi/useDisconnect))
        connectors (.-connectors (wagmi/useConnect))
        signer (.-signMessageAsync (wagmi/useSignMessage))]    
    
    (if address
     
      [:> Flex {:gap "sm"}
       ;[:p (str (js->clj guild-client))]
       ;player-name 
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


(def maps
  {
   "Base"
   {:gltf 
    {:castShadow "castShadow" 
     :receiveShadow "receiveShadow"
     :position [0 0 0]
     ;:rotation [(/ (- (.-PI js/Math)) 2) 0 0]
     :scale 0.6
     :src "/maps/twin_peaks_black_lodge.glb"}
    :rigid-body {:colliders "trimesh" :type "fixed"}
    :physics {:timeStep "vary"}
    :control {:maxVelLimit 5
              :sprintMult 4
              :jumpVel 8
              ;:airDragMultiplier 0.1
              ;:fallingGravityScale 10
              :camInitDis -3}
    :skybox [:> drei/Stars]
    :lights [:<>
       [:directionalLight {:intensity 1 :castShadow "castShadow" :position [0 10 0] :shadow-bias -0.0004}
        [:orthographicCamera {:args [-20 20 20 -20] :attach "shadow-camera"}]]
       [:ambientLight {:intensity 0.5}]
   [:spotLight {:angle 0.15 :decay 0 :intensity (.-PI js/Math) :penumbra 1 :position [10 10 10]}]
   [:pointLight {:decay 0 :intensity (.-PI js/Math) :position [(- 10) (- 10) (- 10)]}]
             
             
             ]
    }
   
   "Ethereum"
   {:gltf 
    {:castShadow "castShadow" 
     :receiveShadow "receiveShadow"
     :position [0 0 -30]
     ;:rotation [(/ (- (.-PI js/Math)) 2) 0 0]
     :scale 1
     :src "/maps/desert_sector.glb"}
    :rigid-body {:colliders "trimesh" :type "fixed"}
    :physics {:timeStep "vary"}
    :control {:maxVelLimit 5
              :sprintMult 2
              :jumpVel 4
              ;:airDragMultiplier 0.1
              ;:fallingGravityScale 10
              :camInitDis -3}
    ;:skybox [:> drei/Stars]
    ;:skybox [:> drei/Environment {:files "/psy.hdr" :ground {:scale 100}}]
    :lights [:<>
       ;[:directionalLight {:intensity 1 :castShadow "castShadow" :position [0 10 0] :shadow-bias -0.0004}
       ; [:orthographicCamera {:args [-20 20 20 -20] :attach "shadow-camera"}]]
       [:ambientLight {:intensity 3}]
             
             
             ]
    }

   "Polygon"
   {:gltf 
    {:castShadow "castShadow" 
     :receiveShadow "receiveShadow"
     :position [0 -30 -30]
     ;:rotation [(/ (- (.-PI js/Math)) 2) 0 0]
     :scale 10
     :src "/maps/small_cybertown.glb"}
    :rigid-body {:colliders "trimesh" :type "fixed"}
    :physics {:timeStep "vary"}
    :control {:maxVelLimit 5
              :sprintMult 2
              :jumpVel 9
              ;:airDragMultiplier 0.1
              ;:fallingGravityScale 10
              :camInitDis -3}
    ;:skybox [:> drei/Stars]
    ;:skybox [:> drei/Environment {:files "/psy.hdr" :ground {:scale 100}}]
    :lights [:<>
       [:directionalLight {:intensity 1 :castShadow "castShadow" :position [0 10 0] :shadow-bias -0.0004}
        [:orthographicCamera {:args [-20 20 20 -20] :attach "shadow-camera"}]]
       [:ambientLight {:intensity 30}]
             
             
             ]
    }

   "OP Mainnet"
{:gltf 
    {:castShadow "castShadow" 
     :receiveShadow "receiveShadow"
     :position [10 -20 0]
     ;:rotation [(/ (- (.-PI js/Math)) 2) 0 0]
     :scale 1
     :src "/maps/magical_manor.glb"}
    :rigid-body {:colliders "trimesh" :type "fixed"}
    :physics {:timeStep "vary"}
    :control {:maxVelLimit 5
              :sprintMult 2
              :jumpVel 4
              ;:airDragMultiplier 0.1
              ;:fallingGravityScale 10
              :camInitDis -3}
    ;:skybox [:> drei/Stars]
    ;:skybox [:> drei/Environment {:files "/psy.hdr" :ground {:scale 100}}]
    :lights [:<>
       ;[:directionalLight {:intensity 1 :castShadow "castShadow" :position [0 10 0] :shadow-bias -0.0004}
       ; [:orthographicCamera {:args [-20 20 20 -20] :attach "shadow-camera"}]]
       [:ambientLight {:intensity 3}]
             
             
             ]
    }

   })

(defn loading []
  [:> Box {:position [(- 3.6) 0 0]}])

(defn player-model [{:keys [nickname position quaternion] :as p}]
  [:<>
   [:> drei/Html {
                  :key (str "html-player"(:key p))
                  :style {:transform "translate(-50%, 0)"} :position [0 1 0]}
    [:div {
           :key (str "nickname-player"(:key p))
           :style {:WebkitTextStroke "0.1rem #fff"}} nickname]] 
   [:> drei/Gltf
    {
     :key (str "gltf-player"(:key p))
     :castShadow "castShadow"
     :receiveShadow "receiveShadow"
     :position position
     :quaternion quaternion
     :scale 0.315
     :src "/ghost_w_tophat-transformed.glb"}]])

(defn player []
  (let [{:keys [x y z]} @(subscribe [:get-in [:player :position]])
        {:keys [qx qy qz qw]} @(subscribe [:get-in [:player :quaternion]])
        
        environment-map @(subscribe [:get-in [:environment :map]])
        player-name @(subscribe [:get-in [:player :name]])
        
        wrapper (react/useRef)
        
        _ (fiber/useFrame
            (fn [state]
              (let [position (.getWorldPosition (.-current wrapper) (new three/Vector3))
                    nx (.toPrecision (.-x position) 5)
                    ny (.toPrecision (.-y position) 3)
                    nz (.toPrecision (.-z position) 5)
                    quaternion (.getWorldQuaternion (.-current wrapper) (new three/Quaternion))
                    nqx (.-x quaternion)
                    nqy (.-y quaternion)
                    nqz (.-z quaternion)
                    nqw (.-w quaternion)]
                (when-not (= nx x) (dispatch [:assoc-in [:player :position :x] nx]))
                (when-not (= ny y) (dispatch [:assoc-in [:player :position :y] ny]))
                (when-not (= nz z) (dispatch [:assoc-in [:player :position :z] nz]))
                (when-not (= nqx qx) (dispatch [:assoc-in [:player :quaternion :qx] nqx]))
                (when-not (= nqy qy) (dispatch [:assoc-in [:player :quaternion :qy] nqy]))
                (when-not (= nqz qz) (dispatch [:assoc-in [:player :quaternion :qz] nqz]))
                (when-not (= nqw qw) (dispatch [:assoc-in [:player :quaternion :qw] nqw]))
                (when
                  (and
                    (or
                    (not= nx x)
                    (not= ny y)
                    (not= nz z)
                    (not= nqx qx)
                    (not= nqy qy)
                    (not= nqz qz)
                    (not= nqw qw)
                    )
                    player-name
                    environment-map)
                  (dispatch [:send {:id "movement"
                                    :player {:name player-name :located environment-map}
                                    :position {:x nx :y ny :z nz}
                                    :quaternion {:qx nqx :qy nqy :qz nqz :qw nqw}}]))
               )))
        ]
    [:group {:ref wrapper}
     [player-model
      {:nickname player-name
       :position [0 -0.55 0]
       :quaternion [0 0 0 0]}]]))

(defn other-player []
  (let [{:keys [x y z]} @(subscribe [:get-in [:player :position]])
        {:keys [qx qy qz qw]} @(subscribe [:get-in [:player :quaternion]])]
    [:group {:position [z y x]}
     [player-model
      {:nickname "NPC"
       :position [0 -0.55 0]
       :quaternion [qx qy qz qw]}]]))

(defn other-players []
  (let [
        environment-map @(subscribe [:get-in [:environment :map]])
        player-name @(subscribe [:get-in [:player :name]])
        players @(subscribe [:get-in [:players environment-map]])
        ]
    [:<>
     (for [[player data] (dissoc players player-name)
           ]
       (let [{:keys [position quaternion]} data
             {:keys [x y z]} position
             {:keys [qx qy qz qw]} quaternion]
         [:group {:key (str player"-group")
                  :position [x y z]}
          [player-model
           {:key (str player"-model-in"environment-map)
            :nickname player
            :position [0 -0.55 0]
            :quaternion [qx qy qz qw]}]]   
         ))]))

(defn player-control []
  (let [
        environment-map @(subscribe [:get-in [:environment :map]])
        keyboard-controls
        [{:keys ["ArrowUp" "KeyW"] :name "forward"}
         {:keys ["ArrowDown" "KeyS"] :name "backward"}
         {:keys ["ArrowLeft" "KeyA"] :name "leftward"}
         {:keys ["ArrowRight" "KeyD"] :name "rightward"}
         {:keys ["Space"] :name "jump"}
         {:keys ["Shift"] :name "run"}]
        ]
       [:> drei/KeyboardControls {:map keyboard-controls}
        [:> ecc/default (get-in maps [environment-map :control]) 
         [:f> player]]
      ]
    )
  )


(defn dragons []
  [:<>

[:> drei/Gltf
    {:castShadow "castShadow"
     :receiveShadow "receiveShadow"
     :position [-100 10 0]
     :scale 50
     :src "/npc/mega_wyvern.glb"}]
   
[:> drei/Gltf
    {:castShadow "castShadow"
     :receiveShadow "receiveShadow"
     :position [10 -1 0]
     :scale 3
     :src "/npc/black_dragon_with_idle_animation.glb"}]
   ])

(defn canvas []
  (let [
        environment-map @(subscribe [:get-in [:environment :map]])
        {:keys [skybox lights physics rigid-body gltf]} (get maps environment-map)
        ]  
    [:> fiber/Canvas {:key environment-map :name environment-map :shadows "shadows" :onPointerDown (fn [e] (.requestPointerLock (.-target e)))}
     [:> react/Suspense {:fallback (reagent.core/as-element [loading])}
      ;[:> drei/Fisheye {:zoom 0.4} 
        skybox
        lights
       
        [other-players]      

        [:> rapier/Physics physics
        
         [player-control]
         
         [:> rapier/RigidBody rigid-body [:> drei/Gltf gltf]]
        ]
       ;]
      ]]))

(defn lobby []
  ;(react/useEffect
  ;  (fn [] 
  ;    (js/console.log "Test component rendered..")
  ;    )) 
  [:> fiber/Canvas
   [:ambientLight {:intensity (/ (.-PI js/Math) 2)}]
      
   [:> Box {:position [(- 1.2) 0 0]}]
   [:> Box {:position [1.2 0 0]}]
   ]
  )

(defn main []
  ;(react/useEffect
  ;  (fn [] 
  ;    (js/console.log "Main component rendered..")
  ;    )) 
  (let [{:keys [address status chain chainId]} (js->clj (wagmi/useAccount) :keywordize-keys true)
        _ (dispatch [:assoc-in [:status] status])
        _ (dispatch [:assoc-in [:chain] chain])
        _ (dispatch [:assoc-in [:environment :map] (:name chain)])
        connect (.-connect (wagmi/useConnect))
        disconnect (.-disconnect (wagmi/useDisconnect))
        connectors (.-connectors (wagmi/useConnect))
        signer (.-signMessageAsync (wagmi/useSignMessage))
        players @(subscribe [:get-in [:players]])
        guild-data @(subscribe [:get-in [:guilds]])
        points @(subscribe [:get-in [:guilds :points]])
        address-a @(subscribe [:get-in [:address]])
        environment-map @(subscribe [:get-in [:environment :map]])
        {:keys [x y z]} @(subscribe [:get-in [:player :position]])
        {:keys [qx qy qz qw]} @(subscribe [:get-in [:player :quaternion]])
        ]
        [:> Grid
         [:> (.-Col Grid) {:span 8 :style {:position "absolute" :top 0 :left 0 :width "70vw" :background "black" :height "100vh" :padding 0}}

          ;[canvas-test]
          (case status
            "connected" [canvas] 
            "disconnected" [:f> lobby]
            "connecting" [:f> lobby]
            [:div "Sign-in First!"])

          ]
         [:> (.-Col Grid) {:span 4 :style {:position "absolute" :top 0 :right 0 :width "34vw"}}
          [:> Stack {:align "stretch" :h "100%" :justify "space-between"}
         
           ;[:h1 "Account"] 
           ;[:h1 (get chain :name)] 
           ;[:h4 "Position: x:"x" y:"y" z:"z]
           ;[:h4 "Quaternion: x:"qx" y:"qy" z:"qz" w:"qw]
           [:f> connect-kit]
           ;(str guild-data)
           ;(str players)
       
           ;[:h1 "Skills"] 
           [:> SimpleGrid {:cols 3 :style {:height "50vh"}}
          
            (when false;(= status "connected")
            (for [{:keys [totalPoints rank guildPlatformId]} (keep #(when (= (:guildId %) 67432) %) points)]
              [:div
               [:> Badge {:size "xl"} 
                (str totalPoints " ")]
               [:> Badge {:size "xl"} 
                (case guildPlatformId
                  34073 "Experience"
                  34177 "Dedication"
                  34189 "Loyalty"
                  34178 "Whale"
                  34376 "Human"
                  34198 "Player"
                  36462 "Supporter"
                  "??")
                ]]
              ))
           
           ] 
           
           ]]]))
