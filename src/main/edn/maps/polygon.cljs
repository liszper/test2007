(ns main.edn.maps.polygon
  (:require [main.models :as model]
            [main.edn.utils :refer [prand-int prand]]
            ["@react-three/drei" :as drei]
            ["@react-three/rapier" :as rapier]))

(def world-map
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
            [:pointLight {:decay 0 :intensity 2 :position [-100 10 -100]}]]})