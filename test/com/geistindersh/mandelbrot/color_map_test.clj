(ns com.geistindersh.mandelbrot.color-map-test
  (:require [clojure.test :refer [are deftest is testing]]
            [com.geistindersh.mandelbrot.color-map :refer [->ColorMap vec->ColorMap]])
  (:import (java.awt Color)))

(deftest vec-to-ColorMap-test
  (testing "Too few colors given"
    (is (thrown? Error (vec->ColorMap [Color/RED]))))
  (testing "Generating ColorMap from a vector of Colors"
    (are [a b] (= a b)
               (vec->ColorMap [Color/RED Color/BLUE]) (->ColorMap [[0.0 (Color. 255 0 0)]
                                                                   [0.3333333333333333 (Color. 191 0 63)]
                                                                   [0.6666666666666666 (Color. 127 0 127)]
                                                                   [1.0 (Color. 63 0 191)]])
               (vec->ColorMap [Color/RED Color/BLUE Color/GREEN]) (->ColorMap [[0.0 (Color. 255 0 0)]
                                                                               [0.14285714285714285 (Color. 191 0 63)]
                                                                               [0.2857142857142857 (Color. 127 0 127)]
                                                                               [0.42857142857142855 (Color. 63 0 191)]
                                                                               [0.5714285714285714 (Color. 0 0 255)]
                                                                               [0.7142857142857142 (Color. 0 63 191)]
                                                                               [0.8571428571428571 (Color. 0 127 127)]
                                                                               [1.0 (Color. 0 191 63)]])
               (vec->ColorMap [Color/RED Color/BLUE Color/GREEN Color/DARK_GRAY]) (->ColorMap [[0.0 (Color. 255 0 0)]
                                                                                               [0.09090909090909091 (Color. 191 0 63)]
                                                                                               [0.18181818181818182 (Color. 127 0 127)]
                                                                                               [0.2727272727272727 (Color. 63 0 191)]
                                                                                               [0.36363636363636365 (Color. 0 0 255)]
                                                                                               [0.4545454545454546 (Color. 0 63 191)]
                                                                                               [0.5454545454545454 (Color. 0 127 127)]
                                                                                               [0.6363636363636364 (Color. 0 191 63)]
                                                                                               [0.7272727272727273 (Color. 0 255 0)]
                                                                                               [0.8181818181818182 (Color. 16 207 16)]
                                                                                               [0.9090909090909092 (Color. 32 159 32)]
                                                                                               [1.0 (Color. 48 111 48)]]))))