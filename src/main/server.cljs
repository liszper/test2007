
(ns main.server
  (:require [main.npc :refer [llama3 ask-edn ask-json experiment]]
            ;[main.worlds :refer [worlds]]
            ["node:process" :as process]
            ["express" :as express]
            ["http" :as http]
            ["ws" :as ws]
            [cognitect.transit :as t]
            ))

(set! *warn-on-infer* false)

(def db (atom {}))
(def prev-db (atom {}))

(def app (express))
(def server (http/createServer app))
(def wss (new ws/WebSocketServer #js {:noServer true}))

(defn get-ip-address [ws] (.-remoteAddress (.-_socket ws)))

(defn scan-all []
  (let [clients (.-clients wss)
        ip-addresses (mapv get-ip-address clients)
        ]
    (js/console.log "IP Addresses: ")
    (js/console.log (clj->js ip-addresses))
    ) 
  )

(defn broadcast-to-all [data]
  (.forEach
    (.-clients wss)
    (fn [client] (.send client (t/write (t/writer :json) data)))))

(defn ws-handler [{:keys [id player position] :as data}]
  (case id
    "movement" (swap! db assoc-in [:players (:located player) (:name player) :position] position);(broadcast-to-all data)
    (js/console.log "Unknown websocket message: "data))
  )

(defn ^:export init []


  (js/setInterval
    (fn []
      (when (not= @db @prev-db)
        (broadcast-to-all (assoc @db :id "movement"))
        (reset! prev-db @db)))
    17)

  (.use app "/" (.static express "public/app"))

  (.on server "upgrade"
       (fn [request socket head]
         (.handleUpgrade wss request socket head (fn [ws] (.emit wss "connection" ws request)))))
    
  (.on wss "connection"
       (fn [ws req]
         ;(scan-all)
         (.on ws "error" (.-error js/console))
         (.on ws "close" (fn  [req] (js/console.log "Websocket connection closed..")))
         (.on ws "message"
              (fn [raw-data]
                (let [data (t/read (t/reader :json) raw-data)
                      ]
                  (ws-handler data))
                ))
         ;(.send ws (t/write (t/writer :json) {:test "Message"}))
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
