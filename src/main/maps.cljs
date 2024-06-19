
(ns main.maps
  (:require ["@react-three/drei" :as drei]
            ["@react-three/rapier" :as rapier]
            [main.models :as model]
    ))



(def maps
  {
   "Base"
   {:gltf 
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
             
    [:group {:position [30 0 30]}
     [:> drei/PositionalAudio {:autoplay "autoplay" :loop "loop" :url "/ost/base_coffee.mp3" :distance 3}] 
     ]
     (model/object
       {:castShadow "castShadow"
        :receiveShadow "receiveShadow"
        :position [30 0 30]
        :scale 0.02
        :src "/objects/GuildDenver.glb"})
     ]
    }
   
   "Ethereum"
   {:gltf 
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
       ;[:directionalLight {:intensity 1 :castShadow "castShadow" :position [0 10 0] :shadow-bias -0.0004}
       ; [:orthographicCamera {:args [-20 20 20 -20] :attach "shadow-camera"}]]
       [:ambientLight {:intensity 3}]
             
             
             ]
    :objects [:<>
             [:> drei/PositionalAudio {:autoplay "autoplay" :loop "loop" :url "/ost/yellow_floating_sand_dune_world.mp3" :distance 3}] 
              ;[:fog {:attach "fog" :args #js ["#cc7b32" 10 500]}]

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

   "OP Mainnet"
{ 
    :control {:maxVelLimit 10
              :sprintMult 3
              :jumpVel 30
              ;:airDragMultiplier 0.1
              ;:fallingGravityScale 10
              :camInitDis -3}
    :skybox [:> drei/Stars {:radius 300 :depth 50 :count 20000 :factor 5 :saturation 0 :fade "fade" :speed 0.01}]
    ;:skybox [:> drei/Environment {:files "/psy.hdr" :ground {:scale 100}}]
    :lights [:<>
          [:pointLight {:decay 0 :intensity 3 :position [0 100 0]}]
       ;[:directionalLight {:intensity 1 :castShadow "castShadow" :position [0 10 0] :shadow-bias -0.0004}
       ; [:orthographicCamera {:args [-20 20 20 -20] :attach "shadow-camera"}]]
       [:ambientLight {:intensity 1}]
             
             
             ]
    :objects [:<>
         
         [:> rapier/RigidBody {:colliders "trimesh" :type "fixed"} [:> drei/Gltf {:castShadow "castShadow" 
     :receiveShadow "receiveShadow"
     :position [0 -10 0]
     ;:rotation [(/ (- (.-PI js/Math)) 2) 0 0]
     :scale 1
     :src "/maps/low-poly_fantasy_island_medieval.glb"}]]

         [:> rapier/RigidBody {:colliders "trimesh" :type "fixed"} 
          [:> drei/Circle {:receiveShadow "receiveShadow" :args [600 600] :position [0 -10 0] :rotation [(/ js/Math.PI 2 -1) 0 0]}
           [:meshStandardMaterial {:color "blue"}]          
           ]]
         
         (for [{:keys [q gx gy gz]} 
               [
                {:q 500 :gx 600 :gy 500 :gz 600}
                {:q 500 :gx 300 :gy 5 :gz 300}
                {:q 500 :gx 300 :gy 50 :gz 300}
                {:q 500 :gx 300 :gy 250 :gz 300}
                {:q 500 :gx 300 :gy 1000 :gz 300}
                ]
               ]
         (for [i (range q)]
           (let [x (- (rand-int gx) (rand-int gx))
                 y (rand-int gy)
                 z (- (rand-int gz) (rand-int gz))
                 color (str "#" (rand-int 9) "d" (rand-int 9))
                 size [(rand-int 25) (rand-int 4) (rand-int 25)]
                 ;size [(+ 5 (rand-int 20)) (inc (rand-int 10)) (+ 5 (rand-int 20))]
                 ]
             (when-not (and (< -100 x 100) (< -100 y 100) (< -100 z 100))
               (model/platform-model {:i i :x x :y y :z z :color color :size size}))))
         )

      ;           <Clouds material={THREE.MeshBasicMaterial}>
      ;    <Cloud seed={10} bounds={50} volume={80} position={[40, 0, -80]} />
      ;    <Cloud seed={10 :bounds 50 :volume 80 :position [-40 10 -80]}] ]

         (for [i [1]]
           (let [x (- (rand-int 300) (rand-int 300))
                 y 1050
                 z (- (rand-int 300) (rand-int 300))
                 color "#a00" 
                 size [25 4 25]
                 ]
             [:group 
              [:> rapier/RigidBody {:key (str "box"i) :colliders "trimesh" :type "fixed"} 
               [:> drei/Box {:castShadow "castShadow" :receiveShadow "receiveShadow" :args size :position [x y z] :rotation [0 0 0]}
                [:meshStandardMaterial {:color color}]]]]))


             ; [:fog {:attach "fog" :args #js ["#0a0" 10 50]}]

              ]
    }

   })
