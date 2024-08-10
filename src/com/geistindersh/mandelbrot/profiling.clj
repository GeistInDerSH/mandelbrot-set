(ns com.geistindersh.mandelbrot.profiling
  (:require
    [clj-async-profiler.core :as prof]))

(defmacro with-profiling [& body]
  `(do
     (prof/start {:event :cpu})
     ~@body
     (println (.toString (prof/stop {:generate-flamegraph? true})))))
