(ns com.geistindersh.mandelbrot.image-test
  (:require
    [clojure.java.io :as io]
    [clojure.test :refer [deftest is testing]]
    [com.geistindersh.mandelbrot.gradient :as gradient]
    [com.geistindersh.mandelbrot.image :refer [create-mandelbrot-png]]
    [com.geistindersh.mandelbrot.options :as opt])
  (:import
    (java.awt Color)))

(def ^:private testing-options (opt/make-options -1.0 0.0 500 0.0 1.0 500))
(def ^:private testing-gradient (gradient/vec->Gradient [Color/RED Color/BLUE]))

(deftest ^:integration create-mandelbrot-png-test
  (testing "Generate an example image"
    (let [file-name "example.png"]
      (try
        (is (create-mandelbrot-png file-name testing-options testing-gradient))
        (catch Exception e
          (println e))
        (finally
          (.delete (io/file file-name)))))))