(ns com.geistindersh.mandelbrot.color-map-test
  (:require [clojure.test :refer [are deftest is testing]]
            [com.geistindersh.mandelbrot.color-map :refer [->ColorMap get-at vec->ColorMap]])
  (:import (java.awt Color)))

(deftest vec-to-ColorMap-test
  (testing "Too few colors given"
    (is (thrown? Error (vec->ColorMap [Color/RED]))))
  (testing "Generating ColorMap from a vector of Colors"
    (are [a b] (= a b)
               (vec->ColorMap [Color/RED Color/BLUE]) (->ColorMap [[0.0 (Color. 255 0 63)]
                                                                   [0.3333333333333333 (Color. 192 0 63)]
                                                                   [0.6666666666666666 (Color. 129 0 63)]
                                                                   [1.0 (Color. 66 0 63)]])
               (vec->ColorMap [Color/RED Color/BLUE Color/GREEN]) (->ColorMap [[0.0 (Color. 255 0 63)]
                                                                               [0.3333333333333333 (Color. 192 0 63)]
                                                                               [0.6666666666666666 (Color. 129 0 63)]
                                                                               [1.0 (Color. 66 0 63)]])
               (vec->ColorMap [Color/RED Color/BLUE Color/GREEN Color/DARK_GRAY]) (->ColorMap [[0.0 (Color. 255 0 63)]
                                                                                               [0.14285714285714285 (Color. 192 0 63)]
                                                                                               [0.2857142857142857 (Color. 129 0 63)]
                                                                                               [0.42857142857142855 (Color. 66 0 63)]
                                                                                               [0.5714285714285714 (Color. 0 255 16)]
                                                                                               [0.7142857142857142 (Color. 16 208 16)]
                                                                                               [0.8571428571428571 (Color. 32 161 16)]
                                                                                               [1.0 (Color. 48 114 16)]]))))

(deftest get-at-test
  (let [colors (vec->ColorMap [Color/RED Color/BLUE Color/GREEN Color/DARK_GRAY])]
    (testing "Getting the Linear Interpolation of the closest Color to the index"
      (are [a b] (= a b)
                 (get-at colors 0.05) (Color. 232 0 63 0)
                 (get-at colors 0.42) (Color. 69 0 63 0)
                 (get-at colors 0.5) (Color. 33 127 39 0)
                 (get-at colors 1.0) (Color. 48 114 16 0)))))