(ns main.views.lobby
  (:require 
   [main.maps :refer [maps]]
   [main.models :refer [loading avatars] :as model]
   [main.wagmi :refer [connect-kit]]
   [main.guild :refer [join-guild]]
   
   ["@mantine/core" :refer [AppShell Modal Avatar Badge Burger Button createTheme Group Center SimpleGrid SimpleGrid Grid Container Flex Stack Skeleton]]
   ["@mantine/hooks" :refer [useDisclosure]]
   ["@tabler/icons-react" :as icon]
   
   ["react" :as react]
   [reagent.core :as reagent]
   [re-frame.core :as rf :refer [subscribe dispatch reg-fx]]
   
   ["wagmi" :as wagmi]
   
   ["three" :as three]
               ;["three/addons/renderers/webgpu/WebGPURenderer.js" :refer [WebGPURenderer]]
   ["@react-three/fiber" :as fiber]
   ["@react-three/drei" :as drei]
   ["@react-three/rapier" :as rapier]
   ["ecctrl" :refer [Controller] :as ecc]
   ["howler" :refer [Howl]]
   
   ["@guildxyz/sdk" :as guild]
   
   [main.audio :as audio]))

(defn welcome-modal [tos? status]
  [:> Modal {:opened (and (not= status "connected") (not tos?))
             :onClose (fn []
                        (audio/initialize-music ["/ost/through_the_ether.mp3"])
                        (dispatch [:assoc-in [:tos?] true])) :title "Disclaimer" :centered "centered"}
   [:p "Adventure.io is an experimental onchain game engine made by the team at " [:a {:href "https://guild.xyz" :target "_blank"} "Guild.xyz"]]])

(defn lobby [debug?] 
  [:> fiber/Canvas {:shadows "shadows"}
   [:> react/Suspense {:fallback (reagent/as-element [loading])}
    [:> rapier/Physics {:timeStep "vary" :debug (if debug? "debug" nil)}
     [:ambientLight {:intensity 3}]

     [:> drei/Stars]
     [:> drei/Gltf
      {:castShadow "castShadow"
       :receiveShadow "receiveShadow"
       :position [0 0 -100]
       :scale 30
       :src "/npc/ethereum_logo.glb"}]]

    [:> drei/Clouds {:material three/MeshBasicMaterial}
     [:> drei/Cloud {:seed 10 :bounds 50 :volume 80 :position [0 0 0] :speed 0.1}]
     [:> drei/Cloud {:seed 1 :bounds 50 :volume 80 :position [-80 10 -80] :speed 0.05}]]]])