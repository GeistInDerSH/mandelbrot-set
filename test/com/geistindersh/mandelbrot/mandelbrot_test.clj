(ns com.geistindersh.mandelbrot.mandelbrot-test
  (:require [clojure.test :refer [are deftest testing]]
            [com.geistindersh.mandelbrot.mandelbrot :refer :all]
            [com.geistindersh.mandelbrot.options :as opt]))

(deftest create-buffer-test
  (let [option (opt/make-options -1.0 1.0 100 -1.0 1.0 100)]
    (testing "Parallel and Sequential buffers are the same"
      (are [a b] (= (vec a) (vec b))
                 (create-buffer option true) (create-buffer option false)))
    (testing "Parallel buffers are the same"
      (are [a b] (= (vec a) (vec b))
                 (create-buffer option true) (create-buffer option true)))
    (testing "Sequential buffers are the same"
      (are [a b] (= (vec a) (vec b))
                 (create-buffer option false) (create-buffer option false)))))