(ns com.geistindersh.mandelbrot.options-test
  (:require
    [clojure.test :refer [are deftest testing]]
    [com.geistindersh.mandelbrot.options :as opt]))

(deftest make-options-test
  (testing "Create new Options record with make-options"
    (let [expected (opt/->Options -1.0 1.0 1000 -1.0 1.0 1000 128 0.0 0.0)]
      (are [actual] (= expected actual)
                    (opt/make-options)
                    (opt/make-options -1.0 1.0 1000 -1.0 1.0 1000)
                    (opt/make-options -1.0 1.0 1000 -1.0 1.0 1000 128)))))