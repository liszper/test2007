
(ns main.component
  (:require [main.maps :refer [maps]]
            [main.models :refer [loading player-model] :as model]
            [main.wagmi :refer [connect-kit]]
            [main.guild :refer [join-guild]]
    
            ["@mantine/core" :refer [AppShell Modal Avatar Badge Burger Button createTheme Group SimpleGrid Grid Container Flex Stack Skeleton]]
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
            ["howler" :refer [Howl]]
            
            ["@guildxyz/sdk" :as guild]
            
            ["../ecmascript/threejs" :refer [Box]]
            ))

(defonce debug? false)

(defn player []
  (let [{:keys [x y z]} @(subscribe [:get-in [:player :position]])
        {:keys [qx qy qz qw]} @(subscribe [:get-in [:player :quaternion]])
        
        environment-map @(subscribe [:get-in [:environment :map]])
        player-name @(subscribe [:get-in [:player :name]])
        
        wrapper (react/useRef)
        
        _ (fiber/useFrame
            (fn [state]
              (let [position (.getWorldPosition (.-current wrapper) (new three/Vector3))
                    nx (.toFixed (.parseFloat js/Number (.-x position)) 3)
                    ny (.toFixed (.parseFloat js/Number (.-y position)) 2)
                    nz (.toFixed (.parseFloat js/Number (.-z position)) 3)
                    quaternion (.getWorldQuaternion (.-current wrapper) (new three/Quaternion))
                    nqx (.toFixed (.parseFloat js/Number (.-x quaternion)) 3)
                    nqy (.toFixed (.parseFloat js/Number (.-y quaternion)) 3)
                    nqz (.toFixed (.parseFloat js/Number (.-z quaternion)) 3)
                    nqw (.toFixed (.parseFloat js/Number (.-w quaternion)) 3)
                    ]
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
           {:key player 
            :nickname (str player quaternion)
            :position [0 -0.55 0]
            :quaternion [qx qy qz qw]}]]   
         ))]))

(def animation-set
   {:idle "Idle",
    :walk "Walk",
    :run "Run",
    :jump "Jump_Start",
    :jumpIdle "Jump_Idle",
    :jumpLand "Jump_Land",
    :fall "Climbing",
    :action1 "Wave",
    :action2 "Dance",
    :action3 "Cheer",
    :action4 "Attack(1h)"})

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
        {:keys [dynamic-environment skybox lights gltf ost control objects]} (get maps environment-map)
        music-play (when ost (new Howl #js {:src (clj->js ost) :loop true :autoplay true}))
        ]  
    [:> fiber/Canvas {:key environment-map :name environment-map :shadows "shadows" :onPointerDown (fn [e] (.requestPointerLock (.-target e)))}
     [:> react/Suspense {:fallback (reagent.core/as-element [loading])}
        skybox
        lights

        (when dynamic-environment [dynamic-environment])

        [other-players] 

        [:> rapier/Physics {:timeStep "vary" :debug (if debug? "debug" nil)}
         (when gltf [:> rapier/RigidBody {:colliders "trimesh" :type "fixed"} [:> drei/Gltf gltf]])
         [:> drei/KeyboardControls {:map keyboard-controls :debug? (if debug? true false) :debug "debug"}
          [:> ecc/default 
           (assoc control
                  ;:animated "animated"
                  :debug debug?
                  ;:followLight true
                  ;:springK 1.4
                  :controllerKeys {:forward 12 :backward 13 :leftward 14 :rightward 15 :jump 2}
                  )
           [:f> player]
           ]
          ]
         objects
         ]
      ]
     
     [:> drei/Stats]
     ]))

(defonce lobby-music
  (new Howl #js {:src #js ["/ost/through_the_ether.mp3"]
                 :loop true
                 :html5 true
                 ;:autoplay true
                 }))

(defn lobby []
  (react/useEffect
    (fn [] 
      (js/console.log "Lobby component rendered..")
      )) 
  [:> fiber/Canvas {:shadows "shadows"}
   [:> react/Suspense {:fallback (reagent.core/as-element [loading])}
   [:> rapier/Physics {:timeStep "vary" :debug (if debug? "debug" nil)}
    [:ambientLight {:intensity 3}]
      
   ;[:> Box {:position [0 0 0]}]
   ;[:> Box {:position [1.2 0 0]}]

     [:> drei/Stars]
   [:> drei/Gltf
    {:castShadow "castShadow"
     :receiveShadow "receiveShadow"
     :position [0 0 -100]
     :scale 30
     :src "/npc/ethereum_logo.glb"}]]

   [:> drei/Clouds {:material three/MeshBasicMaterial}
    [:> drei/Cloud {:seed 10 :bounds 50 :volume 80 :position [0 0 0] :speed 0.1}]
    [:> drei/Cloud {:seed 1 :bounds 50 :volume 80 :position [-80 10 -80] :speed 0.05}]
    ]

   ]]
  )

(defn main []
  (let [{:keys [address status chain chainId]} (js->clj (wagmi/useAccount) :keywordize-keys true)
        _ (dispatch [:assoc-in [:status] status])
        _ (when (= status "connected") (.stop lobby-music))
        _ (dispatch [:assoc-in [:chain] chain])
        _ (dispatch [:assoc-in [:environment :map] (:name chain)])
        connect (.-connect (wagmi/useConnect))
        disconnect (.-disconnect (wagmi/useDisconnect))
        connectors (.-connectors (wagmi/useConnect))
        signer (.-signMessageAsync (wagmi/useSignMessage))
        tos? @(subscribe [:get-in [:tos?]])
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
            "connected" [canvas];(if guild-data [canvas] [lobby])
            "disconnected" [:f> lobby]
            "connecting" [:f> lobby]
            [:div "Sign-in First!"])

          ]
         [:> (.-Col Grid) {:span 4 :style {:position "absolute" :top 0 :right 0 :width "34vw"}}
          
          [:> Modal {:opened (and (not= status "connected") (not tos?))
                     :onClose (fn []
                                (.play lobby-music)
                                (dispatch [:assoc-in [:tos?] true])) :title "Disclaimer" :centered "centered"}
           [:p "Adventure.io is an experimental onchain game engine made by the team at "[:a {:href "https://guild.xyz" :target "_blank"} "Guild.xyz"]]]
          
          [:> Stack {:align "stretch" :h "100%" :justify "space-between"}
         
           ;[:h1 (get chain :name)] 
           ;(when debug? 
             ;[:h4 "Position: x:"x" y:"y" z:"z]
             ;)
           ;[:> Button {:onClick #(dispatch [:send {:id "llama3" :message {:role "user" :content "Are you ready to play?"}}])} "Ask Llama3"]
           ;[:h4 "Quaternion: x:"qx" y:"qy" z:"qz" w:"qw]
           [:f> connect-kit]
           (when (and (= status "connected") (nil? guild-data)) [:> Button {:onClick #(join-guild)} "Join the Onchain guild"])
           ;(str guild-data)
           (when debug? (str players))
       
       
           (when (and (= status "connected") guild-data) [:h1 "Skills"])
           [:> SimpleGrid {:cols 3 :style {:height "50vh"}}
          
            (when (and (= status "connected") points)
            (for [{:keys [totalPoints rank guildPlatformId]} (keep #(when (= (:guildId %) 67432) %) points)]
              [:div {:key (str "point" guildPlatformId)}
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
