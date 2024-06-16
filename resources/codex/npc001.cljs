
(ns main.npc
  (:require ["ollama" :as ollama]
            [clojure.edn :as edn]
            [cognitect.transit :as t]
            [clojure.core.async :as async]
            [clojure.core.async.interop :as async-interop]
            ))

(defmacro safe
  [& body]
  `(cljs.core.async.macros/go
     (try (cljs.core.async.interop/<p! ~@body)
          (catch js/Error e# (println (ex-cause e#))))))

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

(defn validate-or-fix-json-errors [response]
  (js/console.log "Validating JSON code: "response)
  
  (->> 

    (try (.parse js/JSON response)
         (catch js/Error e
           (do 
             (js/console.log "Error: "e)
             (js/console.log "Trying to fix it..")
             (llm
               (str
                 "I got this response that was supposed to be JSON:"response
                 ", but parsing this text into JSON threw this error: "e
                 " please help me fix the original JSON. RESPOND WITH THE JSON FIXED, DON'T SAY ANYTHING ELSE")
               validate-or-fix-json-errors)
             )))

    js/console.log

    )
  )

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

(defn validate-or-fix-json-errors-with-history [chat-history]
  (let [response (:content (last chat-history))]
    (js/console.log "The JSON in question:")
    (js/console.log (str response))
    (->> 
    (try (.parse js/JSON response)
         (catch js/Error e
           (do 
             (js/console.log "Error: "e)
             (js/console.log "Trying to fix it..")
             
             (llx
               (conj (vec (if 
                            (= (:role (first chat-history)) "user")
                            (rest chat-history)
                            (-> chat-history rest vec rest)
                            )
                          )
                     {:role "user"
                      :content (str
                                 "This is not a valid JSON file, when I'm trying to parse it into JSON, it throws this error: "e
                                 " please help me fix it. and RESPOND WITH THE JSON FIXED, DON'T SAY ANYTHING ELSE")}
                     )
               validate-or-fix-json-errors-with-history)
             )))
    js/console.log)))

(defn semantic-validation [original response]
      (llm
        (str
                 
          "Here is the original invalid JSON with a syntax error: "original " "
          "And here is the valid, fixed version of the JSON: "response
          "Please check if the content of the two JSON are similar and if they only differ in the fixed syntax or in semantic too."
          "Respond with the valid version of the JSON if they are the same, or if they are different rewrite the valid version to make its meaning exact to the original, invalid version but with valid syntax now."
          "Respond with the new JSON, DON'T SAY ANYTHING ELSE." 
          )
        (fn validate-content [response]
          (js/console.log response)
          )
        )
  )

(def json-knowledge
  "JSON Stands for JavaScript Object Notation, It is lightweight format for storing and transporting data through API from server to a web page. It is also 'self describing' and easy to understand.

Example:

Alt Text

JSON Data types :
1. JSON Strings
{ \"name\":\"Ajay\"}
Here \"name\" is a key and \"Ajay\" is its
value.

2. JSON Numbers
 { \"Age\":34}
Here \"Age\" is a key and 34 is value, which is a
number type.

3. JSON Objects
{ \"friends\":[
        {
            \"name\":\"Shyam singh\",
            \"age\":25,
            \"email\":\"ram@gmail.com\"
        },
        {
            \"name\":\"Rahul roy\",
            \"age\":26,
            \"dob\":\"23/12/1996\",
            \"isFriend\":true
        }
    ] }
here \"friends\" is a key and its values are in array data type
which contains two objects. His first friend is shyam and
second frind is Rahul.

4. JSON Arrays
{\"hobbies\":[\"Learn to code\", \"Paint\", \"Blogging\", \"Writing\"]}
Here \"hobbies\" is a key and it has multiple values in array
format separated by commas.

5. JSON Booleans
\"isFriend\":true
Here \"isFriend\" is a key and its value in Boolean format which
can be either true or false.

6. JSON Null
{\"middlename\":null}
\"middlename\" is a key and its value in null format. if something has no value than we can assign null to its value for example some people have no middle name in that case we can assign null value."
  )

(defn validate-json [original response]
  (js/console.log "Validating JSON code: "response)
  (when-let [result 
        (try (.parse js/JSON response)
         (catch js/Error e
           (do 
             (js/console.log "Error: "e)
             (js/console.log "Trying to fix it..")
             (llm
               (str
                 "Learn more about JSON: "json-knowledge
                 "Here is an invalid JSON with a syntax error: "response
                 "This object isn't valid JSON. Specifically at the part:" e " "
                 "Please help me fix it. Respond with the valid version of the JSON, DON'T SAY ANYTHING ELSE, NOT EVEN A WORD")
               #(validate-json original %))
             )))
        ]
    (js/console.log result)
    ))
  

(defn ask-json [message]
  (llm
    (str "THE QUESTION: "message " INSTRUCTION: very important that you respond in the JSON data format, and that only. ONLY RESPOND IN JSON, DON'T SAY ANYTHING ELSE")
    #(validate-json message %)))

(defn ask-json2 [message]
  (llx
    [{:role "user" :content (str "THE QUESTION: "message " INSTRUCTION: very important that you respond in the JSON data format, and that only. ONLY RESPOND IN JSON, DON'T SAY ANYTHING ELSE")}] 
    validate-or-fix-json-errors-with-history))
