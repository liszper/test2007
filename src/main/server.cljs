
(ns main.server
  (:require ["node:process" :as process]
            ["express" :as express]
            ["http" :as http]
            ["ws" :as ws]
            [cognitect.transit :as t]
            ))

(set! *warn-on-infer* false)

(def app (express))

(defn ^:export init []

  (.use app "/" (.static express "public/app"))

  (let [server (http/createServer app)
        wss (new ws/WebSocketServer #js {:noServer true})]
    
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
                    ;(js/console.log (str "received: " data))
                    (.forEach
                      (.-clients wss)
                      (fn [client] (.send client (t/write (t/writer :json) data))))
                    )
                  ))
           (.send ws (t/write (t/writer :json) {:test "Message"}))))
  
    (.listen server (or (.-PORT (.-env process)) 5000))
    )
  
  (js/console.log "Server started!"))
