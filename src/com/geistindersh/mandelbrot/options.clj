(ns com.geistindersh.mandelbrot.options)

(defrecord
  ^{:added "0.1.1"}
  Options [x-min x-max x-res y-min y-max y-res limit x-delta y-delta])

(defn make-options
  "A convenience function for creating Options"
  {:added "0.1.1"}
  ([x-min x-max x-res y-min y-max y-res limit]
   (let [dx (quot (- x-max x-min)
                  (dec x-res))
         dy (quot (- y-max y-min)
                  (dec y-res))]
     (->Options x-min x-max x-res y-min y-max y-res limit dx dy)))
  ([x-min x-max x-res y-min y-max y-res]
   (make-options x-min x-max x-res y-min y-max y-res 128))
  ([]
   (make-options -1.0 1.0 1000
                 -1.0 1.0 1000)))
