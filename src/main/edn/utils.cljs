(ns main.edn.utils
  (:require ["prando" :refer [default]]))

(defn random [seed] (-> seed js/Math.sin (* 10000) js/Math.floor))
(def prando (new default "new2"))
(defn prand-int [x y] (.nextInt prando x y))
(defn prand [x y] (.next prando x y))
