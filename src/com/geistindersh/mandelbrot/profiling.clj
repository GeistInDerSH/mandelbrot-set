(ns com.geistindersh.mandelbrot.profiling
  (:require
    [clj-async-profiler.core :as prof]))

(defmacro with-profiling
  "Enable profiling of the body of the code, and generate a flamegraph of
   the result"
  [& body]
  `(do
     (prof/start {:event :cpu})
     ~@body
     (println (.toString (prof/stop {:generate-flamegraph? true})))))
