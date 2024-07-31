(ns com.geistindersh.mandelbrot.core
  (:require [com.geistindersh.mandelbrot.color-map :as color]
            [com.geistindersh.mandelbrot.image :as img]
            [com.geistindersh.mandelbrot.options :as opt])
  (:gen-class)
  (:import (java.awt Color)))

(defn -main [& _]
  (let [option (opt/make-options -1.0 0.0 1000 0.0 1.0 1000)
        colors (color/vec->ColorMap [Color/RED Color/BLUE Color/GREEN Color/DARK_GRAY])]
    (time
      (img/create-mandelbrot-png "output.png" colors option))))