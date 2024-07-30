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