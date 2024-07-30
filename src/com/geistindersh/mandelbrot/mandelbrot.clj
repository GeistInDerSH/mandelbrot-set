(ns com.geistindersh.mandelbrot.mandelbrot)

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
   value for each entry. This is done in parallel, with each 'x row' run
   independently."
  {:added "0.1.1"}
  [options]
  (let [{:keys [x-min y-min y-res x-res x-delta y-delta limit]} options]
    (->> (for [x (range 0 x-res)
               y (range 0 y-res)]
           [x y])
         (map (fn [[x y]]
                (let [cx (float (+ (* x x-delta) x-min))
                      cy (float (+ (* y y-delta) y-min))]
                  (mandelbrot cx cy limit))))
         (int-array))))

(defn create-buffer
  "Generate a 1-D array of size x-res * y-res, and calculate the mandelbrot
   value for each entry."
  {:added "0.1.1"}
  ([options] (create-buffer options false))
  ([options parallel?]
   (if parallel?
     (parallel-create-buffer options)
     (sequential-create-buffer options))))