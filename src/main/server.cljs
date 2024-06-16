
(ns main.server
  (:require [main.npc :refer [llama3 ask-edn ask-json experiment]]
            ["node:process" :as process]
            ["express" :as express]
            ["http" :as http]
            ["ws" :as ws]
            [cognitect.transit :as t]
            ))

(set! *warn-on-infer* false)

(def app (express))
(def server (http/createServer app))
(def wss (new ws/WebSocketServer #js {:noServer true}))

(defn broadcast-to-all [data]
  (.forEach
    (.-clients wss)
    (fn [client] (.send client (t/write (t/writer :json) data)))))

(defn ws-handler [{:keys [id] :as data}]
  (case id
    "movement" (broadcast-to-all data)
    (js/console.log "Unknown websocket message: "data))
  )

(defn ^:export init []

  (.use app "/" (.static express "public/app"))

  (.on server "upgrade"
       (fn [request socket head]
         (.handleUpgrade wss request socket head (fn [ws] (.emit wss "connection" ws request)))
         ))
    
  (.on wss "connection"
       (fn [ws]
         (.on ws "error" (.-error js/console))
         (.on ws "message"
              (fn [raw-data]
                (let [data (t/read (t/reader :json) raw-data)]
                  (ws-handler data))
                ))
         (.send ws (t/write (t/writer :json) {:test "Message"}))))
  
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
