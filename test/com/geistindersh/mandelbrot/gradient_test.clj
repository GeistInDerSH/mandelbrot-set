(ns com.geistindersh.mandelbrot.gradient-test
  (:require
    [clojure.test :refer [are deftest testing]]
    [com.geistindersh.mandelbrot.gradient :refer [lime-forest-gradient
                                                  navy-gold-gradient
                                                  neon-pink-ultramarine-gradient
                                                  vec->Gradient]])
  (:import
    (java.awt Color)))

(deftest vec->Gradient-test
  (testing "The number of colors in the gradient are what we expect"
    (are [a b] (= a (count (:colors b)))
               256 (vec->Gradient [Color/RED Color/BLUE])
               128 (vec->Gradient [Color/RED Color/BLUE] 128)
               64 (vec->Gradient [(Color. (float 0) (float 0) (float 0.2))
                                  Color/BLUE
                                  Color/LIGHT_GRAY
                                  (Color. (float 0.9) (float 0.7) (float 0.4))
                                  Color/GRAY]
                                 64)
               2 (vec->Gradient [(Color. (float 0) (float 0) (float 0.2))
                                 Color/BLUE
                                 Color/LIGHT_GRAY
                                 (Color. (float 0.9) (float 0.7) (float 0.4))
                                 Color/GRAY]
                                2)))
  (testing "The default color is what we expect"
    (are [a b] (= a (:default-color b))
               Color/BLACK (vec->Gradient [Color/RED Color/BLUE])
               Color/PINK (vec->Gradient [Color/RED Color/BLUE] 128 Color/PINK))))

(deftest pre-generated-gradients-test
  (testing "The number of colors in the gradient are what we expect"
    (are [a b] (= a (count (:colors b)))
               128 @navy-gold-gradient
               128 @lime-forest-gradient
               128 @neon-pink-ultramarine-gradient))
  (testing "The default color is what we expect"
    (are [a b] (= a (:default-color b))
               Color/BLACK @navy-gold-gradient
               Color/BLACK @lime-forest-gradient
               Color/BLACK @neon-pink-ultramarine-gradient)))
