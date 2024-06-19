
(ns main.models
  (:require ["@react-three/drei" :as drei]
            [reagent.core :as reagent]
            ["react" :as react]
            ["../ecmascript/threejs" :refer [Box]]
            ["@react-three/rapier" :as rapier]
    ))



(defn loading []
  nil)

(defn object [gltf]
  [:> react/Suspense {:fallback (reagent/as-element [loading])}
   [:> rapier/RigidBody {:colliders "trimesh" :type "fixed"}
    [:> drei/Gltf gltf]]])

(defn player-model [{:keys [nickname position quaternion] :as p}]
  [:<>
   [:> drei/Html {
                  :key (str "html-player"(:key p))
                  :style {:transform "translate(-50%, 0)"} :position [0 1 0]}
    [:div {
           :key (str "nickname-player"(:key p))
           :style {:WebkitTextStroke "0.1rem #fff"}} nickname]] 
             
   [:> drei/PositionalAudio {:autoplay "autoplay" :loop "loop" :url "/ost/mystery_funk.mp3" :distance 3}] 
   
   [:> drei/Gltf
    {
     :key (str "gltf-player"(:key p))
     :castShadow "castShadow"
     :receiveShadow "receiveShadow"
     :position position
     :quaternion quaternion
     :scale 0.315
     :src "/ghost_w_tophat-transformed.glb"}]])

(defn platform-model [{:keys [i size x y z color]}]
  [:group 
   [:> rapier/RigidBody {:key (str "box"i) :colliders "trimesh" :type "fixed"} 
    [:> drei/Box {:castShadow "castShadow" :receiveShadow "receiveShadow" :args size :position [x y z] :rotation [0 0 0]}
     [:meshStandardMaterial {:color color}]]]])

(defn dragons []
  [:<>

[:> drei/Gltf
    {:castShadow "castShadow"
     :receiveShadow "receiveShadow"
     :position [-100 10 0]
     :scale 50
     :src "/npc/mega_wyvern.glb"}]
   
[:> drei/Gltf
    {:castShadow "castShadow"
     :receiveShadow "receiveShadow"
     :position [10 -1 0]
     :scale 3
     :src "/npc/black_dragon_with_idle_animation.glb"}]
   ])

(defn guild-empire []
  (object
    {:castShadow "castShadow"
     :receiveShadow "receiveShadow"
     :position [10 0 -10]
     :scale 0.2
     :src "/objects/GuildDenver.glb"}))
