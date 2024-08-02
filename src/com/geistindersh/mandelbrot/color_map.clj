(ns com.geistindersh.mandelbrot.color-map
  (:require [com.geistindersh.mandelbrot.utils :as utils])
  (:import (java.awt Color)))

(set! *warn-on-reflection* true)

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
         color-step (/ 1.0 (dec (count steps)))]
     (->> steps
          (map-indexed (fn [id color]
                         [(* id color-step) color]))
          (vec)
          (->ColorMap))))
  ([colors]
   (vec->ColorMap colors 4)))

(defn- linear-interpolation-int
  "Interpolate a new value between the two vertexes with a given
   alpha value.
   https://en.wikipedia.org/wiki/Linear_interpolation#Programming_language_support"
  {:added "0.2.3"}
  [v0 v1 alpha]
  (let [v0    (float v0)
        v1    (float v1)
        alpha (float alpha)]
    (mod (+ (* v0 (- 1 alpha))
            (* v1 alpha))
         255)))

(defn- linear-interpolation
  "Interpolate the color between the two given colors"
  {:added "0.2.3"}
  ([^Color c0 ^Color c1 alpha]
   (let [r (linear-interpolation-int (.getRed c0) (.getRed c1) alpha)
         g (linear-interpolation-int (.getGreen c0) (.getGreen c1) alpha)
         b (linear-interpolation-int (.getBlue c0) (.getBlue c1) alpha)
         a (linear-interpolation-int (.getAlpha c0) (.getAlpha c1) alpha)]
     (Color. ^int r ^int g ^int b ^int a))))

(defn get-at
  "Attempt to get a Color from the given ColorMap at the index,
   or the next closed one to it"
  {:added "0.2.3"}
  [color-map index]
  (let [{:keys [pairs]} color-map
        bounded-value (coerce-in index 0.0 1.0)
        [start stop] (nth (into []
                                (comp
                                  (utils/window 2)
                                  (keep (fn [pair]
                                          (let [lower (nth (nth pair 0) 0)
                                                upper (nth (nth pair 1) 0)]
                                            (when (<= lower index upper)
                                              pair)))))
                                pairs)
                          0)
        fraction      (float (/ (- bounded-value (first start))
                                (- (first stop) (first start))))]
    (linear-interpolation (second start) (second stop) fraction)))