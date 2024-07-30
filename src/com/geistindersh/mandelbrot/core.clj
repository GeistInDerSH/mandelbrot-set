(ns com.geistindersh.mandelbrot.core
  (:require [com.geistindersh.mandelbrot.mandelbrot :as mandelbrot]
            [com.geistindersh.mandelbrot.options :as opt])
  (:gen-class))

(defn -main [& _]
  (let [option (opt/make-options -1.0 1.0 15000 -1.0 1.0 15000)]
    (time
      (mandelbrot/create-buffer option))))