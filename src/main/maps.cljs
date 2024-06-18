
(ns main.maps
  (:require ["@react-three/drei" :as drei]
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

     [:ambientLight {:intensity 0.5}]

     ;[:spotLight {:name "followLight" :angle 0.1 :decay 0 :intensity 5 :penumbra 1 :position [0 10 0]}]
  
     [:pointLight {:decay 0 :intensity 2 :position [100 10 -100]}]
     [:pointLight {:decay 0 :intensity 2 :position [-100 10 100]}]
     [:pointLight {:decay 0 :intensity 2 :position [100 10 100]}]
     [:pointLight {:decay 0 :intensity 2 :position [-100 10 -100]}]
             
             
             ]
    :objects 
    [:<>
     (model/object
       {:castShadow "castShadow"
        :receiveShadow "receiveShadow"
        :position [10 0 -10]
        :scale 0.2
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
    :control {:maxVelLimit 5
              :sprintMult 3
              :jumpVel 5
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
{:gltf 
    {:castShadow "castShadow" 
     :receiveShadow "receiveShadow"
     :position [10 -20 0]
     ;:rotation [(/ (- (.-PI js/Math)) 2) 0 0]
     :scale 1
     :src "/maps/magical_manor.glb"}
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
