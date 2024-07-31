(ns com.geistindersh.mandelbrot.mandelbrot
  (:require
    [clojure.math :as math]
    [com.geistindersh.mandelbrot.color-map :as color])
  (:import
    (io.github.humbleui.skija ColorType)
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
        arr (int-array (* y-res x-res) 0)]
    (doseq [y (range 0 y-res)
            :let [base (int (* y x-res))
                  cy   (float (+ (* y y-delta) y-min))]]
      (doseq [x (range 0 x-res)
              :let [cx (float (+ (* x x-delta) x-min))]]
        (aset-int arr (+ base x) (mandelbrot cx cy limit))))
    arr))

(defn get-colors
  "Get the colors that will be in the image"
  {:added "0.2.4"}
  [color-map options]
  (let [{:keys [limit]} options
        buffer        (create-buffer options)
        lower-bound   (apply min buffer)
        upper-bound   (max (inc (apply max buffer))
                           limit)
        dist          (- upper-bound lower-bound)
        color-options (->> (range dist)
                           (map #(math/pow (/ % dist) 0.5))
                           (mapv #(color/get-at color-map %)))]
    (->> buffer
         (map (fn [val]
                (if (>= val limit)
                  Color/BLACK
                  (get color-options (- val lower-bound)))))
         (filter some?))))

(defn create-bitmap-byte-buffer
  "Create a byte-array mapping to the pixel color values for the mandelbrot image.
   The pixels in the buffer is allocated for RGBA 8888 images."
  {:added "0.2.4"}
  [options color-map]
  (let [{:keys [x-res y-res]} options
        color-buffer    (get-colors color-map options)
        bytes-per-pixel (.getBytesPerPixel ColorType/RGBA_8888)
        byte-buffer     (byte-array (* x-res y-res bytes-per-pixel) (byte 0))]
    (doseq [[i color] (map-indexed vector color-buffer)
            :let [base (* i bytes-per-pixel)]]
      (aset-byte byte-buffer (+ 0 base) (unchecked-byte (* 255 (.getRed color))))
      (aset-byte byte-buffer (+ 1 base) (unchecked-byte (* 255 (.getGreen color))))
      (aset-byte byte-buffer (+ 2 base) (unchecked-byte (* 255 (.getBlue color))))
      (aset-byte byte-buffer (+ 3 base) (unchecked-byte 255)))
    byte-buffer))