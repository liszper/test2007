(ns main.edn.maps.plane
  (:require [main.models :as model]
            [main.edn.utils :refer [prand-int prand prando]]
            ["prando" :refer [default]]
            ["@react-three/drei" :as drei]
            ["@react-three/rapier" :as rapier]))

(def world-map
    {:title "Beginner Houses"
   :ost "/ost/lounge_music_2.mp3"
   :objects
   (let [seed (new default "beginnerhouses")
         prand-int (fn [x y] (.nextInt seed x y))
         prand (fn [x y] (.next prando x y))
         instances
         (mapv
          (fn [i]
            {:key (str "key" i)
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
  
      [:> rapier/InstancedRigidBodies {:instances instances :type "fixed"}
       [:instancedMesh {:args [10 10 250] :frustumCulled false}
        [:boxGeometry {:args [1 1 1]}]
        [:meshStandardMaterial {:color "#333"}]]]])})