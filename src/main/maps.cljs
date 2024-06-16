
(ns main.maps
  (:require ["@react-three/drei" :as drei]
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
    :control {:debug true
              :maxVelLimit 5
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
    :control {:debug true
              :maxVelLimit 10
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
