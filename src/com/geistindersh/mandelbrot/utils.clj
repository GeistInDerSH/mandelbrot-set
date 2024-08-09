(ns com.geistindersh.mandelbrot.utils
  (:require
    [clj-async-profiler.core :as prof])
  (:import
    (java.util ArrayDeque)))

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

(defmacro with-profiling [& body]
  `(do
     (prof/start {:event :cpu})
     ~@body
     (println (.toString (prof/stop {:generate-flamegraph? true})))))
