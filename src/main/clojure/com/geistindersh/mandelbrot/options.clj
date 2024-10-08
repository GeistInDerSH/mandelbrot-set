(ns com.geistindersh.mandelbrot.options
  (:import (clojure.lang IPersistentVector)))

(defrecord
  ^{:added "0.1.1"}
  Options [width-view-min width height-view-min height limit width-delta height-delta])

(defn make-options
  "A convenience function for creating Options"
  {:added "0.1.1"}
  ([width-view-min width-view-max width height-view-min height-view-max height limit]
   {:pre [(pos? width)
          (pos? height)
          (<= (* height width) Integer/MAX_VALUE)
          (< width-view-min width-view-max)
          (< height-view-min height-view-max)]}
   (let [dx (/ (- width-view-max width-view-min)
               (double (dec width)))
         dy (/ (- height-view-max height-view-min)
               (double (dec height)))]
     (->Options width-view-min width height-view-min height limit dx dy)))
  ([width-view-min width-view-max width height-view-min height-view-max height]
   (make-options width-view-min width-view-max width height-view-min height-view-max height 128))
  ([]
   (make-options -1.0 1.0 1000 -1.0 1.0 1000)))

(defn row-constants
  "Eagerly generate all values for the rows of the image"
  ^IPersistentVector [options]
  (let [{:keys [width-delta width-view-min width]} options]
    (into []
          (comp
            (map #(* % width-delta))
            (map #(+ % width-view-min)))
          (range width))))

(defn column-constants
  "Eagerly generate all values for the columns of the image"
  ^IPersistentVector [options]
  (let [{:keys [height-delta height-view-min height]} options]
    (into []
          (comp
            (map #(* % height-delta))
            (map #(+ % height-view-min)))
          (range height))))

(defn image-buffer-size
  "Get the number of bytes in the image"
  [options]
  (* 4
     (:width options)
     (:height options)))