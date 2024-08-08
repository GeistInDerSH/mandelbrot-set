(ns com.geistindersh.mandelbrot.mandelbrot
  (:require
    [clojure.math :as math]
    [com.geistindersh.mandelbrot.color-map :as colors]
    [com.geistindersh.mandelbrot.color-map])
  (:import
    (java.awt Color)))

(def log-2 (math/log 2))
(def xyn2-limit (double (bit-shift-left 1 16)))

(defn mandelbrot
  "Calculate the mandelbrot value, based on the given constants"
  {:added "0.1.1"}
  ^"java.lang.Double" [cx cy limit]
  (loop [i  0
         xn (double 0.0)
         yn (double 0.0)]
    (let [xn2 (double (* xn xn))
          yn2 (double (* yn yn))]
      (cond
        (and (<= (+ xn2 yn2) xyn2-limit)
             (< i limit)) (recur (inc i)
                                 (+ (- xn2 yn2)
                                    cx)
                                 (+ (* 2 xn yn)
                                    cy))
        (< i limit) (let [log-zn (/ (math/log (+ xn2 yn2)) 2)
                          nu     (/ (math/log (/ log-zn log-2))
                                    log-2)]
                      (- (inc i)
                         nu))
        :else i))))

(defn get-rgba-for-pixel
  "Get the red-green-blue-alpha value for the given x y pixel"
  [colors x y limit]
  (let [val       (double (mandelbrot x y limit))
        alpha     (double (mod val 1))
        index     (int (math/floor val))
        ^Color c0 (nth colors index Color/BLACK)
        ^Color c1 (nth colors (inc index) Color/BLACK)
        r         (int (colors/linear-interpolation-int (.getRed c0) (.getRed c1) alpha))
        g         (int (colors/linear-interpolation-int (.getGreen c0) (.getGreen c1) alpha))
        b         (int (colors/linear-interpolation-int (.getBlue c0) (.getBlue c1) alpha))]
    [r g b -1]))

(defn create-bitmap-byte-buffer
  "Create a byte-array mapping to the pixel color values for the mandelbrot image.
   The pixels in the buffer is allocated for RGBA 8888 images."
  ^"[B" [options colors]
  (let [{:keys [x-res y-res x-min y-min x-delta y-delta limit]} options
        y-range  (into []
                       (comp
                         (map #(* % y-delta))
                         (map #(+ % y-min)))
                       (range y-res))
        rgb-func (partial get-rgba-for-pixel colors)]
    (->> (range x-res)
         (into []
               (comp
                 (map #(* % x-delta))
                 (map #(+ % x-min))
                 (map (fn [x]
                        (into [] (map #(rgb-func x % limit)) y-range)))
                 cat
                 cat))
         (byte-array))))
