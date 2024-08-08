(ns com.geistindersh.mandelbrot.core
  (:require [clj-async-profiler.core :as prof]
            [com.geistindersh.mandelbrot.color-map :as colors]
            [com.geistindersh.mandelbrot.image :as image]
            [com.geistindersh.mandelbrot.options :as opt])
  (:gen-class)
  (:import (java.awt Color)))

(defn -main [& _]
  (prof/start {:event :cpu})
  (let [option (opt/make-options -1.0 0.0 5000 0.0 1.0 5000)
        color  (->> (colors/vec->ColorMap [(Color. (float 0) (float 0) (float 0.2))
                                           Color/BLUE
                                           Color/LIGHT_GRAY
                                           (Color. (float 0.9) (float 0.7) (float 0.4))
                                           Color/GRAY]
                                          8)
                    (:pairs)
                    (mapv #'second))]
    (dotimes [_ 10]
      (time (image/create-mandelbrot-png "example/png/smooth.png" option color)))
    (println (.toString (prof/stop {:generate-flamegraph? true})))
    ))