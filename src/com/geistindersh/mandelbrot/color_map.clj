(ns com.geistindersh.mandelbrot.color-map
  (:require [com.geistindersh.mandelbrot.utils :as utils])
  (:import (java.awt Color)))

(defrecord ColorMap [pairs])

(defn- coerce-in
  "Coerce the given value between [min-val max-val], or return the value
   if it's already between the two"
  {:added "0.2.3"}
  [value min-val max-val]
  (cond
    (<= value min-val) min-val
    (>= value max-val) max-val
    :else value))

(defn- colors-between
  "Generate a sequence of colors between the given start and end colors"
  [step-count [^Color start ^Color end]]
  (let [red-start   (.getRed start)
        red-step    (/ (- (.getRed end) red-start)
                       step-count)
        green-start (.getGreen start)
        green-step  (/ (- (.getGreen end) green-start)
                       step-count)
        blue-start  (.getBlue start)
        blue-step   (/ (- (.getBlue end) blue-start)
                       step-count)]
    (->> (range step-count)
         (map (fn [i]
                (let [r (+ (* red-step i) red-start)
                      g (+ (* green-step i) green-start)
                      b (+ (* blue-step i) blue-start)]
                  (Color. (int (coerce-in r 0 255))
                          (int (coerce-in g 0 255))
                          (int (coerce-in b 0 255))
                          255)))))))

(defn vec->ColorMap
  {:added "0.2.3"}
  ([colors step-count]
   {:pre [(>= (count colors) 2)]}
   (let [func       (partial colors-between step-count)
         steps      (into []
                          (comp
                            (utils/window 2)
                            (map func)
                            cat)
                          colors)
         color-step (double (/ 1.0
                               (dec (count steps))))]
     (->> steps
          (map-indexed (fn [id color]
                         [(double (* id color-step)) color]))
          (vec)
          (->ColorMap))))
  ([colors]
   (vec->ColorMap colors 4)))

(defn linear-interpolation
  "Interpolate a new value between the two vertexes with a given
   alpha value.
   https://en.wikipedia.org/wiki/Linear_interpolation#Programming_language_support"
  {:added "0.2.3"}
  [^double v0 ^double v1 ^double alpha]
  (unchecked-byte (+ (* v0 (- 1 alpha))
                     (* v1 alpha))))