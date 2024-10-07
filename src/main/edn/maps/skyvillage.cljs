(ns main.edn.maps.skyvillage 
  (:require [main.models :as model]
            ["three" :as three]
            [main.edn.utils :refer [prand-int prand]]
            [re-frame.core :as rf :refer [subscribe dispatch reg-fx]]
            ["@react-three/drei" :as drei]
            ["@react-three/rapier" :as rapier]))

(def world-map
  {:ost ["/ost/mystery_funk.mp3"]
   :control {:maxVelLimit 10
             :sprintMult 3
             :jumpVel 30
                ;:airDragMultiplier 0.1
                ;:fallingGravityScale 10
             :camInitDis -3}
   :dynamic-environment
   (fn []
     (let [{:keys [y]} @(subscribe [:get-in [:player :position]])]
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
        ]))
   :objects [:<>
  
             [:> rapier/RigidBody {:colliders "trimesh" :type "fixed"}
              [:> drei/Circle {:args [600 600] :position [0 -10 0] :rotation [(/ js/Math.PI 2 -1) 0 0]}
               [:meshStandardMaterial {:color "blue"}]]]
  
             (model/object
              {:position [0 -10 0]
              ;:rotation [(/ (- (.-PI js/Math)) 2) 0 0]
               :scale 1
               :src "/maps/low-poly_fantasy_island_medieval.glb"})
  
  
  
             (model/object
              {:position [5 -5.2 11]
               :scale 4
               :rotation [0 (/ js/Math.PI 2 -1) 0]
               :src "/npc/wizard.glb"})
  
             (for [{:keys [ii q gx gy gz]}
                   [{:ii 0 :q 500 :gx 600 :gy 500 :gz 600}
                  ;{:ii 1 :q 500 :gx 300 :gy 5 :gz 300}
                    {:ii 2 :q 500 :gx 300 :gy 50 :gz 300}
                    {:ii 3 :q 500 :gx 300 :gy 250 :gz 300}
                    {:ii 4 :q 1000 :gx 300 :gy 1000 :gz 300}]]
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
                     (model/platform {:i i :ii ii :x x :y y :z z :rx rx :ry ry :rz rz :color color :size size})))))
  
  
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
                 [:> drei/Box {:args [25 4 25]
                               :position position
                               :rotation [0 0 0]}
                  [:meshStandardMaterial {:color "#a00"}]]]
  
  
  
                (for [i (range 50)]
                  (let [x (- (prand-int 0 600) (prand-int 0 600))
                        y (prand-int 0 1000)
                        z (- (prand-int 0 600) (prand-int 0 600))]
                    (model/object {:key (str "castle" i) :index (str "floatingcastle" i) :position [x y z] :rotation [0 (rand 3) 0] :scale 5 :src "/objects/floating_castle.glb"})))])
  
  
             [:> drei/Clouds {:material three/MeshBasicMaterial
                              :frustumCulled false
                              :texture "/texture/cloud.png"}
              (for [i (range 10)]
                (let [x (- (prand-int 0 600) (prand-int 0 600))
                      y (+ (prand-int 0 200) 200)
                      z (- (prand-int 0 600) (prand-int 0 600))
                      bounds (+ (prand-int 0 100) 50)
                      volume (+ (prand-int 0 500) 100)
                      speed (prand 0 0.5)]
                  [:> drei/Cloud {:key (str "cloud" i) :seed i :bounds bounds :volume volume :position [x y z] :speed speed :frustumCulled false}]))]]})