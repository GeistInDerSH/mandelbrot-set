(ns com.geistindersh.mandelbrot.options)

(defrecord
  ^{:added "0.1.1"}
  Options [width-view-min width-view-max width height-view-min height-view-max height limit width-delta height-delta])

(defn make-options
  "A convenience function for creating Options"
  {:added "0.1.1"}
  ([width-view-min width-view-max width height-view-min height-view-max height limit]
   {:pre [(pos? width)
          (pos? height)
          (<= (* height width) Integer/MAX_VALUE)]}
   (let [dx (/ (- width-view-max width-view-min)
               (double (dec width)))
         dy (/ (- height-view-max height-view-min)
               (double (dec height)))]
     (->Options width-view-min width-view-max width height-view-min height-view-max height limit dx dy)))
  ([width-view-min width-view-max width height-view-min height-view-max height]
   (make-options width-view-min width-view-max width height-view-min height-view-max height 128))
  ([]
   (make-options -1.0 1.0 1000 -1.0 1.0 1000)))

(defn row-constants
  "Eagerly generate all values for the rows of the image"
  [option]
  (let [{:keys [width-delta width-view-min width]} option]
    (into []
          (comp
            (map #(* % width-delta))
            (map #(+ % width-view-min)))
          (range width))))

(defn column-constants
  "Eagerly generate all values for the columns of the image"
  [option]
  (let [{:keys [height-delta height-view-min height]} option]
    (into []
          (comp
            (map #(* % height-delta))
            (map #(+ % height-view-min)))
          (range height))))

(defn image-buffer-size
  "Get the number of bytes in the image"
  [option]
  (* 4
     (:width option)
     (:height option)))