(ns main.components.player
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

   ["@guildxyz/sdk" :as guild]))

(set! *warn-on-infer* false)

(defn record-player-movement [wrapper {:keys [nickname avatar located x y z qx qy qz qw]}]
  (fn [state]
    (let [position (.getWorldPosition (.-current wrapper) (new three/Vector3))
          nx (.toFixed (.parseFloat js/Number (.-x position)) 3)
          ny (.toFixed (.parseFloat js/Number (.-y position)) 2)
          nz (.toFixed (.parseFloat js/Number (.-z position)) 3)
          quaternion (.getWorldQuaternion (.-current wrapper) (new three/Quaternion))
          nqx (.-x quaternion)
          nqy (.-y quaternion)
          nqz (.-z quaternion)
          nqw (.-w quaternion)
          new-state {:nickname nickname :avatar avatar :located located :x nx :y ny :z nz :qx nqx :qy nqy :qz nqz :qw nqw}]
      (when
       (and
        nickname located
        (or
         (not= nx x) (not= ny y) (not= nz z)
         (not= nqx qx) (not= nqy qy) (not= nqz qz) (not= nqw qw)))
        (do
          (dispatch [:assoc-in [:player] new-state])
          (dispatch [:send {:id "movement" :player new-state}]))))))

(defn player []
  (let [{:keys [nickname avatar] :as player} @(subscribe [:get-in [:player]])
        avatar @(subscribe [:get-in [:player :avatar]])
        nickname @(subscribe [:get-in [:player :nickname]])
        wrapper (react/useRef)]
    (fiber/useFrame (record-player-movement wrapper player))
    [:group {:ref wrapper}
     [model/player
      {:nickname nickname
       :avatar avatar}]]))