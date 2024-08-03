(ns com.geistindersh.mandelbrot.options)

(defrecord
  ^{:added "0.1.1"}
  Options [x-min x-max x-res y-min y-max y-res limit x-delta y-delta])

(defn make-options
  "A convenience function for creating Options"
  {:added "0.1.1"}
  ([x-min x-max x-res y-min y-max y-res limit]
   {:pre [(pos? x-res)
          (pos? y-res)
          (<= (* y-res x-res) Integer/MAX_VALUE)]}
   (let [dx (/ (- x-max x-min)
               (double (dec x-res)))
         dy (/ (- y-max y-min)
               (double (dec y-res)))]
     (->Options x-min x-max x-res y-min y-max y-res limit dx dy)))
  ([x-min x-max x-res y-min y-max y-res]
   (make-options x-min x-max x-res y-min y-max y-res 128))
  ([]
   (make-options -1.0 1.0 1000
                 -1.0 1.0 1000)))
