(ns main.views.canvas
  (:require
   [reagent.core :as reagent]
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

   [main.components.player :refer [player]]
   [main.components.others :refer [other-players]]
   [main.audio :as audio]))

(def animation-set
  {:idle "Idle",
   :walk "Walk",
   :run "Run",
   :jump "Jump_Start",
   :jumpIdle "Jump_Idle",
   :jumpLand "Jump_Land",
   :fall "Climbing",
   :action1 "Wave",
   :action2 "Dance",
   :action3 "Cheer",
   :action4 "Attack(1h)"})

(def keyboard-controls
  [{:keys ["ArrowUp" "KeyW"] :name "forward"}
   {:keys ["ArrowDown" "KeyS"] :name "backward"}
   {:keys ["ArrowLeft" "KeyA"] :name "leftward"}
   {:keys ["ArrowRight" "KeyD"] :name "rightward"}
   {:keys ["Space"] :name "jump"}
   {:keys ["Shift"] :name "run"}])

(defn canvas [debug?]
  (let [_ @(subscribe [:get-in [:update-physics?]])
        springK @(subscribe [:get-in [:player :avatar :springK]])
        dampingC @(subscribe [:get-in [:player :avatar :dampingC]])
        environment-map @(subscribe [:get-in [:player :located]])
        {:keys [dynamic-environment skybox lights gltf ost control objects]} (get maps environment-map)
        prev-environment-ref (react/useRef nil)] 
    
    (react/useEffect
     (fn []
       (when (not= environment-map (.-current prev-environment-ref))
         (set! (.-current prev-environment-ref) environment-map)
         (when ost
           (audio/change-music ost)))
       
       ;; Cleanup function
       #(audio/stop-music))
     #js [environment-map])
    
    [:> fiber/Canvas {:key environment-map
                      :name environment-map
                      :shadows "shadows"
                      :onPointerDown (fn [e] (.requestPointerLock (.-target e)))
                      ;:gl (fn [canvas] (let [r (new WebGPURenderer {:canvas canvas})] r))
				;r.xr = {
			;		addEventListener: () => {},
		;			removeEventListener: () => {},
		;		}
                      }
     [:> react/Suspense {:fallback (reagent.core/as-element [loading])}
      skybox
      lights

      (when dynamic-environment [dynamic-environment])

      [:> rapier/Physics {:timeStep "vary" :debug (if debug? "debug" nil)}

       (when gltf [:> rapier/RigidBody {:colliders "trimesh" :type "fixed"} [:> drei/Gltf gltf]])

       [:> drei/KeyboardControls {:map keyboard-controls :debug? (if debug? true false) :debug "debug"}
        [:> ecc/default
         (assoc control
                  ;:animated "animated"
                :debug debug?
                  ;:followLight true
                :springK (or springK 1.2)
                :dampingC (or dampingC 0.08)
                :controllerKeys {:forward 12 :backward 13 :leftward 14 :rightward 15 :jump 2})
         [:f> player]]]
       objects]

      [other-players]]

     [:> drei/Stats]]))