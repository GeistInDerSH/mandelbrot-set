(ns com.geistindersh.mandelbrot.mandelbrot-test
  (:require
    [clojure.test :refer [are deftest is testing]]
    [com.geistindersh.mandelbrot.gradient :as gradient]
    [com.geistindersh.mandelbrot.mandelbrot :refer [create-byte-buffer mandelbrot-periodicity-checking]]
    [com.geistindersh.mandelbrot.options :as opt])
  (:import
    (java.awt Color)
    (java.util Arrays)))

(def ^:private testing-options (opt/make-options -1.0 1.0 500 -1.0 1.0 500))
(def ^:private testing-gradient (gradient/vec->Gradient [Color/RED Color/BLUE]))

(deftest mandelbrot-test
  (testing "Test that the function for generating the mandelbrot value is correct"
    (are [a b] (= a b)
               (mandelbrot-periodicity-checking 1.0 2.325 128) 1.4506901156008833
               (mandelbrot-periodicity-checking -0.2992992992992993 0.16716716716716706 128) 128
               (mandelbrot-periodicity-checking 0.9339339339339339 0.8838838838838838 128) 2.4649220625321964
               (mandelbrot-periodicity-checking 0.49949949949949946 0.4674674674674675 128) 5.266015774842188
               (mandelbrot-periodicity-checking -0.5815815815815816 -0.6076076076076076 128) 15.102318498058457
               (mandelbrot-periodicity-checking -0.5875875875875876 -0.6176176176176176 128) 28.506146162113307
               (mandelbrot-periodicity-checking 0.41741741741741745 0.35335335335335327 128) 43.28587721842625
               (mandelbrot-periodicity-checking 0.26726726726726735 0.18918918918918926 128) 128.0)))

(deftest mandelbrot-create-byte-buffer-test
  (testing "Bytes count matches expected"
    (is (= (count (create-byte-buffer testing-options testing-gradient))
           (opt/image-buffer-size testing-options))))
  (testing "Parallel buffers are the same"
    (is (Arrays/equals ^bytes (create-byte-buffer testing-options testing-gradient true)
                       ^bytes (create-byte-buffer testing-options testing-gradient true))))
  (testing "Serial buffers are the same"
    (is (Arrays/equals ^bytes (create-byte-buffer testing-options testing-gradient false)
                       ^bytes (create-byte-buffer testing-options testing-gradient false))))
  (testing "Serial and Parallel buffers are the same"
    (is (Arrays/equals ^bytes (create-byte-buffer testing-options testing-gradient false)
                       ^bytes (create-byte-buffer testing-options testing-gradient true)))))
