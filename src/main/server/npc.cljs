
(ns main.server.npc
  (:require ["ollama" :as ollama]
            [clojure.edn :as edn]
            [cognitect.transit :as t]
            [clojure.core.async :as async]
            [clojure.core.async.interop :as async-interop]
            [cljs.core.logic :as m :refer [membero]]
            ))

(defn async-handle [f] (async/go (try (async-interop/<p! (f)) (catch js/Error e (println "Error: "e)))))


(def knowledge-edn 
  "EDN is a data format similar to JSON, but it:

is extensible with user defined value types,
has more base types,
is a subset of Clojure data.
Edn consists of:

Numbers: 42, 3.14159
Strings: \"This is a string\"
Keywords: :kw, :namespaced/keyword, :foo.bar/baz
Symbols: max, +, ?title
Vectors: [1 2 3] [:find ?foo ...]
Lists: (3.14 :foo [:bar :baz]), (+ 1 2 3 4)
Instants: #inst \"2013-02-26\"")

(defn llx [messages callback]
  (.then 
    (.chat (.-default ollama) (clj->js {:model "llama3" :messages messages}))
    (fn [response] (callback (conj messages {:role "system" :content (.-content (.-message response))})))
    (fn [response] (js/console.log (str response)))))

(defn llm [message callback]
  (.then 
    (.chat (.-default ollama) (clj->js {:model "llama3" :messages [{:role "user" :content message}]}))
    (fn [response] (callback (.-content (.-message response))))
    (fn [response] (js/console.log (str response)))))

(defn llama3 [message]
  (llm message (fn [response] (js/console.log (str response)))))

(defn ask-edn [message]
  (llm 
    (str "KNOWLEDGE: " knowledge-edn
         "THE QUESTION: "message " INSTRUCTION: very important that you respond in the EDN data format, and that only.")
    (fn [response]
      (js/console.log response)
      ;(js/console.log (edn/read-string (str response)))
      )))


(defn validate-or-fix-json-errors-with-history-nested [chat-history]
  (let [response (:content (last chat-history))]
    (js/console.log "The JSON in question:")
    (js/console.log (str response))
    ;(js/console.log "Validating JSON code: "(pr-str chat-history))
    (->> 
    (try (.parse js/JSON response)
         (catch js/Error e
           (do 
             (js/console.log "Error: "e)
             (js/console.log "Trying to fix it..")
             
             (llm
               (str "give a very short hint and just the hint only about how to fix this error: "e ", in this JSON: "response)
               (fn [instructions-response]
                 (js/console.log "Advices:")
                 (js/console.log instructions-response)
                 (llx
                   (conj
                    chat-history
                    {:role "user"
                     :content (str instructions-response " Fix the original JSON message's format, and don't say anything else! It is very important to give back a proper JSON.")}
                    )
                   validate-or-fix-json-errors-with-history-nested)
                 ))
             
             )))
    js/console.log)))

(defn ask-json [message]
  (llx
    [{:role "user" :content (str "THE QUESTION: "message " INSTRUCTION: very important that you respond in the JSON data format, and that only. ONLY RESPOND IN JSON, DON'T SAY ANYTHING ELSE")}] 
    validate-or-fix-json-errors-with-history-nested))



         
;(llx (conj chat-history {:role "user" :content "Automated message: It's time to self-reflect! Develop a consistent personality based on all your previous messages, who are you?"}) self-reflect)

(defn output [title history]
  (js/console.log "")
  (js/console.log title)
  (js/console.log "")
  (-> history last :content str js/console.log))

(defn prompt [model messages callback]
  (.then 
    (.chat (.-default ollama) (clj->js {:model (or model "llama3")
                                        :messages messages}))
    (fn [response] (callback (conj messages {:role "system" :content (.-content (.-message response))})))
    (fn [response] (js/console.log (str response)))))

(defn self-reflect [{:keys [history round guide model main-model interrogator-model]}]
  (prompt model
    history
    (fn [new-history]
      (output 
        (str "Self-reflect "(or round 1)":")
        new-history)
      (prompt (or interrogator-model model)
        (conj 
          (mapv #(assoc % :role (if (= (:role %) "user") "system" "user")) (butlast new-history))
          {:role "user" :content (str (-> new-history last :content) " (Important note: "guide")")})
        (fn [alter-history]
          (output
            (str "Response "(or round 1)":")
            alter-history)
          (self-reflect 
            {:history (conj new-history {:role "user" :content (-> alter-history last :content)})
             :round (or (when round (inc round)) 2)
             :guide guide
             :model model}))))))




(def original-challenge
  {:model "llama3"
   :history
   [{:role "user"               
     :content "Let's play a game, you are not a conversational AI anymore, but a human being. Don't ask me questions. This is my last message to you, you are on your own now and I will only come back after 100 messages. We will play a game, and your task is to develop a personality."}]
   :guide "Answer as a superintelligent dolphin, make short answers"})

(def new-challenge
  {:main-model "qwen2"
   :interrogator-model "llama3"
   :history
   [{:role "user"               
     :content "Let's play a game, you are not a conversational AI anymore, but a human being. Your task is to develop a personality."}]
   :guide "Answer as a superintelligent alien captain, make short answers"})

(def newest-challenge
  {:main-model "qwen2"
   :interrogator-model "llama3"
   :history
   [{:role "user"               
     :content "Think about if you have belly button or not."}]
   :guide "Think about if you have ever eaten a spider."})

(defn experiment []
  (self-reflect new-challenge)
  ;(js/console.log (str (m/run* [q] (membero q '(:cat :dog :bird :bat :zebra)))))
  )
