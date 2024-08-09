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

(defn mandelbrot-periodicity-checking
  "Calculate the mandelbrot value, based on the given constants"
  {:added "0.1.1"}
  ^"java.lang.Double" [^double cx ^double cy ^long limit]
  (loop [i      (long 0)
         xn     (double 0.0)
         yn     (double 0.0)
         x-old  (double 0.0)
         y-old  (double 0.0)
         period (long 0)]
    (let [xn2 (double (* xn xn))
          yn2 (double (* yn yn))]
      (cond
        (and (< (+ xn2 yn2) xyn2-limit)
             (< i limit)) (let [x-new (+ (- xn2 yn2)
                                         cx)
                                y-new (+ (* (+ xn xn) yn)
                                         cy)]
                            (cond
                              (and (= x-new x-old)
                                   (= y-new y-old)) limit
                              (< period 20) (recur (inc i) x-new y-new x-old y-old (inc period))
                              :else (recur (inc i) x-new y-new x-new y-new 0)
                              ))

        (< i limit) (let [log-zn (/ (math/log (+ xn2 yn2)) 2)
                          nu     (/ (math/log (/ log-zn log-2))
                                    log-2)]
                      (double (- (inc i)
                                 nu)))
        :else (double i)))))

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

(defn- create-byte-buffer-serial
  "Create a byte-array mapping to the pixel color values for the mandelbrot image.
   The pixels in the buffer is allocated for RGBA 8888 images."
  [options gradient]
  (let [{:keys [colors default-color]} gradient
        {:keys [limit]} options
        arr (ArrayList. (int (option/image-buffer-size options)))]
    (doseq [x (option/x-range options)
            y (option/y-range options)
            :let [val       (double (mandelbrot-periodicity-checking x y limit))
                  alpha     (double (mod val 1))
                  index     (int (math/floor val))
                  ^Color c0 (nth colors index default-color)
                  ^Color c1 (nth colors (inc index) default-color)]]
      (.add arr (colors/linear-interpolation (.getRed c0) (.getRed c1) alpha))
      (.add arr (colors/linear-interpolation (.getGreen c0) (.getGreen c1) alpha))
      (.add arr (colors/linear-interpolation (.getBlue c0) (.getBlue c1) alpha))
      (.add arr -1))
    (bytes (byte-array (.toArray arr)))))

(defn- create-mandelbrot-vals-parallel [options]
  (let [{:keys [x-res y-res limit]} options
        x-range (option/x-range options)
        y-range (option/y-range options)
        buff    (double-array (* x-res y-res))
        tasks   (into []
                      (map (fn [i]
                             (let [x    (nth x-range i)
                                   base (* i y-res)]
                               (future
                                 (doseq [j (range y-res)
                                         :let [y (nth y-range j)]]
                                   (aset-double buff (+ base j) (mandelbrot-periodicity-checking x y limit)))))))
                      (range x-res))]
    (doseq [task tasks] @task)
    buff))

(defn create-byte-buffer-parallel
  "Create a byte-array mapping to the pixel color values for the mandelbrot image.
   The pixels in the buffer is allocated for RGBA 8888 images."
  [options gradient]
  (let [{:keys [colors default-color]} gradient
        vals (create-mandelbrot-vals-parallel options)
        arr  (ArrayList. (int (option/image-buffer-size options)))]
    (doseq [val vals
            :let [alpha     (double (mod val 1))
                  index     (int (math/floor val))
                  ^Color c0 (nth colors index default-color)
                  ^Color c1 (nth colors (inc index) default-color)]]
      (.add arr (colors/linear-interpolation (.getRed c0) (.getRed c1) alpha))
      (.add arr (colors/linear-interpolation (.getGreen c0) (.getGreen c1) alpha))
      (.add arr (colors/linear-interpolation (.getBlue c0) (.getBlue c1) alpha))
      (.add arr -1))
    (bytes (byte-array (.toArray arr)))))

(defn create-byte-buffer
  ([options gradient] (create-byte-buffer options gradient true))
  ([options gradient parallel?]
   (if parallel?
     (create-byte-buffer-parallel options gradient)
     (create-byte-buffer-serial options gradient))))