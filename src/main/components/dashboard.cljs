(ns main.components.dashboard
  (:require
   [main.wagmi :refer [connect-kit]]
   [main.guild :refer [join-guild]]
   ["@mantine/core" :refer [Stack Button]]
   [re-frame.core :as rf :refer [subscribe dispatch]]))

(defn dashboard []
  (let [status @(subscribe [:get-in [:status]])
        guild-data @(subscribe [:get-in [:guilds :points]])
        avatar @(subscribe [:get-in [:player :avatar]])
        avatars @(subscribe [:get-in [:avatars]])]
    [:> Stack {:align "stretch" :h "100%" :justify "space-between"}
     [:p "Hint: Try changing the blockchain"]
     [:f> connect-kit]
     ;(when (and (= status "connected") (nil? guild-data)) [:> Button {:onClick #(join-guild)} "Join the Onchain guild"])
     (when (= status "connected")
       (case avatar
         :wizard [:> Button {:onClick #(dispatch [:assoc-in [:player :avatar] (:ghost avatars)])} "Turn into a Ghost"]
         :ghost [:> Button {:onClick #(dispatch [:assoc-in [:player :avatar] (:wizard avatars)])} "Become a Wizard again"]
         nil))]
    ))