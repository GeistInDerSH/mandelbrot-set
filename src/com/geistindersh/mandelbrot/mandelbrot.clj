(ns com.geistindersh.mandelbrot.mandelbrot
  (:require
    [clojure.math :as math]
    [com.geistindersh.mandelbrot.gradient :as gradient]
    [com.geistindersh.mandelbrot.options :as opt])
  (:import
    (java.awt Color)
    (java.util ArrayList)))

(def ^:private log-2 (math/log 2))
(def ^:private xyn2-limit (double (bit-shift-left 1 16)))

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
                              :else (recur (inc i) x-new y-new x-new y-new 0)))
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
        arr (ArrayList. (int (opt/image-buffer-size options)))]
    (doseq [y (opt/column-constants options)
            x (opt/row-constants options)
            :let [val       (double (mandelbrot-periodicity-checking x y limit))
                  alpha     (double (mod val 1))
                  index     (int (math/floor val))
                  ^Color c0 (nth colors index default-color)
                  ^Color c1 (nth colors (inc index) default-color)]]
      (.add arr (gradient/linear-interpolation (.getRed c0) (.getRed c1) alpha))
      (.add arr (gradient/linear-interpolation (.getGreen c0) (.getGreen c1) alpha))
      (.add arr (gradient/linear-interpolation (.getBlue c0) (.getBlue c1) alpha))
      (.add arr -1))
    (bytes (byte-array (.toArray arr)))))

(defn- create-mandelbrot-vals-parallel
  "Generate the initial mandelbrot values in a double-array.
   This is done in parallel using futures that are awaited before returning."
  [options]
  (let [{:keys [width height limit]} options
        row-vals (opt/row-constants options)
        col-vals (opt/column-constants options)
        buff     (double-array (* width height))
        tasks    (into []
                       (map (fn [i]
                              (let [y    (nth col-vals i)
                                    base (* i width)]
                                (future
                                  (doseq [j (range width)
                                          :let [x (nth row-vals j)]]
                                    (aset-double buff (+ base j) (mandelbrot-periodicity-checking x y limit)))))))
                       (range height))]
    (doseq [task tasks] @task)
    buff))

(defn- create-byte-buffer-parallel
  "Create a byte-array mapping to the pixel color values for the mandelbrot image.
   The pixels in the buffer is allocated for RGBA 8888 images."
  [options gradient]
  (let [{:keys [colors default-color]} gradient
        vals (create-mandelbrot-vals-parallel options)
        arr  (ArrayList. (int (opt/image-buffer-size options)))]
    (doseq [val vals
            :let [alpha     (double (mod val 1))
                  index     (int (math/floor val))
                  ^Color c0 (nth colors index default-color)
                  ^Color c1 (nth colors (inc index) default-color)]]
      (.add arr (gradient/linear-interpolation (.getRed c0) (.getRed c1) alpha))
      (.add arr (gradient/linear-interpolation (.getGreen c0) (.getGreen c1) alpha))
      (.add arr (gradient/linear-interpolation (.getBlue c0) (.getBlue c1) alpha))
      (.add arr -1))
    (bytes (byte-array (.toArray arr)))))

(defn create-byte-buffer
  "Generate an image byte buffer of 8-bit RGB values.
   This can optionally run in parallel to speed up the generation (default)."
  ([options gradient] (create-byte-buffer options gradient true))
  ([options gradient parallel?]
   (if parallel?
     (create-byte-buffer-parallel options gradient)
     (create-byte-buffer-serial options gradient))))