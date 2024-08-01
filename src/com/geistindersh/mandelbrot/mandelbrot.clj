(ns com.geistindersh.mandelbrot.mandelbrot
  (:require
    [clojure.math :as math]
    [com.geistindersh.mandelbrot.color-map :as color])
  (:import
    (java.awt Color)))

(defn mandelbrot
  "Calculate the mandelbrot value, based on the given constants"
  {:added "0.1.1"}
  [cx cy limit]
  (loop [i  (int 0)
         xn (float 0.0)
         yn (float 0.0)]
    (let [temp (float (* xn yn))
          xn2  (float (* xn xn))
          yn2  (float (* yn yn))]
      (if (or (> (+ xn2 yn2) 16.0)
              (>= i limit))
        i
        (recur (inc i)
               (+ (- xn2 yn2) cx)
               (+ temp temp cy))))))

(defn create-buffer
  "Generate a 1-D array of size x-res * y-res, and calculate the mandelbrot
   value for each entry."
  {:added "0.1.1"}
  [options]
  (let [{:keys [x-min y-min y-res x-res x-delta y-delta limit]} options
        x-vec (into []
                    (comp
                      (map #(+ x-min %))
                      (map #(* x-delta %)))
                    (range x-res))]
    (into []
          (comp
            (map #(+ y-min %))
            (map #(* y-delta %))
            (map (fn [cy]
                   (into [] (map #(mandelbrot % cy limit)) x-vec)))
            cat)
          (range y-res))))

(defn create-bitmap-byte-buffer
  "Create a byte-array mapping to the pixel color values for the mandelbrot image.
   The pixels in the buffer is allocated for RGBA 8888 images."
  {:added "0.2.4"}
  [options color-map]
  (let [{:keys [limit]} options
        buffer        (create-buffer options)
        lower-bound   (apply min buffer)
        upper-bound   (inc (apply max buffer))
        dist          (- upper-bound lower-bound)
        color-options (into []
                            (comp
                              (map #(math/pow (/ % dist) 0.5))
                              (map #(color/get-at color-map %)))
                            (range dist))]
    (->> buffer
         (into []
               (comp
                 (map (fn [val]
                        (if (>= val limit)
                          Color/BLACK
                          (get color-options (- val lower-bound)))))
                 (keep (fn [val]
                         (when (some? val)
                           val)))
                 (map (fn [color]
                        [(unchecked-byte (* 255 (.getRed color)))
                         (unchecked-byte (* 255 (.getGreen color)))
                         (unchecked-byte (* 255 (.getBlue color)))
                         -1]))
                 cat))
         (byte-array))))
