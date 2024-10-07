(ns main.edn.maps.minimage2
  (:require [main.edn.utils :refer [prand-int prand]]
            ["@react-three/drei" :as drei]
            ["@react-three/rapier" :as rapier]))

(def world-map
  {:control {:maxVelLimit 20}
   :skybox [:> drei/Environment {:files "/skyboxes/background.hdr" :ground {:scale 100}}]
   :ost ["/ost/lounge_music_1.mp3"
         "/ost/lounge_music_2.mp3"
         "/ost/lounge_music_3.mp3"]
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
     [:> drei/Box {:args [1 425 50] :position [-25 -210 0] :rotation [0 0 0]} [:meshStandardMaterial {:color "#000"}]]]
  
    (let [instances
          (mapv
           (fn [i]
             {:key (str "key" i)
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
             {:key (str "key" i)
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
     (for [{:keys [ii q gx gy gz]}
           [{:ii 4 :q 0 :gx 100 :gy 100 :gz 100}]]
       (for [i (range q)]
         (let [x (- (rand gx) (rand gx))
               y (prand-int 0 gy)
               z (- (rand gz) (rand gz))
               color (str "#" "f" (prand-int 0 9) (prand-int 0 9))
               size [(inc (prand-int 1 5)) (inc (prand-int 1 4)) (inc (prand-int 1 5))]]
  
           [:> drei/Instance {:color color :scale 1 :size size :position [x y z]}])))]]})