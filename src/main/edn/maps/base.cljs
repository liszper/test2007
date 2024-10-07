(ns main.edn.maps.base
  (:require [main.edn.utils :refer [prand-int prand]]
            [main.models :as model]
            ["@react-three/drei" :as drei]
            ["@react-three/rapier" :as rapier]))

(def world-map
  {:ost ["/ost/base_coffee.mp3"]
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
    [:pointLight {:color "#00a" :decay 0.1 :intensity 4 :position [53 7.7 27]}]]
   :objects
   [:<>
  
    (model/object
     {:castShadow "castShadow"
      :receiveShadow "receiveShadow"
      :position [30 0 30]
      :scale 0.02
      :src "/objects/GuildDenver.glb"})]})