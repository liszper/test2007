
(ns main.maps
  (:require
   [re-frame.core :as rf :refer [subscribe dispatch reg-fx]]
   ["three" :as three]
   ["@react-three/drei" :as drei]
   ["@react-three/rapier" :as rapier]
   ["prando" :refer [default]]
   [main.models :as model]
   [main.edn.utils :refer [prand-int prand random prando]]
   [main.edn.maps.minimage2 :as minimage2]
   [main.edn.maps.ethereum :as ethereum]
   [main.edn.maps.base :as base]
   [main.edn.maps.opmainnet :as opmainnet]
   [main.edn.maps.polygon :as polygon]
   [main.edn.maps.skyvillage :as skyvillage]
   [main.edn.maps.solidjumping :as solidjumping]
   [main.edn.maps.castle :as castle]
   [main.edn.maps.plane :as plane]
            ;["../ecmascript/threejs" :refer [Box Instances]]
   ))

(def maps
  {"Base" base/world-map 
   "OP Mainnet" opmainnet/world-map
   "Polygon" polygon/world-map
   "Ethereum2" skyvillage/world-map
   "Ethereum-solid-jumping" solidjumping/world-map
   "Ethereum-minimage-2-descent-into-the-labyrinth" minimage2/world-map
   "Ethereum" ethereum/world-map
   "Castle" castle/world-map
   "Plane" plane/world-map})