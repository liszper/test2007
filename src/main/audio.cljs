(ns main.audio
  (:require ["howler" :refer [Howl]]))

(def ^:private ^js music-instance (atom nil))

(defn initialize-music [ost]
  (when-not @music-instance
    (reset! music-instance (Howl. #js {:src (clj->js ost) :loop true}))
    (.play ^js @music-instance)))

(defn stop-music []
  (when @music-instance
    (.stop ^js @music-instance)
    (.unload ^js @music-instance)
    (reset! music-instance nil)))

(defn change-music [new-ost]
  (stop-music)
  (initialize-music new-ost))