(ns main.components.welcome
  (:require
   ["@mantine/core" :refer [Modal]]
   ["howler" :refer [Howl]]
   [re-frame.core :as rf :refer [subscribe dispatch]]))

(defonce lobby-music
  (new Howl #js {:src #js ["/ost/through_the_ether.mp3"]
                 :loop true
                 :html5 true
                 ;:autoplay true
                 }))

(defn welcome-modal [tos? status]
  [:> Modal {:opened (and (not= status "connected") (not tos?))
                 :onClose (fn []
                            (.play lobby-music)
                            (dispatch [:assoc-in [:tos?] true])) :title "Disclaimer" :centered "centered"}
       [:p "Adventure.io is an experimental onchain game engine made by the team at " [:a {:href "https://guild.xyz" :target "_blank"} "Guild.xyz"]]])