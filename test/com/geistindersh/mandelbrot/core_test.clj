(ns com.geistindersh.mandelbrot.core-test
  (:require
    [clojure.string :as str]
    [clojure.test :refer [are deftest is testing]]
    [com.geistindersh.mandelbrot.core :refer [validate-opts]]
    [com.geistindersh.mandelbrot.gradient :as gradient]
    [com.geistindersh.mandelbrot.options :as opt])
  (:import
    (java.awt Color)))

(deftest validate-opts-test
  (testing "Help Flag"
    (let [{:keys [exit-code exit-text]} (validate-opts ["-h"])]
      (is (= 0 exit-code))
      (is (str/starts-with?
            exit-text
            "A program for generating mandelbrot set images\n\nUsage: mandelbrot [options]\n\nOptions:\n"))))
  (testing "No Color or Preset"
    (let [{:keys [exit-code exit-text]} (validate-opts nil)]
      (is (= -1 exit-code))
      (is (= exit-text
             (str "No color or preset was provided. "
                  "Provide colors with --color, or a preset with --preset-gradient"
                  "\nSee --help for more options")))))
  (testing "With Image Configs"
    (let [flags ["-H" "1000" "-W" "1000" "-o" "/tmp/example.png"
                 "-x" "-1.0" "-X" "0.0" "-y" "0" "-Y" "1.0"
                 "--limit" "128" "-p" "lime-forest"]
          {:keys [option file-name]} (validate-opts flags)]
      (are [a b] (= a b)
                 option (opt/make-options -1.0 0.0 1000 0.0 1.0 1000 128)
                 file-name "/tmp/example.png")))
  (testing "With Preset Gradient Flags"
    (let [flags ["-p" "lime-forest"]
          {:keys [grad]} (validate-opts flags)]
      (is (= grad @gradient/lime-forest-gradient))))
  (testing "With Colors Flags"
    (let [flags ["-c" "red" "--color" "BLUE" "-c" "00ff00" "-d" "pink"]
          {:keys [grad]} (validate-opts flags)]
      (is (= grad
             (gradient/vec->Gradient [Color/RED Color/BLUE Color/GREEN] 128 Color/PINK))))))
