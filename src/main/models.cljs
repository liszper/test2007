
(ns main.models
  (:require ["@react-three/drei" :as drei]
            [reagent.core :as reagent]
            ["three" :as three]
            ["react" :as react]
            ["../ecmascript/threejs" :refer [Box]]
            ["@react-three/rapier" :as rapier]
    ))



(defn loading []
  nil)

(defn gltf [gltf]
  [:> drei/Gltf gltf])

(defn object [gltf]
   [:> rapier/RigidBody (if (:key gltf) {:key (str (:key gltf)) :colliders "trimesh" :type "fixed"} {:colliders "trimesh" :type "fixed"})
    [:> drei/Gltf (dissoc gltf :key)]])

(defn player [{:keys [nickname position quaternion avatar] :as p}]
  [:<>
   [:> drei/Html {:style {:transform "translate(-50%, 0)"} :position (or (:nickname-position avatar) [0 1 0])}
    [:div {:style {:WebkitTextStroke "0.1rem #000"}} nickname]] 
   [:> drei/Gltf
    {;:castShadow "castShadow"
     ;:receiveShadow "receiveShadow"
     :position (or (:position avatar) [0 -0.55 0])
     :quaternion (or quaternion [0 0 0 0])
     :scale (or (:scale avatar) 0.315)
     :src (or (:src avatar) "/ghost_w_tophat-transformed.glb")}]])

(defn platform [{:keys [i ii size x y z rx ry rz color]}]
  [:group {:key (str "box"(+ (* ii 1000)i))}
   [:> rapier/RigidBody {:colliders "cuboid" :args size :type "fixed"} 
    [:> drei/Box {:args size :position [x y z] :rotation [rx ry rz]}
     [:meshStandardMaterial {:color color}]]]])

(defn clouds [{:keys [texture]} clouds]
   [:> drei/Clouds {:material three/MeshBasicMaterial 
                    :frustumCulled false
                    :texture (or texture "/texture/cloud.png")}
         (for [settings clouds]
           (let [] [:> drei/Cloud settings]))])


(def avatars
  {
   :wizard
   {:name :wizard
    :src "/npc/wizard.glb"
    :scale 1
    :position [0 -0.65 0]
    :springK 0
    :dampingC 0
    :nickname-position [0 2 0]}

   :ghost
   {:name :ghost
    :src "/ghost_w_tophat-transformed.glb"
    :scale 0.315
    :position [0 -0.55 0]
    :nickname-position [0 1 0]}
   
   })


