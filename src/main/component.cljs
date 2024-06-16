
(ns main.component
  (:require [main.maps :refer [maps]]
            [main.models :refer [loading player-model]]
            [main.wagmi :refer [connect-kit]]
    
            ["@mantine/core" :refer [AppShell Avatar Badge Burger Button createTheme Group SimpleGrid Grid Container Flex Stack Skeleton]]
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
            ))

(defonce debug? true)

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

(def keyboard-controls
  [{:keys ["ArrowUp" "KeyW"] :name "forward"}
   {:keys ["ArrowDown" "KeyS"] :name "backward"}
   {:keys ["ArrowLeft" "KeyA"] :name "leftward"}
   {:keys ["ArrowRight" "KeyD"] :name "rightward"}
   {:keys ["Space"] :name "jump"}
   {:keys ["Shift"] :name "run"}])

(defn canvas []
  (let [
        environment-map @(subscribe [:get-in [:environment :map]])
        {:keys [skybox lights gltf control]} (get maps environment-map)
        ]  
    [:> fiber/Canvas {:key environment-map :name environment-map :shadows "shadows" :onPointerDown (fn [e] (.requestPointerLock (.-target e)))}
     [:> react/Suspense {:fallback (reagent.core/as-element [loading])}
      ;[:> drei/Fisheye {:zoom 0.4} 
        skybox
        lights

        [other-players] 

        ;[other-player]     

        [:> rapier/Physics {:timeStep "vary" :debug (if debug? "debug" nil)}
        
         [:> drei/KeyboardControls {:map keyboard-controls :debug? (if debug? true false) :debug "debug"}
          ;[:> ecc/default (assoc control :debug debug?)
           [:f> player]
          ; ]
          ]
         
         [:> rapier/RigidBody {:colliders "trimesh" :type "fixed"} [:> drei/Gltf gltf]]
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
         [:> (.-Col Grid) {:span 8 :style {:position "fixed" :top 0 :left 0 :width "70vw" :background "black" :height "100vh" :padding 0}}

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
           (when debug? [:h4 "Position: x:"x" y:"y" z:"z])
           ;[:> Button {:onClick #(dispatch [:send {:id "llama3" :message {:role "user" :content "Are you ready to play?"}}])} "Ask Llama3"]
           ;[:h4 "Quaternion: x:"qx" y:"qy" z:"qz" w:"qw]
           [:f> connect-kit]
           ;(str guild-data)
           (when debug? (str players))
       
       
           ;[:h1 "Skills"] 
           [:> SimpleGrid {:cols 3 :style {:height "50vh"}}
          
            (when false ;(= status "connected")
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
