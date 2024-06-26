
(ns main.guild
  (:require ["@guildxyz/sdk" :as guild]
            [re-frame.core :as rf :refer [subscribe dispatch reg-fx]]
  ))


(defonce Guild (guild/createGuildClient "test"))
(defonce user-client (.-user Guild))
(defonce guild-client (.-guild Guild))
(defonce role-client (.-role Guild))
(defonce reward-client (.-reward Guild))
(defonce admin-client (.-admin Guild))

(defn logged-in? [{:keys [address status guilds]}] 
  (and address (= status "connected") 
       (not guilds)
       ))

(defn join-guild []
  (js/console.log "Entering the Onchain guild..")
  (dispatch 
    [:wait 
     {:when logged-in? 
      :fn (fn [{:keys [address signer-fn]}]
            (js/console.log "Getting the points..") 
            (js/console.log user-client) 
            (js/console.log address) 
            (js/console.log signer-fn) 
            (.getPoints user-client address signer-fn))
      ;:fn (fn [{:keys [address signer-fn]}] (.getMemberships user-client address signer-fn))
      :then #(do (js/console.log "Saving points..") (dispatch [:assoc-in [:guilds :points] (js->clj % :keywordize-keys true)])) ;bad design pattern
      :log #(js/console.log (str "success:" (js->clj % :keywordize-keys true)))
      :catch #(js/console.log (str "Error: " (js->clj %)))}]))
