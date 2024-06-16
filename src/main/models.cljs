
(ns main.models
  (:require ["@react-three/drei" :as drei]
            ["../ecmascript/threejs" :refer [Box]]
    ))



(defn loading []
  [:> Box {:position [(- 3.6) 0 0]}])


(defn player-model [{:keys [nickname position quaternion] :as p}]
  [:<>
   [:> drei/Html {
                  :key (str "html-player"(:key p))
                  :style {:transform "translate(-50%, 0)"} :position [0 1 0]}
    [:div {
           :key (str "nickname-player"(:key p))
           :style {:WebkitTextStroke "0.1rem #fff"}} nickname]] 
   [:> drei/Gltf
    {
     :key (str "gltf-player"(:key p))
     :castShadow "castShadow"
     :receiveShadow "receiveShadow"
     :position position
     :quaternion quaternion
     :scale 0.315
     :src "/ghost_w_tophat-transformed.glb"}]])


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
