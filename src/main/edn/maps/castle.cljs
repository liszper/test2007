(ns main.edn.maps.castle
  (:require [main.models :as model]
            [main.edn.utils :refer [prand-int prand prando]]
            ["prando" :refer [default]]
            ["@react-three/drei" :as drei]
            ["@react-three/rapier" :as rapier]))

(def world-map
  {:title "Main Square"
   :ost "/ost/lounge_music_3.mp3"
   :objects
   (let [seed (new default "mainsquare")
         prand-int (fn [x y] (.nextInt seed x y))
         prand (fn [x y] (.next prando x y))]
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
        [:meshStandardMaterial {:color "#ddd"}]]]])})