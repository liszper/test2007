
(ns main.guild
  (:require ["@guildxyz/sdk" :as guild]
            [re-frame.core :as rf :refer [subscribe dispatch reg-fx]]
  ))


(def Guild (guild/createGuildClient "test"))
(def user-client (.-user Guild))
(def guild-client (.-guild Guild))
(def role-client (.-role Guild))
(def reward-client (.-reward Guild))
(def admin-client (.-admin Guild))

(defn logged-in? [{:keys [address status guilds]}] 
  (and address (= status "connected") 
       (not guilds)
       ))

(defn join-guild []
  (js/console.log "Entering the Onchain guild..")
  (dispatch 
    [:wait 
     {:when logged-in? 
      :fn (fn [{:keys [address signer-fn]}] (.getPoints user-client address signer-fn))
      :then #(dispatch [:assoc-in [:guilds :points] (js->clj % :keywordize-keys true)]) ;bad design pattern
      :log #(js/console.log (str "success:" (js->clj % :keywordize-keys true)))
      :catch #(js/console.log (str "Error: " (js->clj %)))}]))

(defn fetch-guild []
  (js/setInterval

    (fn []
       
      (js/console.log "Entering the Onchain guild..")
         
      ;(dispatch 
      ;  [:wait 
      ;   {:when logged-in? 
      ;    :fn (fn [{:keys [address signer-fn]}] (.getMemberships user-client address signer-fn))
      ;    :then #(dispatch [:assoc-in [:guilds :joined] (js->clj % :keywordize-keys true)]) ;bad design pattern
      ;    :log #(js/console.log (str "success:" (js->clj % :keywordize-keys true)))
      ;    :catch #(js/console.log (str "Error: " (js->clj %)))}])
        
      (dispatch 
        [:wait 
         {:when logged-in? 
          :fn (fn [{:keys [address signer-fn]}] (.getPoints user-client address signer-fn))
          :then #(dispatch [:assoc-in [:guilds :points] (js->clj % :keywordize-keys true)]) ;bad design pattern
          :log #(js/console.log (str "success:" (js->clj % :keywordize-keys true)))
          :catch #(js/console.log (str "Error: " (js->clj %)))}])

      )
    
    10000))
