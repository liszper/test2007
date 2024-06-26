
(ns main.server
  (:require [main.npc :refer [llama3 ask-edn ask-json experiment]]
            ;[main.worlds :refer [worlds]]
            ["node:process" :as process]
            ["express" :as express]
            ["http" :as http]
            ["ws" :as ws]
            [cognitect.transit :as t]
            ))

(defn fmap [f m] (into {} (for [[k v] m] [k (f v)])))

(set! *warn-on-infer* false)

(def db (atom {}))
(def prev-db (atom {}))
(def prev-logged-db (atom {}))

(def private-db (atom {}))

(def app (express))
(def server (http/createServer app))
(def wss (new ws/WebSocketServer #js {:noServer true}))

(defn ws-handler [{:keys [id player] :as data} user-id {:keys [user-agent ip]}]
  (case id
    "movement" (swap! db assoc-in [:players (:located player) user-id] (assoc player :id user-id))
    nil))

(defn logging []
  (js/setInterval
    (fn []
      (when (not= @db @prev-logged-db)
        (js/console.log (str @private-db))
        (js/console.log (str @db))
        (reset! prev-logged-db @db)))
    1000))

(defn ^:export init []

  ;(logging)

  (js/setInterval
    (fn []
      (when (not= @db @prev-db)
        (doseq [[id {:keys [client]}] (:players @private-db)]
          (.send client (t/write (t/writer :json)
                                            (-> @db
                                              (assoc :id "movement")
                                              (update :players (fn [m] (fmap #(dissoc % id) m)))
                                              )))
          )
        (reset! prev-db @db)))
    1;17
    )

  (.use app "/" (.static express "public/app"))

  (.on server "upgrade"
       (fn [request socket head]
         (.handleUpgrade wss request socket head (fn [ws] (.emit wss "connection" ws request)))))
    
  (.on wss "connection"
       (fn [ws req]
         (let [new-user-id (get (js->clj (.-headers req)) "sec-websocket-key")
               new-user {:user-agent (get (js->clj (.-headers req)) "user-agent")
                         :ip (.-remoteAddress (.-_socket ws))
                         :client ws}]
          (swap! private-db assoc-in [:players new-user-id] new-user)
          (js/console.log "Websocket connection initiated..")
          (.on ws "error" (.-error js/console))
          (.on ws "close" (fn  [req]
                            (swap! db update :players (fn [m] (fmap #(dissoc % new-user-id) m)))
                            (swap! private-db update-in [:players] dissoc new-user-id)
                            (js/console.log "Websocket connection closed..")))
          (.on ws "message" (fn [data] (ws-handler (t/read (t/reader :json) data) new-user-id new-user)))
          )
         ))
  
  (.listen server (or (.-PORT (.-env process)) 5000))

  ;(llama3 "Write a greeting in ASCII art. Don't explain what you are doing just send me the ASCII art and the greeting. Be nerdy and funny, your audience is programmers.")
  
  ;(experiment)
  ;(ask-json "How are you?")
  ;(ask-json "Write me a poem")
  ;(ask-json "Create a simple data structure")
  ;(ask-json "Write me a very complex, invalid json file")
  ;(ask-edn "Write me a poem")
  ;(ask-edn "Give me a weather prediction for August")
  )
