(ns com.geistindersh.mandelbrot.color-map
  (:import (java.awt Color)))

(defrecord ColorMap [pairs])

(defn vec->ColorMap
  {:added "0.2.3"}
  ([colors steps]
   {:pre [(>= (count colors) 2)]}
   (let [steps      (->> colors
                         (partition 2 2)
                         (map (fn [[start end]]
                                (let [red-start   (.getRed start)
                                      green-start (.getGreen start)
                                      blue-start  (.getBlue start)
                                      red-step    (quot (- (.getRed end) red-start)
                                                        steps)
                                      green-step  (quot (- (.getGreen end) green-start)
                                                        steps)
                                      blue-step   (quot (- (.getBlue end) blue-start)
                                                        steps)]
                                  (->> (range 0 steps)
                                       (map #(Color. (int (+ (* red-step %) red-start))
                                                     (int (+ (* green-step %) green-start))
                                                     (int (+ (* blue-start %) blue-step))
                                                     255)))
                                  )))
                         (flatten)
                         (vec))
         color-step (/ 1.0 (dec (count steps)))]
     (->> steps
          (map-indexed (fn [id color]
                         [(* id color-step) color]))
          (vec)
          (->ColorMap))))
  ([colors]
   (vec->ColorMap colors 4)))

(defn- coerce-in
  "Coerce the given value between [min-val max-val], or return the value
   if it's already between the two"
  {:added "0.2.3"}
  [value min-val max-val]
  (cond
    (<= value min-val) min-val
    (>= value max-val) max-val
    :else (float value)))

(defn- linear-interpolation-int
  "Interpolate a new value between the two vertexes with a given
   alpha value.
   https://en.wikipedia.org/wiki/Linear_interpolation#Programming_language_support"
  {:added "0.2.3"}
  [v0 v1 alpha]
  (let [v0   (float v0)
        v1   (float v1)
        frac (float alpha)]
    (mod (+ (* v0 (- 1 frac))
            (* v1 frac))
         255)))

(defn- linear-interpolation
  "Interpolate the color between the two given colors"
  {:added "0.2.3"}
  ([c0 c1 alpha]
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
        [start stop] (->> pairs
                          (partition 2 1)
                          (filter (fn [[start stop]]
                                    (<= (first start) index (first stop))))
                          (first))
        fraction      (float (/ (- bounded-value (first start))
                                (- (first stop) (first start))))]
    (linear-interpolation (second start) (second stop) fraction)))