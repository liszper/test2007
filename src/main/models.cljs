
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
   [:> rapier/RigidBody (if (:key gltf) {:key (str (:key gltf)) :colliders "trimesh" :type "fixed"} {:colliders "trimesh" :type "fixed"})
    [:> drei/Gltf (dissoc gltf :key)]])

(defn player-model [{:keys [nickname position quaternion] :as p}]
  [:<>
   [:> drei/Html {
                  :key (str "html-player"(:key p))
                  :style {:transform "translate(-50%, 0)"} :position [0 1 0]}
    [:div {
           :key (str "nickname-player"(:key p))
           :style {:WebkitTextStroke "0.1rem #000"}} nickname]] 
              
   [:> drei/Gltf
    {
     :key (str "gltf-player"(:key p))
     :castShadow "castShadow"
     :receiveShadow "receiveShadow"
     :position position
     :quaternion quaternion
     :scale 0.315
     :src "/ghost_w_tophat-transformed.glb"}]])

(defn platform-model [{:keys [i ii size x y z rx ry rz color]}]
  [:group {:key (str "box"(+ (* ii 1000)i))}
   [:> rapier/RigidBody {:colliders "trimesh" :type "fixed"} 
    [:> drei/Box {:args size :position [x y z] :rotation [rx ry rz]}
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
