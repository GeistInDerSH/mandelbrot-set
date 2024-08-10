(ns com.geistindersh.mandelbrot.options-test
  (:require
    [clojure.test :refer [are deftest is testing]]
    [com.geistindersh.mandelbrot.options :as opt]))

(def testing-option (opt/->Options -1.0 1000 -1.0 1000 128 0.002002002002002002 0.002002002002002002))

(deftest make-options-test
  (testing "Create new Options record with make-options"
    (are [actual] (= testing-option actual)
                  (opt/make-options)
                  (opt/make-options -1.0 1.0 1000 -1.0 1.0 1000)
                  (opt/make-options -1.0 1.0 1000 -1.0 1.0 1000 128))))

(deftest range-tests
  (testing "x-range"
    (is (= (count (opt/row-constants testing-option))
           1000)))
  (testing "x-range"
    (is (= (count (opt/column-constants testing-option))
           1000))))

(deftest image-size-test
  (testing "Image Size matches expected"
    (is (= (* 4 1000 1000)
           (opt/image-buffer-size testing-option)))))