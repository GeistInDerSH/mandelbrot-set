(ns com.geistindersh.mandelbrot.mandelbrot
  (:require
    [clojure.math :as math]
    [com.geistindersh.mandelbrot.color-map :as colors]
    [com.geistindersh.mandelbrot.options :as option])
  (:import
    (java.awt Color)
    (java.util ArrayList)))

(def log-2 (math/log 2))
(def xyn2-limit (double (bit-shift-left 1 16)))

(defn mandelbrot
  "Calculate the mandelbrot value, based on the given constants"
  {:added "0.1.1"}
  ^"java.lang.Double" [^double cx ^double cy ^long limit]
  (loop [i  (long 0)
         xn (double 0.0)
         yn (double 0.0)]
    (let [xn2 (double (* xn xn))
          yn2 (double (* yn yn))]
      (cond
        (and (< (+ xn2 yn2) xyn2-limit)
             (< i limit)) (recur (inc i)
                                 (+ (- xn2 yn2)
                                    cx)
                                 (+ (* (+ xn xn) yn)
                                    cy))
        (< i limit) (let [log-zn (/ (math/log (+ xn2 yn2)) 2)
                          nu     (/ (math/log (/ log-zn log-2))
                                    log-2)]
                      (double (- (inc i)
                                 nu)))
        :else (double i)))))

(defn create-bitmap-byte-buffer
  "Create a byte-array mapping to the pixel color values for the mandelbrot image.
   The pixels in the buffer is allocated for RGBA 8888 images."
  [options gradient]
  (let [{:keys [colors default-color]} gradient
        {:keys [limit]} options
        arr (ArrayList. (int (option/image-size options)))]
    (doseq [x (option/x-range options)
            y (option/y-range options)
            :let [val       (double (mandelbrot x y limit))
                  alpha     (double (mod val 1))
                  index     (int (math/floor val))
                  ^Color c0 (nth colors index default-color)
                  ^Color c1 (nth colors (inc index) default-color)]]
      (.add arr (colors/linear-interpolation (.getRed c0) (.getRed c1) alpha))
      (.add arr (colors/linear-interpolation (.getGreen c0) (.getGreen c1) alpha))
      (.add arr (colors/linear-interpolation (.getBlue c0) (.getBlue c1) alpha))
      (.add arr -1))
    (bytes (byte-array (.toArray arr)))))