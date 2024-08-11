(ns com.geistindersh.mandelbrot.image
  (:require
    [clojure.java.io :as io]
    [com.geistindersh.mandelbrot.mandelbrot :as mandelbrot])
  (:import
    (io.github.humbleui.skija ColorAlphaType ColorInfo ColorSpace ColorType EncoderPNG Image ImageInfo)
    (java.nio.file Files OpenOption StandardOpenOption)))

(defn create-mandelbrot-png
  "Create a new bitmap image with the given options,
   and save the result as a PNG file"
  {:added "0.2.6"}
  ([file-name option colors] (create-mandelbrot-png file-name option colors true))
  ([file-name option colors parallel?]
   (let [{:keys [width height]} option
         color-info   (ColorInfo. ColorType/RGBA_8888 ColorAlphaType/UNPREMUL (ColorSpace/getSRGB))
         image-info   (ImageInfo. color-info width height)
         buffer       (mandelbrot/create-byte-buffer option colors parallel?)
         byte-buffer  (->> (Image/makeRasterFromBytes image-info buffer (.getMinRowBytes image-info))
                           (EncoderPNG/encode)
                           (.toByteBuffer))
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
