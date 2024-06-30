
(ns main.component
  (:require [main.maps :refer [maps world]]
            [main.models :refer [loading avatars] :as model]
            [main.wagmi :refer [connect-kit]]
            [main.guild :refer [join-guild]]
    
            ["@mantine/core" :refer [AppShell Modal Avatar Badge Burger Button createTheme Group Center SimpleGrid SimpleGrid Grid Container Flex Stack Skeleton]]
            ["@mantine/hooks" :refer [useDisclosure]]
            ["@tabler/icons-react" :as icon]

            ["react" :as react]
            [re-frame.core :as rf :refer [subscribe dispatch reg-fx]]
            
            ["wagmi" :as wagmi]
            
            ["three" :as three]
            ;["three/addons/renderers/webgpu/WebGPURenderer.js" :refer [WebGPURenderer]]
            ["@react-three/fiber" :as fiber]
            ["@react-three/drei" :as drei]
            ["@react-three/rapier" :as rapier]
            ["ecctrl" :refer [Controller] :as ecc]
            ["howler" :refer [Howl]]
            
            ["@guildxyz/sdk" :as guild]
            
            ["../ecmascript/threejs" :refer [Box]]
            ))

(set! *warn-on-infer* false)

(defonce debug? false)

(defn record-player-movement [wrapper {:keys [nickname avatar located x y z qx qy qz qw]}]
  (fn [state]
    (let [position (.getWorldPosition (.-current wrapper) (new three/Vector3))
          nx (.toFixed (.parseFloat js/Number (.-x position)) 3)
          ny (.toFixed (.parseFloat js/Number (.-y position)) 2)
          nz (.toFixed (.parseFloat js/Number (.-z position)) 3)
          quaternion (.getWorldQuaternion (.-current wrapper) (new three/Quaternion))
          nqx (.-x quaternion)
          nqy (.-y quaternion)
          nqz (.-z quaternion)
          nqw (.-w quaternion)
          new-state {:nickname nickname :avatar avatar :located located :x nx :y ny :z nz :qx nqx :qy nqy :qz nqz :qw nqw}]
      (when
        (and
          nickname located
          (or
            (not= nx x) (not= ny y) (not= nz z)
            (not= nqx qx) (not= nqy qy) (not= nqz qz) (not= nqw qw)))
        (do 
          (dispatch [:assoc-in [:player] new-state])
          (dispatch [:send {:id "movement" :player new-state}]))))))

(defn player []
  (react/useEffect (fn [] (js/console.log "Player component rendered.."))) 
  (let [
        {:keys [nickname avatar] :as player} @(subscribe [:get-in [:player]])
        avatar @(subscribe [:get-in [:player :avatar]])
        nickname @(subscribe [:get-in [:player :nickname]])
        wrapper (react/useRef)
        ]
    (fiber/useFrame (record-player-movement wrapper player))
    [:group {:ref wrapper}
     [model/player
      {:nickname nickname
       :avatar avatar}]]))

(defn other-players []
  (let [{:keys [nickname located]} @(subscribe [:get-in [:player]])
        players @(subscribe [:get-in [:players located]])]
    [:<>
     (for [[other-player data] (dissoc players nickname)
           ]
       (let [{:keys [nickname avatar x y z qx qy qz qw]} data]
         [:group {:key (str other-player"-group")
                  :position [x y z]}
           [model/player
            {:key nickname
             :nickname nickname
             :avatar avatar
             :quaternion [qx qy qz qw]}]]))]))

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
  ;(react/useEffect (fn [] (js/console.log "Canvas component rendered.."))) 
  (let [
        _ @(subscribe [:get-in [:update-physics?]])
        springK @(subscribe [:get-in [:player :avatar :springK]])
        dampingC @(subscribe [:get-in [:player :avatar :dampingC]])
        environment-map @(subscribe [:get-in [:player :located]])
        {:keys [dynamic-environment skybox lights gltf ost control objects]} (get world environment-map);(get maps environment-map)
        music-play (when ost (new Howl #js {:src (clj->js ost) :loop true :autoplay true}))
        ]  
    [:> fiber/Canvas {:key environment-map 
                      :name environment-map 
                      :shadows "shadows" 
                      :onPointerDown (fn [e] (.requestPointerLock (.-target e)))
                      ;:gl (fn [canvas] (let [r (new WebGPURenderer {:canvas canvas})] r))
				;r.xr = {
			;		addEventListener: () => {},
		;			removeEventListener: () => {},
		;		}
                      }
     [:> react/Suspense {:fallback (reagent.core/as-element [loading])}
        skybox
        lights

        (when dynamic-environment [dynamic-environment])

        [:> rapier/Physics {:timeStep "vary" :debug (if debug? "debug" nil)}
          
         (when gltf [:> rapier/RigidBody {:colliders "trimesh" :type "fixed"} [:> drei/Gltf gltf]])
         
         [:> drei/KeyboardControls {:map keyboard-controls :debug? (if debug? true false) :debug "debug"}
          [:> ecc/default 
           (assoc control
                  ;:animated "animated"
                  :debug debug?
                  ;:followLight true
                  :springK (or springK 1.2)
                  :dampingC (or dampingC 0.08)
                  :controllerKeys {:forward 12 :backward 13 :leftward 14 :rightward 15 :jump 2}
                  )
           [:f> player]
           ]
          ]
         objects
         ]
        
        [other-players] 
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
  ;(react/useEffect (fn [] (js/console.log "Lobby component rendered.."))) 
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


(defn status-panel []
  (let [
        nickname @(subscribe [:get-in [:player :nickname]])
        located @(subscribe [:get-in [:player :located]])
        others @(subscribe [:get-in [:players located]])
        {:keys [nickname avatar] :as player} @(subscribe [:get-in [:player]])
        live-players-count (inc (count others))
        north [(inc (first located)) (second located)]
        west [(first located) (dec (second located))]
        east [(first located) (inc (second located))]
        south [(dec (first located)) (second located)]
        north-west [(dec (first located)) (inc (second located))]
        north-east [(inc (first located)) (inc (second located))]
        south-west [(dec (first located)) (dec (second located))]
        south-east [(dec (first located)) (inc (second located))]
        ]
    [:div
     [:p "Coordinates: "(str located)]
     [:p (cond (= 1 live-players-count) "Only you are online."
           :else (str live-players-count" players are online."))]
     ;[:p (str player)]
     [:> SimpleGrid {:cols 3}
      (for [direction [north-west north north-east west located east south-west south south-east]]
        (let [title (get-in world [direction :title])]
          [:> Center {:key (rand)} 
           (if title title "...")]
          )
        )
      ]
     [:div {:style {:display "grid" :grid-template-areas "\". north .\" \"west . east\"\". south .\""}}
     [:> Button {:onClick #(when (get world north) (dispatch [:assoc-in [:player :located] north]))
                 :style {:grid-area "north"}
                 :data-disabled (when-not (get world north) true)} "Go North"]
     [:> Button {:onClick #(when (get world west) (dispatch [:assoc-in [:player :located] west]))
                 :style {:grid-area "west"}
                 :data-disabled (when-not (get world west) true)} "Go West"]
     [:> Button {:onClick #(when (get world east) (dispatch [:assoc-in [:player :located] east]))
                 :style {:grid-area "east"}
                 :data-disabled (when-not (get world east) true)} "Go East"]
     [:> Button {:onClick #(when (get world south) (dispatch [:assoc-in [:player :located] south]))
                 :style {:grid-area "south"}
                 :data-disabled (when-not (get world south) true)} "Go South"]
      ]
     ]))

(defn main []
  (let [{:keys [address status chain chainId]} (js->clj (wagmi/useAccount) :keywordize-keys true)
        connect (.-connect (wagmi/useConnect))
        disconnect (.-disconnect (wagmi/useDisconnect))
        connectors (.-connectors (wagmi/useConnect))
        signer (.-signMessageAsync (wagmi/useSignMessage))
        tos? @(subscribe [:get-in [:tos?]])
        guild-data @(subscribe [:get-in [:guilds]])
        points @(subscribe [:get-in [:guilds :points]])
        address-a @(subscribe [:get-in [:address]])
        located @(subscribe [:get-in [:player :located]])
        avatar @(subscribe [:get-in [:player :avatar :name]])
        ;{:keys [located x y z qx qy qz qw]} @(subscribe [:get-in [:player]])
        ]
    (react/useEffect
      (fn []
        (dispatch [:assoc-in [:status] status])
        (when (= status "connected") (.stop lobby-music))
        (dispatch [:assoc-in [:chain] chain])
        ;(dispatch [:assoc-in [:player :located] (:name chain)])
        (when-not located (dispatch [:assoc-in [:player :located] [0 0]]))
        (js/console.log "Main component rendered.."))) 
    [:> Grid
     [:> (.-Col Grid) {:span 8 :style {:position "fixed" :top 0 :left 0 :width "70vw" :background "black" :height "100vh" :padding 0}}

          ;[canvas-test]
          (case status
            "connected" [:f> canvas];(if guild-data [canvas] [lobby])
            "disconnected" [:f> lobby]
            "connecting" [:f> lobby]
            [:div "Sign-in First!"])

          ]
         [:> (.-Col Grid) {:span 4 :style {:position "fixed" :top 0 :right 0 :width "34vw"}}
          
          [:> Modal {:opened (and (not= status "connected") (not tos?))
                     :onClose (fn []
                                (.play lobby-music)
                                (dispatch [:assoc-in [:tos?] true])) :title "Disclaimer" :centered "centered"}
           [:p "Adventure.io is an experimental onchain game engine made by the team at "[:a {:href "https://guild.xyz" :target "_blank"} "Guild.xyz"]]]
          
          [:> Stack {:align "stretch" :h "100%" :justify "space-between"}
         
           [:f> status-panel]
           ;(when debug? 
             ;[:h4 "Position: x:"x" y:"y" z:"z]
             ;)
           ;[:> Button {:onClick #(dispatch [:send {:id "llama3" :message {:role "user" :content "Are you ready to play?"}}])} "Ask Llama3"]
           ;[:> Button {:onClick #(dispatch [:assoc-in [:update-physics?] (rand-int 100000)])} "Update Physics"]
           ;[:h4 "Quaternion: x:"qx" y:"qy" z:"qz" w:"qw]
           [:f> connect-kit]
           (when (and (= status "connected") (nil? guild-data)) [:> Button {:onClick #(join-guild)} "Join the Onchain guild"])
           (when (= status "connected")
             (case avatar
              :wizard [:> Button {:onClick #(dispatch [:assoc-in [:player :avatar] (:ghost avatars)])} "Turn into a Ghost"]
              :ghost [:> Button {:onClick #(dispatch [:assoc-in [:player :avatar] (:wizard avatars)])} "Become a Wizard again"]
              nil))
           ;(str guild-data)
       
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
