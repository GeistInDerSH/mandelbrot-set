(ns com.geistindersh.mandelbrot.core
  (:require [com.geistindersh.mandelbrot.color-map :as color]
            [com.geistindersh.mandelbrot.image :as img]
            [com.geistindersh.mandelbrot.options :as opt])
  (:gen-class))

(set! *warn-on-reflection* true)

(defn -main [& _]
  (let [option (opt/make-options -1.0 0.0 5000 0.0 1.0 5000)]
    (time
      (img/create-mandelbrot-png "example/png/ppr.png" @color/pink-purple-100 option))))