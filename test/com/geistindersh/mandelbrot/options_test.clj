(ns com.geistindersh.mandelbrot.options-test
  (:require
    [clojure.test :refer [are deftest is testing]]
    [com.geistindersh.mandelbrot.options :as opt]))

(def ^:private testing-options (opt/->Options -1.0 1000 -1.0 1000 128 0.002002002002002002 0.002002002002002002))

(deftest make-options-test
  (testing "Create new Options record with make-options"
    (are [actual] (= testing-options actual)
                  (opt/make-options)
                  (opt/make-options -1.0 1.0 1000 -1.0 1.0 1000)
                  (opt/make-options -1.0 1.0 1000 -1.0 1.0 1000 128))))

(deftest row-col-constants-test
  (testing "Row Constants"
    (is (= (count (opt/row-constants testing-options))
           1000)))
  (testing "Column Constants"
    (is (= (count (opt/column-constants testing-options))
           1000))))

(deftest image-size-test
  (testing "Image Size matches expected"
    (is (= (* 4 1000 1000)
           (opt/image-buffer-size testing-options)))))