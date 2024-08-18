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
    :default -2.0
    :parse-fn #(Double/parseDouble %)]
   ["-X" "--width-view-max MAX" "The upper bound of the width view"
    :default 0.5
    :parse-fn #(Double/parseDouble %)]
   ["-y" "--height-view-min MIN" "The lower bound of the height view"
    :default -1.25
    :parse-fn #(Double/parseDouble %)]
   ["-Y" "--height-view-max MAX" "The upper bound of the height view"
    :default 1.25
    :parse-fn #(Double/parseDouble %)]
   ["-l" "--limit LIMIT" "The upper bound of the mandelbrot set"
    :default 128
    :parse-fn #(Integer/parseInt %)
    :validate [#(pos-int? %) "Must be a positive integer"]]
   ["-c" "--color COLOR" "A hex color to use in the image. Can be given multiple times to add additional colors"
    :multi true
    :default []
    :parse-fn #(gradient/str->Color %)
    :update-fn conj]
   ["-d" "--default-color COLOR" "The hex code of the default color to use, inside the mandelbrot set"
    :default Color/BLACK
    :parse-fn #(gradient/str->Color %)]
   ["-p" "--preset-gradient NAME"
    :desc (str "The preset gradient to use. Options: "
               (str/join ", " (keys gradient/presets)))
    :default nil
    :parse-fn #(get gradient/presets %)]
   ["-t" "--image-type IMAGE_TYPE"
    :desc (str "The type of image to generate. Options: "
               (str/join ", " (map name image/valid-encoders)))
    :default-fn (fn [obj]
                  (let [file-name (get obj :output-file)
                        ext       (peek (str/split file-name #"\."))]
                    (image/str->image-encoder ext)))
    :parse-fn #(image/str->image-encoder %)]
   [nil "--[no-]parallel" "Run the image generation in parallel"
    :default true]
   ["-h" "--help"]])

(defn- usage [summary-options]
  (->> ["A program for generating mandelbrot set images"
        ""
        "Usage: mandelbrot [options]"
        ""
        "Options:"
        summary-options]
       (str/join \newline)))

(def no-color-or-preset
  (str "No color or preset was provided. "
       "Provide colors with --color, or a preset with --preset-gradient"
       "\nSee --help for more options"))

(defn validate-opts [args]
  (let [{:keys                     [summary]
         {:keys [height height-view-min height-view-max
                 width width-view-min width-view-max
                 limit output-file
                 default-color color
                 preset-gradient parallel
                 help image-type]} :options} (cli/parse-opts args cli-options)]
    (cond
      (some? help) {:exit-code 0 :exit-text (usage summary)}
      (and (empty? color)
           (nil? preset-gradient)) {:exit-code -1 :exit-text no-color-or-preset}
      (nil? image-type) {:exit-code -1 :exit-text (str "Unknown or unsupported image extension for " output-file)}
      :else {:option     (opt/make-options width-view-min width-view-max width height-view-min height-view-max height limit)
             :grad       (if (some? preset-gradient)
                           @preset-gradient
                           (gradient/vec->Gradient color limit default-color))
             :file-name  output-file
             :image-type image-type
             :parallel?  parallel})))

(defn -main [& args]
  (let [{:keys [option grad file-name image-type parallel? exit-code exit-text]} (validate-opts args)]
    (when (and exit-code exit-text)
      (println exit-text)
      (System/exit exit-code))
    (time
      (image/create-mandelbrot-image image-type file-name option grad parallel?))
    (shutdown-agents)))