
(ns main.server
  (:require ["express" :as express]
            ["ws" :as ws]
            [cognitect.transit :as t]
            ))

(set! *warn-on-infer* false)

(def app (express))

(defn ^:export init []
  (.use app "/"
        (.static express "public/app")
        ;(fn [req res] (.send res "Hello World"))
        )
  (.listen app 5000)
    
  (let [wss (new ws/WebSocketServer #js {:port 8080 :path "/join"})]
    
    (.on 
      wss
      "connection"
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
    
    )
  
    (js/console.log "Server started!"))
