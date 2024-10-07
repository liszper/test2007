(ns main.edn.maps.ethereum
  (:require [main.edn.utils :refer [prand-int prand]]
            ["@react-three/drei" :as drei]
            [main.models :as model]
            ["@react-three/rapier" :as rapier]))

(def world-map
  {:control {:maxVelLimit 5}
    :ost ["/ost/lounge_music_2.mp3"
          "/ost/lounge_music_3.mp3"]
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
              {:key (str "key" i)
               :position [(- (rand-int 50) 25) (- (rand-int 100) 50) (- (rand-int 50) 25)]
               :rotation [0 0 0]
               :scale (rand 4)})
            (range 250))]
       [:> rapier/InstancedRigidBodies {:instances instances :type "fixed"}
        [:instancedMesh {:args #js [10 10 250] :frustumCulled false}
         [:boxGeometry {:args #js [1 1 1]}]
         [:meshStandardMaterial {:color "#ccc"}]]])]})