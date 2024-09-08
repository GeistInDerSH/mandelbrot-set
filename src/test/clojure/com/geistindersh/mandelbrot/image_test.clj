(ns com.geistindersh.mandelbrot.image-test
  (:require
    [clojure.java.io :as io]
    [clojure.test :refer [are deftest is testing]]
    [clojure.tools.logging :as log]
    [com.geistindersh.mandelbrot.gradient :as gradient]
    [com.geistindersh.mandelbrot.image :refer [create-mandelbrot-image str->image-encoder]]
    [com.geistindersh.mandelbrot.options :as opt])
  (:import
    (java.awt Color)))

(def ^:private testing-options (opt/make-options -1.0 0.0 500 0.0 1.0 500))
(def ^:private testing-gradient (gradient/vec->Gradient [Color/RED Color/BLUE]))

(deftest str->image-encoder-test
  (testing "String to image-encoder"
    (are [a b] (= (str->image-encoder a) b)
               "PNG" :png
               "png" :png
               "JPEG" :jpeg
               "jpeg" :jpeg
               "JPG" :jpeg
               "jpg" :jpeg
               "WEBP" :webp
               "webp" :webp
               "gif" nil)))

(deftest ^:integration create-mandelbrot-image-test
  (testing "PNG"
    (let [file-name "example.png"]
      (try
        (is (create-mandelbrot-image :png file-name testing-options testing-gradient))
        (catch Exception e
          (log/error e))
        (finally
          (.delete (io/file file-name))))))
  (testing "JPEG"
    (let [file-name "example.jpg"]
      (try
        (is (create-mandelbrot-image :jpeg file-name testing-options testing-gradient))
        (catch Exception e
          (log/error e))
        (finally
          (.delete (io/file file-name))))))
  (testing "WEBP"
    (let [file-name "example.webp"]
      (try
        (is (create-mandelbrot-image :webp file-name testing-options testing-gradient))
        (catch Exception e
          (log/error e))
        (finally
          (.delete (io/file file-name))))))
  (testing "Unsupported Image Type"
    (let [file-name "example.webp"]
      (is (thrown? IllegalArgumentException
                   (create-mandelbrot-image :gif file-name testing-options testing-gradient))))))