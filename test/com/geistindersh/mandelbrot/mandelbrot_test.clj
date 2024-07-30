(ns com.geistindersh.mandelbrot.mandelbrot-test
  (:require [clojure.test :refer [are deftest testing]]
            [com.geistindersh.mandelbrot.mandelbrot :refer [create-buffer]]
            [com.geistindersh.mandelbrot.options :as opt]))

(deftest create-buffer-test
  (let [option (opt/make-options -1.0 1.0 100 -1.0 1.0 100)]
    (testing "Sequential buffers are the same"
      (are [a b] (= (vec a) (vec b))
                 (create-buffer option) (create-buffer option)))))