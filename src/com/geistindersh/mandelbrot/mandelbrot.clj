(ns com.geistindersh.mandelbrot.mandelbrot
  (:require
    [clojure.math :as math]))

(defn mandelbrot
  "Calculate the mandelbrot value, based on the given constants"
  {:added "0.1.1"}
  [cx cy limit]
  (loop [i  (int 0)
         xn (double 0.0)
         yn (double 0.0)]
    (if (and (< (+ (math/pow xn 2)
                   (math/pow yn 2))
                4)
             (< i limit))
      (let [x-temp (- (* xn xn)
                      (* yn yn))
            y-temp (* 2 xn yn)]
        (recur (inc i)
               (+ cx x-temp)
               (+ cy y-temp)))
      i)))

(defn- parallel-create-buffer
  "Generate a 1-D array of size x-res * y-res, and calculate the mandelbrot
   value for each entry. This is done in parallel, with each 'x row' run
   independently."
  {:added "0.1.1"}
  [options]
  (let [{:keys [x-min y-min y-res x-res x-delta y-delta limit]} options
        arr   (int-array (* x-res y-res) 0)
        tasks (for [y (range 0 y-res)
                    x (range 0 x-res)]
                (future
                  (let [cx    (+ (* x x-delta)
                                 x-min)
                        cy    (+ (* y y-delta)
                                 y-min)
                        index (+ (* y x-res) x)]
                    (aset arr index (mandelbrot cx cy limit)))))]
    (doseq [task tasks]
      @task)
    arr))

(defn- sequential-create-buffer
  "Generate a 1-D array of size x-res * y-res, and calculate the mandelbrot
   value for each entry. Each 'row' is calculated sequentially"
  {:added "0.1.1"}
  [options]
  (let [{:keys [x-min y-min y-res x-res x-delta y-delta limit]} options
        arr (int-array (* x-res y-res) 0)]
    (doseq [y (range 0 y-res)
            x (range 0 x-res)]
      (let [cx    (+ (* x x-delta)
                     x-min)
            cy    (+ (* y y-delta)
                     y-min)
            index (+ (* y x-res) x)]
        (aset arr index (mandelbrot cx cy limit))))
    arr))

(defn create-buffer
  "Generate a 1-D array of size x-res * y-res, and calculate the mandelbrot
   value for each entry."
  {:added "0.1.1"}
  ([options] (create-buffer options true))
  ([options parallel?]
   (if parallel?
     (parallel-create-buffer options)
     (sequential-create-buffer options))))