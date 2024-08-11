(ns com.geistindersh.mandelbrot.gradient
  (:require
    [clojure.math :as math]
    [clojure.string :as str]
    [com.geistindersh.mandelbrot.utils :as utils])
  (:import
    (java.awt Color)))

(defn- coerce-in
  "Coerce the given value between [min-val max-val], or return the value
   if it's already between the two"
  {:added "0.2.3"}
  [value min-val max-val]
  (min (max value min-val)
       max-val))

(defn- colors-between
  "Generate a sequence of colors between the given start and end colors"
  [step-count [^Color start ^Color end]]
  (let [red-start   (.getRed start)
        red-step    (/ (- (.getRed end) red-start)
                       step-count)
        green-start (.getGreen start)
        green-step  (/ (- (.getGreen end) green-start)
                       step-count)
        blue-start  (.getBlue start)
        blue-step   (/ (- (.getBlue end) blue-start)
                       step-count)]
    (->> (range step-count)
         (map (fn [i]
                (let [r (+ (* red-step i) red-start)
                      g (+ (* green-step i) green-start)
                      b (+ (* blue-step i) blue-start)]
                  (Color. (int (coerce-in r 0 255))
                          (int (coerce-in g 0 255))
                          (int (coerce-in b 0 255)))))))))

(defrecord Gradient [colors default-color])

(defn vec->Gradient
  "Convert a vector into a Gradient, generating any of the colors
   in between to the given limit"
  ([v] (vec->Gradient v 256))
  ([v size] (vec->Gradient v size Color/BLACK))
  ([v size default]
   {:pre [(> (count v) 0)]}
   (let [steps  (->> v count dec (min size) (/ size) (math/ceil))
         colors (into []
                      (comp
                        (utils/window 2)
                        (map #(colors-between steps %))
                        cat
                        (take size))
                      v)]
     (->Gradient colors default))))

(defn linear-interpolation
  "Interpolate a new value between the two vertexes with a given
   alpha value.
   https://en.wikipedia.org/wiki/Linear_interpolation#Programming_language_support"
  {:added "0.2.3"}
  [^double v0 ^double v1 ^double alpha]
  (unchecked-byte (+ (* v0 (- 1 alpha))
                     (* v1 alpha))))

(defn str->Color
  "Try to convert the given string to a Color"
  [s]
  (let [s (str/lower-case s)]
    (case s
      "black" Color/BLACK
      "blue" Color/BLUE
      "cyan" Color/CYAN
      "gray" Color/GRAY
      "green" Color/GREEN
      "grey" Color/GRAY
      "magenta" Color/MAGENTA
      "pink" Color/PINK
      "red" Color/RED
      "yellow" Color/YELLOW
      (Color. (Integer/parseInt (str/replace s #"^0x" "")
                                16)))))

(def navy-gold-gradient (delay
                          (vec->Gradient [(Color. (float 0) (float 0) (float 0.2))
                                          Color/BLUE
                                          Color/LIGHT_GRAY
                                          (Color. (float 0.9) (float 0.7) (float 0.4))
                                          Color/GRAY]
                                         128)))
(def lime-forest-gradient (delay
                            (vec->Gradient [(Color. 0 81 81)
                                            (Color. 75 183 109)
                                            Color/GREEN]
                                           128)))
(def neon-pink-ultramarine-gradient (delay
                                      (vec->Gradient [(Color. 18 10 143)
                                                      (Color. 218 66 216)
                                                      (Color. 222 22 221)
                                                      Color/PINK]
                                                     128)))
(def presets
  {"navy-gold"        navy-gold-gradient
   "pink-ultramarine" neon-pink-ultramarine-gradient
   "lime-forest"      lime-forest-gradient})
