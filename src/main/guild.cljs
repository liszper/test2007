
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

(defonce do-timer 
  (js/setInterval

    (fn []
       
      (when false;true
         
      (dispatch 
        [:wait 
         {:when logged-in? 
          :fn (fn [{:keys [address signer-fn]}] (.getMemberships user-client address signer-fn))
          :then #(dispatch [:assoc-in [:guilds :joined] (js->clj % :keywordize-keys true)]) ;bad design pattern
          :log #(js/console.log (str "success:" (js->clj % :keywordize-keys true)))
          :catch #(js/console.log (str "Error: " (js->clj %)))}])
        
      (dispatch 
        [:wait 
         {:when logged-in? 
          :fn (fn [{:keys [address signer-fn]}] (.getPoints user-client address signer-fn))
          :then #(dispatch [:assoc-in [:guilds :points] (js->clj % :keywordize-keys true)]) ;bad design pattern
          :log #(js/console.log (str "success:" (js->clj % :keywordize-keys true)))
          :catch #(js/console.log (str "Error: " (js->clj %)))}])
        )

      )
    
    10000))
