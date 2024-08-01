(ns com.geistindersh.mandelbrot.mandelbrot-test
  (:require [clojure.test :refer [are deftest testing]]
            [com.geistindersh.mandelbrot.color-map :as colors]
            [com.geistindersh.mandelbrot.mandelbrot :refer [create-bitmap-byte-buffer create-buffer mandelbrot]]
            [com.geistindersh.mandelbrot.options :as opt])
  (:import (java.awt Color)))

(deftest mandelbrot-test
  (testing "Mandelbrot values are what we expect"
    (are [a b] (= a b)
               (mandelbrot 0.22 0.77 128) 6
               (mandelbrot 0.0 0.001001 128) 128
               (mandelbrot -1.0 0.001001 128) 128)))

(deftest create-buffer-test
  (let [option (opt/make-options -1.0 1.0 100 -1.0 1.0 100)]
    (testing "Sequential buffers are the same"
      (are [a b] (= (vec a) (vec b))
                 (create-buffer option) (create-buffer option)))))

(deftest create-bitmap-byte-buffer-test
  (testing "The generated bitmap byte buffer is as expected"
    (let [option (opt/make-options -1.0 1.0 5 -1.0 1.0 5)
          colors (colors/vec->ColorMap [Color/RED Color/BLUE])]
      (are [a b] (= a b)
                 (count (create-bitmap-byte-buffer option colors)) (* 4 (:x-res option) (:y-res option))
                 (vec (create-bitmap-byte-buffer option colors)) [18 0 -63 -1 ;; each row here is an RGBA pixel
                                                                  25 0 -63 -1
                                                                  0 0 0 -1
                                                                  18 0 -63 -1
                                                                  0 0 -63 -1
                                                                  31 0 -63 -1
                                                                  0 0 0 -1
                                                                  0 0 0 -1
                                                                  31 0 -63 -1
                                                                  0 0 -63 -1
                                                                  0 0 0 -1
                                                                  0 0 0 -1
                                                                  0 0 0 -1
                                                                  31 0 -63 -1
                                                                  0 0 -63 -1
                                                                  31 0 -63 -1
                                                                  0 0 0 -1
                                                                  0 0 0 -1
                                                                  31 0 -63 -1
                                                                  0 0 -63 -1
                                                                  18 0 -63 -1
                                                                  25 0 -63 -1
                                                                  0 0 0 -1
                                                                  18 0 -63 -1
                                                                  0 0 -63 -1]))))