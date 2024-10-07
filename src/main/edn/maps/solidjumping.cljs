(ns main.edn.maps.solidjumping
  (:require [main.edn.utils :refer [prand-int prand]]
            [re-frame.core :as rf :refer [subscribe dispatch reg-fx]]
            ["@react-three/drei" :as drei]
            [main.models :as model]
            ["@react-three/rapier" :as rapier]))

(def world-map
  {:ost ["/ost/In_the_depths_of_Farcaster.mp3"]
   :control {:maxVelLimit 10 ; Maximum velocity limit
             :turnVelMultiplier 0.2 ; Turn velocity multiplier
             :turnSpeed 15 ; Turn speed
             :sprintMult 1.2 ; Sprint speed multiplier
             :jumpVel 8 ; Jump velocity
             :jumpForceToGroundMult 5 ; Jump force to ground object multiplier
             :slopJumpMult 0.25 ; Slope jump affect multiplier
             :sprintJumpMult 1.2 ; Sprint jump multiplier
             :airDragMultiplier 0.2 ; Air drag multiplier
             :dragDampingC 0.15 ; Drag damping coefficient
             :accDeltaTime 8 ; Acceleration delta time
             :rejectVelMult 4 ; Reject velocity multiplier
             :moveImpulsePointY 0.5 ; Move impulse point Y offset
             :camFollowMult 11 ; Camera follow speed multiplier
             :fallingGravityScale 2.5 ; Character is falling, apply higher gravity
             :fallingMaxVel -20 ; Limit character max falling velocity
             :wakeUpDelay 200 ; Wake up character delay time after window visibility change to visible
                ;Floating Ray setups
             :rayHitForgiveness 0.1 ; Ray hit forgiveness
             :rayDir {:x 0 :y -1 :z 0} ; Ray direction
             :dampingC 0.08 ; Damping coefficient
                ;Slope Ray setups
             :showSlopeRayOrigin false ; Show slope ray origin
             :slopeMaxAngle 1 ; in rad, the max walkable slope angle
             :slopeRayDir {:x 0 :y -1 :z 0} ; Slope ray direction
             :slopeUpExtraForce 0.1 ; Slope up extra force
             :slopeDownExtraForce 0.2 ; Slope down extra force
                ;AutoBalance Force setups
             :autoBalance false ; Enable auto-balance
             :autoBalanceSpringK 0.3 ; Auto-balance spring constant
             :autoBalanceDampingC 0.03 ; Auto-balance damping coefficient
             :autoBalanceSpringOnY 0.5 ; Auto-balance spring on Y-axis
             :autoBalanceDampingOnY 0.015 ; Auto-balance damping on Y-axis 
             }
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
              [:> drei/Box {:args [600 2 600]
                            :position [0 -2 0]
                            :rotation [0 0 0]}
               [:meshStandardMaterial {:color "#00f"}]]]
  
             (model/object
              {:position [0 -2 0]
              ;:rotation [(/ (- (.-PI js/Math)) 2) 0 0]
               :scale 0.35
               :src "/maps/low-poly_fantasy_island_medieval.glb"})
  
  
  
             (for [{:keys [ii q gx gy gz]}
                   [{:ii 0 :q 200 :gx 60 :gy 60 :gz 60}
                  ;{:ii 1 :q 500 :gx 300 :gy 5 :gz 300}
                    {:ii 2 :q 100 :gx 60 :gy 30 :gz 60}
                    {:ii 3 :q 100 :gx 30 :gy 90 :gz 30}
                    {:ii 4 :q 200 :gx 30 :gy 200 :gz 30}]]
               (for [i (range q)]
                 (let [x (- (prand-int 0 gx) (prand-int 0 gx))
                       y (prand-int 0 gy)
                       z (- (prand-int 0 gz) (prand-int 0 gz))
                       rx (* (/ (- (.-PI js/Math)) (prand-int 0 10)) (prand-int 0 10))
                       ry (* (/ (- (.-PI js/Math)) (prand-int 0 10)) (prand-int 0 10))
                       rz (* (/ (- (.-PI js/Math)) (prand-int 0 10)) (prand-int 0 10))
                   ;color (str "#" (prand-int 0 9) "d" (prand-int 0 9))
                       color (str "#" "f" (prand-int 0 9) (prand-int 0 9))
                       size [(inc (prand-int 1 5)) (inc (prand-int 1 4)) (inc (prand-int 1 5))]
                   ;size [(inc (prand-int 0 25)) (inc (prand-int 0 4)) (inc (prand-int 0 25))]
                   ;size [(+ 5 (rand-int 20)) (inc (rand-int 10)) (+ 5 (rand-int 20))]
                       ]
                   (when-not (and (< -30 x 30) (< -30 y 30) (< -30 z 30))
                     (model/platform {:i i :ii ii :x x :y y :z z
                                  ;:rx rx :ry ry :rz rz
                                      :color color :size size})))))
  
  
             (let [x (- (prand-int 1 30) (prand-int 1 30))
                   y 205
                   z (- (prand-int 1 30) (prand-int 1 30))
                   position [x y z]]
               [:group
  
                [:> rapier/RigidBody {:key "winner-box" :colliders "trimesh" :type "fixed"}
                 [:> drei/Box {:args [25 4 25]
                               :position position
                               :rotation [0 0 0]}
                  [:meshStandardMaterial {:color "#a00"}]]]])
             (model/clouds
              {}
              (for [i (range 10)]
                (let [x (- (prand-int 1 60) (prand-int 1 60))
                      y (prand-int 60 180)
                      z (- (prand-int 1 60) (prand-int 1 60))
                      bounds (+ (prand-int 1 10) 5)
                      volume (+ (prand-int 1 50) 10)
                      speed (prand 0 0.5)]
                  {:key (str "cloud" i) :seed i :bounds bounds :volume volume :position [x y z] :speed speed :frustumCulled false})))]})