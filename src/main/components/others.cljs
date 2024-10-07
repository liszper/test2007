(ns main.components.others
  (:require 
   [main.maps :refer [maps]]
   [main.models :refer [loading avatars] :as model]
   [main.wagmi :refer [connect-kit]]
   [main.guild :refer [join-guild]]
   
   ["@mantine/core" :refer [AppShell Modal Avatar Badge Burger Button createTheme Group Center SimpleGrid SimpleGrid Grid Container Flex Stack Skeleton]]
   ["@mantine/hooks" :refer [useDisclosure]]
   ["@tabler/icons-react" :as icon]
   
   ["react" :as react]
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
   ))

(defn other-players []
  (let [{:keys [nickname located]} @(subscribe [:get-in [:player]])
        players @(subscribe [:get-in [:players located]])]
    [:<>
     (for [[other-player data] (dissoc players nickname)]
       (let [{:keys [nickname avatar x y z qx qy qz qw]} data]
         [:group {:key (str other-player "-group")
                  :position [x y z]}
          [model/player
           {:key nickname
            :nickname nickname
            :avatar avatar
            :quaternion [qx qy qz qw]}]]))]))