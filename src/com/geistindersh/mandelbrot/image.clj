(ns com.geistindersh.mandelbrot.image
  (:require
    [clojure.java.io :as io]
    [clojure.string :as str]
    [com.geistindersh.mandelbrot.mandelbrot :as mandelbrot])
  (:import
    (io.github.humbleui.skija ColorAlphaType ColorInfo ColorSpace ColorType EncoderJPEG EncoderPNG EncoderWEBP Image ImageInfo)
    (java.nio.file Files OpenOption StandardOpenOption)))

(defn- create-mandelbrot-image-int
  "Internal function for generating the mandelbrot image.
   encoder-fn is a function that takes in an Image, and returns a java.nio.ByteBuffer. See 'encoders' for functions"
  ([encoder-fn file-name option colors]
   (create-mandelbrot-image-int encoder-fn file-name option colors true))
  ([encoder-fn file-name option colors parallel?]
   (let [{:keys [width height]} option
         color-info   (ColorInfo. ColorType/RGBA_8888 ColorAlphaType/UNPREMUL (ColorSpace/getSRGB))
         image-info   (ImageInfo. color-info width height)
         buffer       (mandelbrot/create-byte-buffer option colors parallel?)
         byte-buffer  (->> (Image/makeRasterFromBytes image-info buffer (.getMinRowBytes image-info))
                           (encoder-fn))
         path         (.toPath (io/file file-name))
         file-options (into-array OpenOption [StandardOpenOption/CREATE
                                              StandardOpenOption/TRUNCATE_EXISTING
                                              StandardOpenOption/WRITE])
         file         (Files/newByteChannel path file-options)]
     (try
       (.write file byte-buffer)
       true
       (catch Exception e
         (println e)
         false)
       (finally
         (.close file))))))

(def ^:private encoders {:png  (fn [^Image img] (.toByteBuffer (EncoderPNG/encode img)))
                         :jpeg (fn [^Image img] (.toByteBuffer (EncoderJPEG/encode img)))
                         :webp (fn [^Image img] (.toByteBuffer (EncoderWEBP/encode img)))})
(def valid-encoders (keys encoders))

(defn create-mandelbrot-image
  ([encoder file-name option colors] (create-mandelbrot-image encoder file-name option colors true))
  ([encoder file-name option colors parallel?]
   (let [encoder-fn (get encoders encoder)]
     (when (nil? encoder-fn)
       (throw
         (IllegalArgumentException. (str encoder " is not one of the supported encoders: "
                                         (str/join ", " valid-encoders)))))
     (create-mandelbrot-image-int encoder-fn file-name option colors parallel?))))

(defn str->image-encoder
  "Attempt to convert the given string to an image encoder"
  [s]
  (let [s (str/lower-case s)]
    (case s
      "png" :png
      "webp" :webp
      "jpeg" :jpeg
      "jpg" :jpeg
      nil)))