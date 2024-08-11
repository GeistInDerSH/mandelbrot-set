(ns com.geistindersh.mandelbrot.core
  (:gen-class)
  (:require
    [clojure.string :as str]
    [clojure.tools.cli :as cli]
    [com.geistindersh.mandelbrot.gradient :as gradient]
    [com.geistindersh.mandelbrot.image :as image]
    [com.geistindersh.mandelbrot.options :as opt])
  (:import
    (java.awt Color)))

(def cli-options
  [["-H" "--height HEIGHT" "The height of the image"
    :default 5000
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 1 % Integer/MAX_VALUE) "The value must be between 0 and MAX_INT"]]
   ["-W" "--width WIDTH" "The width of the image"
    :default 5000
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 1 % Integer/MAX_VALUE) "The value must be between 0 and MAX_INT"]]
   ["-o" "--output-file FILE_PATH" "Where to write the resulting file to"
    :default "output.png"
    :validate [#(not (str/blank? %)) "The name of the file must be given"]]
   ["-x" "--width-view-min MIN" "The lower bound of the width view"
    :default -1.0
    :parse-fn #(Double/parseDouble %)]
   ["-X" "--width-view-max MAX" "The upper bound of the width view"
    :default 0.0
    :parse-fn #(Double/parseDouble %)]
   ["-y" "--height-view-min MIN" "The lower bound of the height view"
    :default 0.0
    :parse-fn #(Double/parseDouble %)]
   ["-Y" "--height-view-max MAX" "The upper bound of the height view"
    :default 1.0
    :parse-fn #(Double/parseDouble %)]
   ["-l" "--limit LIMIT" "The upper bound of the mandelbrot set"
    :default 128
    :parse-fn #(Integer/parseInt %)
    :validate [#(pos-int? %) "Must be a positive integer"]]
   ["-c" "--color COLOR" "A hex color to use in the image. Can be given multiple times to add additional colors"
    :multi true
    :default []
    :parse-fn #(Color. (Integer/parseInt (str/replace % #"^0x[xX]" "")
                                         16))
    :update-fn conj]
   ["-d" "--default-color COLOR" "The hex code of the default color to use, inside the mandelbrot set"
    :default Color/BLACK
    :parse-fn #(Color. (Integer/parseInt %))]
   ["-p" "--preset-gradient NAME" "The preset gradient to use"
    :default nil
    :parse-fn #(case %
                 "navy-gold" @gradient/navy-gold-gradient
                 "pink-ultramarine" @gradient/neon-pink-ultramarine-gradient
                 "lime-forest" @gradient/lime-forest-gradient
                 nil)]
   ["-h" "--help"]])

(defn- usage [summary-options]
  (->> ["A program for generating mandelbrot set images"
        ""
        "Usage: mandelbrot [options]"
        ""
        "Options:"
        summary-options]
       (str/join \newline)))

(defn- validate-opts [args]
  (let [{:keys [options summary]} (cli/parse-opts args cli-options)]
    (when (:help options)
      (println (usage summary))
      (System/exit 0))
    (let [{:keys [height height-view-min height-view-max
                  width width-view-min width-view-max
                  limit output-file
                  default-color color
                  preset-gradient]} options
          option (opt/make-options width-view-min width-view-max width height-view-min height-view-max height limit)
          grad   (if (some? preset-gradient)
                   preset-gradient
                   (gradient/vec->Gradient color limit default-color))]
      {:option    option
       :grad      grad
       :file-name output-file})))

(defn -main [& args]
  (let [{:keys [option grad file-name]} (validate-opts args)]
    (time
      (image/create-mandelbrot-png file-name option grad))
    (shutdown-agents)))