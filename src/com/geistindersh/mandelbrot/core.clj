(ns com.geistindersh.mandelbrot.core
  (:gen-class)
  (:require
    [com.geistindersh.mandelbrot.gradient :as gradient]
    [com.geistindersh.mandelbrot.image :as image]
    [com.geistindersh.mandelbrot.options :as opt]))

(defn -main [& _]
  (let [options (opt/make-options -1.0 0.0 5000 0.0 1.0 5000)]
    (time
      (image/create-mandelbrot-png "example/png/smooth-small.png"
                                   options
                                   @gradient/navy-gold-gradient))
    (shutdown-agents)))