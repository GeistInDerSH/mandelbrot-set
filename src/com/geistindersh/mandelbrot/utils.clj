(ns com.geistindersh.mandelbrot.utils
  (:import (java.util ArrayDeque)))

(set! *warn-on-reflection* true)

(defn window
  "A transducer compatible version of partial"
  ([^long n] (window n 1))
  ([^long n ^long step]
   (fn [rf]
     (let [a (ArrayDeque. n)]
       (fn
         ([] (rf))
         ([result] (rf result))
         ([result input]
          (.add a input)
          (if (= n (.size a))
            (let [v (vec (.toArray a))]
              (dotimes [_ step]
                (.removeFirst a))
              (rf result v))
            result)))))))