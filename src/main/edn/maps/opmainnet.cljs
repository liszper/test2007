(ns main.edn.maps.opmainnet
  (:require [main.models :as model]
            [main.edn.utils :refer [prand-int prand]]
            ["@react-three/drei" :as drei]
            ["@react-three/rapier" :as rapier]))

(def world-map
  {:ost ["/ost/yellow_floating_sand_dune_world.mp3"]
   :gltf
   {:castShadow "castShadow"
    :receiveShadow "receiveShadow"
    :position [0 -2 -30]
       ;:rotation [(/ (- (.-PI js/Math)) 2) 0 0]
    :scale 1
    :src "/maps/desert_sector.glb"}
   :control {:maxVelLimit 10
             :sprintMult 2
             :jumpVel 4
                ;:airDragMultiplier 0.1
                ;:fallingGravityScale 10
             :camInitDis -3}
      ;:skybox [:> drei/Environment {:files "/psy.hdr" :ground {:scale 100}}]
   :lights [:<>
            [:ambientLight {:intensity 3}]]
   :objects [:<>
                ;[:fog {:attach "fog" :args #js ["#cc7b32" 10 500]}]
  

             [:> drei/Gltf
              {:castShadow "castShadow"
               :receiveShadow "receiveShadow"
               :position [-100 10 0]
               :scale 50
               :src "/npc/mega_wyvern.glb"}]]})