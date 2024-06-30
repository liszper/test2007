
(ns main.maps
  (:require 

            [re-frame.core :as rf :refer [subscribe dispatch reg-fx]]
            ["three" :as three]
            ["@react-three/drei" :as drei]
            ["@react-three/rapier" :as rapier]
            ["prando" :refer [default]]
            [main.models :as model]
            
            ;["../ecmascript/threejs" :refer [Box Instances]]
    ))

(defn random [seed] (-> seed js/Math.sin (* 10000) js/Math.floor))
(def prando (new default "new2"))
(defn prand-int [x y] (.nextInt prando x y))
(defn prand [x y] (.next prando x y))


(def maps
  {
   "Base"
   {
    :ost ["/ost/base_coffee.mp3"] 
    :gltf 
    {:castShadow "castShadow" 
     :receiveShadow "receiveShadow"
     :position [0 0 0]
     ;:rotation [(/ (- (.-PI js/Math)) 2) 0 0]
     :scale 1
     :src "/maps/twin_peaks_black_lodge.glb"}
    :control {:maxVelLimit 5
              :sprintMult 4
              :jumpVel 8
              ;:airDragMultiplier 0.1
              ;:fallingGravityScale 10
              :camInitDis -3}
    :skybox [:> drei/Stars]
    :lights 
    [:<>
    
     ;[:directionalLight {:intensity 1 :castShadow "castShadow" :position [0 10 0] :shadow-bias -0.0004}
     ; [:orthographicCamera {:args [-20 20 20 -20] :attach "shadow-camera"}]]

     [:ambientLight {:intensity 0.3}]

     ;[:spotLight {:name "followLight" :angle 0.1 :decay 0 :intensity 5 :penumbra 1 :position [0 10 0]}]
  
     [:pointLight {:decay 0 :intensity 2 :position [100 50 -100]}]
     [:pointLight {:decay 0 :intensity 2 :position [-100 50 100]}]
     [:pointLight {:decay 0 :intensity 2 :position [100 50 100]}]
     [:pointLight {:decay 0 :intensity 2 :position [-100 50 -100]}]
     [:pointLight {:color "#00a" :decay 0.1 :intensity 4 :position [53 7.7 27]}]
             
             
             ]
    :objects 
    [:<>
             
     (model/object
       {:castShadow "castShadow"
        :receiveShadow "receiveShadow"
        :position [30 0 30]
        :scale 0.02
        :src "/objects/GuildDenver.glb"})
     ]
    }
   
   "OP Mainnet";"Ethereum"
   {
    :ost ["/ost/yellow_floating_sand_dune_world.mp3"] 
    :gltf 
    {:castShadow "castShadow" 
     :receiveShadow "receiveShadow"
     :position [0 -2 -30]
     ;:rotation [(/ (- (.-PI js/Math)) 2) 0 0]
     :scale 1
     :src "/maps/desert_sector.glb"}
    :control {:maxVelLimit 10
              :sprintMult 2
              :jumpVel 4
              ;:airDragMultiplier 0.1
              ;:fallingGravityScale 10
              :camInitDis -3}
    ;:skybox [:> drei/Environment {:files "/psy.hdr" :ground {:scale 100}}]
    :lights [:<>
       [:ambientLight {:intensity 3}]
             
             
             ]
    :objects [:<>
              ;[:fog {:attach "fog" :args #js ["#cc7b32" 10 500]}]

[:> drei/Gltf
    {:castShadow "castShadow"
     :receiveShadow "receiveShadow"
     :position [10 -1 0]
     :scale 3
     :src "/npc/black_dragon_with_idle_animation.glb"}]
[:> drei/Gltf
    {:castShadow "castShadow"
     :receiveShadow "receiveShadow"
     :position [-100 10 0]
     :scale 50
     :src "/npc/mega_wyvern.glb"}]
              ]
    }

   "Polygon"
   {:gltf 
    {:castShadow "castShadow" 
     :receiveShadow "receiveShadow"
     :position [0 0 0]
     ;:rotation [(/ (- (.-PI js/Math)) 2) 0 0]
     :scale 95
     :src "/maps/untitled.glb"} 
    :control {:maxVelLimit 2
              :sprintMult 3
              :jumpVel 10
              ;:airDragMultiplier 0.1
              ;:fallingGravityScale 10
              :camInitDis -3}
    ;:skybox [:> drei/Stars]
    ;:skybox [:> drei/Environment {:files "/psy.hdr" :ground {:scale 100}}]
    :lights [:<>
       ;[:directionalLight {:intensity 1 :castShadow "castShadow" :position [0 10 0] :shadow-bias -0.0004}
       ; [:orthographicCamera {:args [-20 20 20 -20] :attach "shadow-camera"}]]
     ;  [:ambientLight {:intensity 100}]
     [:pointLight {:decay 0 :intensity 2 :position [100 10 -100]}]
     [:pointLight {:decay 0 :intensity 2 :position [-100 10 100]}]
     [:pointLight {:decay 0 :intensity 2 :position [100 10 100]}]
     [:pointLight {:decay 0 :intensity 2 :position [-100 10 -100]}]
             
             
             ]
    }

   "Ethereum2"
{
    :ost ["/ost/mystery_funk.mp3"] 
    :control {:maxVelLimit 10
              :sprintMult 3
              :jumpVel 30
              ;:airDragMultiplier 0.1
              ;:fallingGravityScale 10
              :camInitDis -3}
    :dynamic-environment
    (fn [] 
      (let [
        
            {:keys [y]} @(subscribe [:get-in [:player :position]])

            ]
        [:<>
             [:> drei/Stars {:radius 600 :depth 100 :count 20000 :factor 15 :saturation 0 :fade "fade" :speed 0.01 :frustumCulled false :logarithmicDepthBuffer true}]
             ;[:> drei/Sky {:distance 450 :sunPosition [0 1 0] :inclination 0 :azimuth 0.25}]
             [:> drei/Sky {:turbidity 10 :rayleigh 3 :mieCoefficient 0.005 :mieDirectionalG 0.7 :elevation 21.4 :azimuth 180 :exposure 0.5}]
       
             ;[:ambientLight {:intensity 3}]
             [:hemisphereLight {:position [0 100 0] :color "#fff" :groundColor "#ddd"}]
             [:pointLight {:decay 0 :intensity 2 :position [0 300 0]}]
             (cond 
               (< y 100) [:fog {:attach "fog" :args #js ["#fff" 0.1 250]}]
               (< 100 y 200) [:fog {:attach "fog" :args #js ["#fff" 0.1 500]}]
               (< 200 y) [:fog {:attach "fog" :args #js ["#fff" 0.1 1000]}]
               :else nil)
             ;[:directionalLight {:decay 0 :intensity 3 :position [100 100 100] :castShadow "castShadow"}]
       
             ;[:directionalLight {:intensity 3 :castShadow "castShadow" :position [100 100 100] ;:shadow-bias -0.0004
             ;                    }
             ;   [:orthographicCamera {:args [-20 20 20 -20] :attach "shadow-camera" :near 10}]]
             
             ]     
        )
 
      )
    :objects [:<>

         [:> rapier/RigidBody {:colliders "trimesh" :type "fixed"} 
          [:> drei/Circle {:args [600 600] :position [0 -10 0] :rotation [(/ js/Math.PI 2 -1) 0 0]}
           [:meshStandardMaterial {:color "blue"
                                   }]          
           ]]

         (model/object
           {:position [0 -10 0]
            ;:rotation [(/ (- (.-PI js/Math)) 2) 0 0]
            :scale 1
            :src "/maps/low-poly_fantasy_island_medieval.glb"})
   


         (model/object
           {
            :position [5 -5.2 11]
            :scale 4
            :rotation [0 (/ js/Math.PI 2 -1) 0]
            :src "/npc/wizard.glb"})
         
         (for [
               {:keys [ii q gx gy gz]} 
               [
                {:ii 0 :q 500 :gx 600 :gy 500 :gz 600}
                ;{:ii 1 :q 500 :gx 300 :gy 5 :gz 300}
                {:ii 2 :q 500 :gx 300 :gy 50 :gz 300}
                {:ii 3 :q 500 :gx 300 :gy 250 :gz 300}
                {:ii 4 :q 1000 :gx 300 :gy 1000 :gz 300}
                ]
              ]
         (for [i (range q)]
           (let [x (- (prand-int 0 gx) (prand-int 0 gx))
                 y (prand-int 0 gy)
                 z (- (prand-int 0 gz) (prand-int 0 gz))
                 rx (* (/ (- (.-PI js/Math)) (prand-int 0 10)) (prand-int 0 10))
                 ry (* (/ (- (.-PI js/Math)) (prand-int 0 10)) (prand-int 0 10))
                 rz (* (/ (- (.-PI js/Math)) (prand-int 0 10)) (prand-int 0 10))
                 ;color (str "#" (prand-int 0 9) "d" (prand-int 0 9))
                 color (str "#" "f" (prand-int 0 9) (prand-int 0 9))
                 size [(inc (prand-int 0 50)) (inc (prand-int 0 40)) (inc (prand-int 0 50))]
                 ;size [(inc (prand-int 0 25)) (inc (prand-int 0 4)) (inc (prand-int 0 25))]
                 ;size [(+ 5 (rand-int 20)) (inc (rand-int 10)) (+ 5 (rand-int 20))]
                 ]
             (when-not (and (< -100 x 100) (< -100 y 100) (< -100 z 100))
               (model/platform {:i i :ii ii :x x :y y :z z :rx rx :ry ry :rz rz :color color :size size}))))
         )


             (let [x (- (prand-int 0 300) (prand-int 0 300))
                   y 1050
                   z (- (prand-int 0 300) (prand-int 0 300))
                   position [x y z]]
               [:group 
              
              (model/object 
                {:position [x y z]
                 :scale 3
                 :src "/npc/princess_felicity_fish_-_purple.glb"})
                [:> rapier/RigidBody {:key "winner-box" :colliders "trimesh" :type "fixed"} 
               [:> drei/Box {
                             :args [25 4 25]
                             :position position
                             :rotation [0 0 0]
                             }
                [:meshStandardMaterial {:color "#a00"}]]]



         (for [i (range 50)]
           (let [x (- (prand-int 0 600) (prand-int 0 600))
                 y (prand-int 0 1000)
                 z (- (prand-int 0 600) (prand-int 0 600))
                 ]
               (model/object {:key (str "castle"i) :index (str "floatingcastle"i) :position [x y z] :rotation [0 (rand 3) 0] :scale 5 :src "/objects/floating_castle.glb"})))
              
              ])


   [:> drei/Clouds {:material three/MeshBasicMaterial 
                    :frustumCulled false
                    :texture "/texture/cloud.png"
                    }
         (for [i (range 10)]
           (let [
                 x (- (prand-int 0 600) (prand-int 0 600))
                 y (+ (prand-int 0 200) 200)
                 z (- (prand-int 0 600) (prand-int 0 600))
                 bounds (+ (prand-int 0 100) 50)
                 volume (+ (prand-int 0 500) 100)
                 speed (prand 0 0.5)
                 ]
    [:> drei/Cloud {:key (str "cloud"i) :seed i :bounds bounds :volume volume :position [x y z] :speed speed :frustumCulled false}]
    ))]

              ]
    }
"Ethereum-solid-jumping"
{
    :ost ["/ost/In_the_depths_of_Farcaster.mp3"] 
    :control {
              :maxVelLimit 10 ; Maximum velocity limit
              :turnVelMultiplier 0.2 ; Turn velocity multiplier
              :turnSpeed 15 ; Turn speed
              :sprintMult 1.2 ; Sprint speed multiplier
              :jumpVel 8 ; Jump velocity
              :jumpForceToGroundMult 5 ; Jump force to ground object multiplier
              :slopJumpMult 0.25 ; Slope jump affect multiplier
              :sprintJumpMult 1.2 ; Sprint jump multiplier
              :airDragMultiplier 0.2 ; Air drag multiplier
              :dragDampingC 0.15 ; Drag damping coefficient
              :accDeltaTime 8 ; Acceleration delta time
              :rejectVelMult 4 ; Reject velocity multiplier
              :moveImpulsePointY 0.5 ; Move impulse point Y offset
              :camFollowMult 11 ; Camera follow speed multiplier
              :fallingGravityScale 2.5 ; Character is falling, apply higher gravity
              :fallingMaxVel -20 ; Limit character max falling velocity
              :wakeUpDelay 200 ; Wake up character delay time after window visibility change to visible
              ;Floating Ray setups
              :rayHitForgiveness 0.1 ; Ray hit forgiveness
              :rayDir {:x 0 :y -1 :z 0} ; Ray direction
              :dampingC 0.08 ; Damping coefficient
              ;Slope Ray setups
              :showSlopeRayOrigin false ; Show slope ray origin
              :slopeMaxAngle 1 ; in rad, the max walkable slope angle
              :slopeRayDir {:x 0 :y -1 :z 0} ; Slope ray direction
              :slopeUpExtraForce 0.1 ; Slope up extra force
              :slopeDownExtraForce 0.2 ; Slope down extra force
              ;AutoBalance Force setups
              :autoBalance false ; Enable auto-balance
              :autoBalanceSpringK 0.3 ; Auto-balance spring constant
              :autoBalanceDampingC 0.03 ; Auto-balance damping coefficient
              :autoBalanceSpringOnY 0.5 ; Auto-balance spring on Y-axis
              :autoBalanceDampingOnY 0.015 ; Auto-balance damping on Y-axis 

              }
    :dynamic-environment
    (fn [] 
      (let [
        
            {:keys [y]} @(subscribe [:get-in [:player :position]])

            ]
        [:<>
             [:> drei/Stars {:radius 600 :depth 100 :count 20000 :factor 15 :saturation 0 :fade "fade" :speed 0.01 :frustumCulled false :logarithmicDepthBuffer true}]
             ;[:> drei/Sky {:distance 450 :sunPosition [0 1 0] :inclination 0 :azimuth 0.25}]
             [:> drei/Sky {:turbidity 10 :rayleigh 3 :mieCoefficient 0.005 :mieDirectionalG 0.7 :elevation 21.4 :azimuth 180 :exposure 0.5}]
       
             ;[:ambientLight {:intensity 3}]
             [:hemisphereLight {:position [0 100 0] :color "#fff" :groundColor "#ddd"}]
             [:pointLight {:decay 0 :intensity 2 :position [0 300 0]}]
             (cond 
               (< y 100) [:fog {:attach "fog" :args #js ["#fff" 0.1 250]}]
               (< 100 y 200) [:fog {:attach "fog" :args #js ["#fff" 0.1 500]}]
               (< 200 y) [:fog {:attach "fog" :args #js ["#fff" 0.1 1000]}]
               :else nil)
             ;[:directionalLight {:decay 0 :intensity 3 :position [100 100 100] :castShadow "castShadow"}]
       
             ;[:directionalLight {:intensity 3 :castShadow "castShadow" :position [100 100 100] ;:shadow-bias -0.0004
             ;                    }
             ;   [:orthographicCamera {:args [-20 20 20 -20] :attach "shadow-camera" :near 10}]]
             
             ]     
        )
 
      )
    :objects [:<>

         [:> rapier/RigidBody {:colliders "trimesh" :type "fixed"} 
          [:> drei/Box {
                       :args [600 2 600]
                       :position [0 -2 0]
                       :rotation [0 0 0]
                       }
           [:meshStandardMaterial {:color "#00f"
                                   }]]
          ]
         
         (model/object
           {:position [0 -2 0]
            ;:rotation [(/ (- (.-PI js/Math)) 2) 0 0]
            :scale 0.35
            :src "/maps/low-poly_fantasy_island_medieval.glb"})
   
               

         (for [
               {:keys [ii q gx gy gz]} 
               [
                {:ii 0 :q 200 :gx 60 :gy 60 :gz 60}
                ;{:ii 1 :q 500 :gx 300 :gy 5 :gz 300}
                {:ii 2 :q 100 :gx 60 :gy 30 :gz 60}
                {:ii 3 :q 100 :gx 30 :gy 90 :gz 30}
                {:ii 4 :q 200 :gx 30 :gy 200 :gz 30}
                ]
              ]
         (for [i (range q)]
           (let [x (- (prand-int 0 gx) (prand-int 0 gx))
                 y (prand-int 0 gy)
                 z (- (prand-int 0 gz) (prand-int 0 gz))
                 rx (* (/ (- (.-PI js/Math)) (prand-int 0 10)) (prand-int 0 10))
                 ry (* (/ (- (.-PI js/Math)) (prand-int 0 10)) (prand-int 0 10))
                 rz (* (/ (- (.-PI js/Math)) (prand-int 0 10)) (prand-int 0 10))
                 ;color (str "#" (prand-int 0 9) "d" (prand-int 0 9))
                 color (str "#" "f" (prand-int 0 9) (prand-int 0 9))
                 size [(inc (prand-int 1 5)) (inc (prand-int 1 4)) (inc (prand-int 1 5))]
                 ;size [(inc (prand-int 0 25)) (inc (prand-int 0 4)) (inc (prand-int 0 25))]
                 ;size [(+ 5 (rand-int 20)) (inc (rand-int 10)) (+ 5 (rand-int 20))]
                 ]
             (when-not (and (< -30 x 30) (< -30 y 30) (< -30 z 30))
               (model/platform {:i i :ii ii :x x :y y :z z
                                ;:rx rx :ry ry :rz rz
                                :color color :size size}))))
         )


             (let [x (- (prand-int 1 30) (prand-int 1 30))
                   y 205
                   z (- (prand-int 1 30) (prand-int 1 30))
                   position [x y z]]
               [:group 
              
                [:> rapier/RigidBody {:key "winner-box" :colliders "trimesh" :type "fixed"} 
               [:> drei/Box {
                             :args [25 4 25]
                             :position position
                             :rotation [0 0 0]
                             }
                [:meshStandardMaterial {:color "#a00"}]]]

              ])
             (model/clouds
               {}
               (for [i (range 10)]
           (let [
                 x (- (prand-int 1 60) (prand-int 1 60))
                 y (prand-int 60 180)
                 z (- (prand-int 1 60) (prand-int 1 60))
                 bounds (+ (prand-int 1 10) 5)
                 volume (+ (prand-int 1 50) 10)
                 speed (prand 0 0.5)
                 ]
             {:key (str "cloud"i) :seed i :bounds bounds :volume volume :position [x y z] :speed speed :frustumCulled false}))
               )
              ]
    }
"Ethereum-minimage-2-descent-into-the-labyrinth"
{
    :control {
              :maxVelLimit 20
              }
    :skybox [:> drei/Environment {:files "/skyboxes/background.hdr" :ground {:scale 100}}]
    :ost [
          "/ost/lounge_music_1.mp3"
          "/ost/lounge_music_2.mp3"
          "/ost/lounge_music_3.mp3"
          ] 
    :objects 
    [:<>
     [:> drei/Stars {:radius 600 :depth 100 :count 20000 :factor 15 :saturation 0 :fade "fade" :speed 0.01 :frustumCulled false :logarithmicDepthBuffer true}]
     [:> drei/Sky {:turbidity 10 :rayleigh 3 :mieCoefficient 0.005 :mieDirectionalG 0.7 :elevation 21.4 :azimuth 180 :exposure 0.5}]
     [:hemisphereLight {:position [0 100 0] :color "#fff" :groundColor "#ddd"}]
     [:pointLight {:decay 0 :intensity 2 :position [0 300 0]}]
     [:fog {:attach "fog" :args #js ["#fff" 0.1 250]}]

     [:> rapier/RigidBody {:colliders "trimesh" :type "fixed"} 
      [:> drei/Box {:args [6 5 6] :position [0 -5 0] :rotation [0 0 0]}
       [:meshStandardMaterial {:color "#00f"}]]]
    
     [:> rapier/RigidBody {:colliders "trimesh" :type "fixed"} 
      [:> drei/Box {:args [600 5 600] :position [0 -425 0] :rotation [0 0 0]}
       [:meshStandardMaterial {:color "#00f"}]]]
    
     [:> rapier/RigidBody {:colliders "trimesh" :type "fixed"} 
      [:> drei/Box {:args [50 425 1] :position [0 -210 -25] :rotation [0 0 0]} [:meshStandardMaterial {:color "#000"}]]
      [:> drei/Box {:args [50 425 1] :position [0 -210 25] :rotation [0 0 0]} [:meshStandardMaterial {:color "#000"}]]
      
      [:> drei/Box {:args [1 425 50] :position [25 -210 0] :rotation [0 0 0]} [:meshStandardMaterial {:color "#000"}]]
      [:> drei/Box {:args [1 425 50] :position [-25 -210 0] :rotation [0 0 0]} [:meshStandardMaterial {:color "#000"}]]
      ]
    
     (let [instances 
           (mapv
             (fn [i]
               {:key (str "key"i)
                :args [5 10 20]
                :position [(- (rand-int 50) 25) (- (rand-int 400) 420) (- (rand-int 50) 25)]
                :rotation [0 0 0]
                :scale (rand 4)
                :color "tomato"})
             (range 250))]
       [:> rapier/InstancedRigidBodies {:instances instances :type "fixed"}
        [:instancedMesh {:args #js [10 10 250] :frustumCulled false}
         [:boxGeometry {:args #js [8 1 8]}]
         [:meshStandardMaterial {:color "#00f"}]]])
     
     (let [instances 
           (mapv
             (fn [i]
               {:key (str "key"i)
                :args [5 10 20]
                :position [(- (rand-int 50) 25) (- (rand-int 400) 420) (- (rand-int 50) 25)]
                :rotation [0 0 0]
                :scale (rand 4)
                :color "tomato"})
             (range 250))]
       [:> rapier/InstancedRigidBodies {:instances instances :type "fixed"}
        [:instancedMesh {:args #js [10 10 250] :frustumCulled false}
         [:boxGeometry {:args #js [8 1 8]}]
         [:meshStandardMaterial {:color "#0f0"}]]])

     [:> drei/Instances {:limit 10000}; :range 1000}
      [:boxGeometry]
      [:meshStandardMaterial]
      (for [
               {:keys [ii q gx gy gz]} 
               [
                {:ii 4 :q 0 :gx 100 :gy 100 :gz 100}
                ]
              ]
         (for [i (range q)]
           (let [x (- (rand gx) (rand gx))
                 y (prand-int 0 gy)
                 z (- (rand gz) (rand gz))
                 color (str "#" "f" (prand-int 0 9) (prand-int 0 9))
                 size [(inc (prand-int 1 5)) (inc (prand-int 1 4)) (inc (prand-int 1 5))]]
             
             [:> drei/Instance {:color color :scale 1 :size size :position [x y z]}]
             )))]
              ]
    }

"Ethereum"
{
    :control {
              :maxVelLimit 5
              }
    :ost [
          "/ost/lounge_music_2.mp3"
          "/ost/lounge_music_3.mp3"
          ] 
    :objects 
    [:<>
     [:> drei/Sky {:turbidity 10 :rayleigh 3 :mieCoefficient 0.005 :mieDirectionalG 0.7 :elevation 21.4 :azimuth 180 :exposure 0.5}]
     [:hemisphereLight {:position [0 100 0] :color "#fff" :groundColor "#ddd"}]
     [:pointLight {:decay 0 :intensity 2 :position [0 300 0]}]

     [:> rapier/RigidBody {:colliders "trimesh" :type "fixed"} 
      [:> drei/Box {:args [600 5 600] :position [0 -5 0] :rotation [0 0 0]}
       [:meshStandardMaterial {:color "#ddd"}]]]
    
     (let [instances 
           (mapv
             (fn [i]
               {:key (str "key"i)
                :position [(- (rand-int 50) 25) (- (rand-int 100) 50) (- (rand-int 50) 25)]
                :rotation [0 0 0]
                :scale (rand 4)})
             (range 250))]
       [:> rapier/InstancedRigidBodies {:instances instances :type "fixed"
                                        }
        [:instancedMesh {:args #js [10 10 250] :frustumCulled false}
         [:boxGeometry {:args #js [1 1 1]}]
         [:meshStandardMaterial {:color "#ccc"}]]])
     ]
     
     }


   })


(def world
  {
   [0 0]
   {:title "Main Square"
    :ost "/ost/lounge_music_3.mp3"
    :objects
    (let [seed (new default "mainsquare")
          prand-int (fn [x y] (.nextInt seed x y))
          prand (fn [x y] (.next prando x y))
           ]
    [:<>
     (model/object
           {:position [0 -5 0]
            ;:rotation [(/ (- (.-PI js/Math)) 2) 0 0]
            :scale 0.2
            :src "/maps/throne_room.glb"})
     
     [:> drei/Sky {:turbidity 10 :rayleigh 3 :mieCoefficient 0.005 :mieDirectionalG 0.7 :elevation 21.4 :azimuth 180 :exposure 0.5}]
     [:hemisphereLight {:position [0 100 0] :color "#fff" :groundColor "#ddd"}]
     [:pointLight {:decay 0 :intensity 2 :position [0 300 0]}]

     [:> rapier/RigidBody {:colliders "trimesh" :type "fixed"} 
      [:> drei/Box {:args [600 5 600] :position [0 -5 0] :rotation [0 0 0]}
       [:meshStandardMaterial {:color "#ddd"}]]]
    
     ])}

   [1 0]
   {:title "Beginner Houses"
    :ost "/ost/lounge_music_2.mp3"
    :objects
    (let [seed (new default "beginnerhouses")
          prand-int (fn [x y] (.nextInt seed x y))
          prand (fn [x y] (.next prando x y))
          instances 
           (mapv
             (fn [i]
               {:key (str "key"i)
                :position [(- (prand-int 0 50) 25) (- (prand-int 0 100) 50) (- (prand-int 0 50) 25)]
                :rotation [0 0 0]
                :scale (prand 0 4)})
             (range 250))]
    [:<>
     (model/object
           {:position [0 0 0]
            ;:rotation [(/ (- (.-PI js/Math)) 2) 0 0]
            :scale 10
            :src "/maps/terrain.glb"})
     
     [:> drei/Stars {:radius 300 :depth 100 :count 20000 :factor 15 :saturation 0 :fade "fade" :speed 0.01 :frustumCulled false :logarithmicDepthBuffer true}]
     ;[:> drei/Sky {:turbidity 10 :rayleigh 3 :mieCoefficient 0.005 :mieDirectionalG 0.7 :elevation 21.4 :azimuth 180 :exposure 0.5}]
     [:hemisphereLight {:position [0 100 0] :color "#fff" :groundColor "#333"}]
     [:pointLight {:decay 0 :intensity 0.2 :position [0 300 0]}]

     [:> rapier/RigidBody {:colliders "trimesh" :type "fixed"} 
      [:> drei/Box {:args [600 5 600] :position [0 0 0] :rotation [0 0 0]}
       [:meshStandardMaterial {:color "#000"}]]]
    
       [:> rapier/InstancedRigidBodies {:instances instances :type "fixed"
                                        }
        [:instancedMesh {:args [10 10 250] :frustumCulled false}
         [:boxGeometry {:args [1 1 1]}]
         [:meshStandardMaterial {:color "#333"}]]]
     ])}

   
   }
  )
