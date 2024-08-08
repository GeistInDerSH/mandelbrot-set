(ns com.geistindersh.mandelbrot.image
  (:require [clojure.java.io :as io]
            [com.geistindersh.mandelbrot.mandelbrot :as mandelbrot])
  (:import (io.github.humbleui.skija Bitmap ColorAlphaType ColorInfo ColorSpace ColorType EncoderPNG Image ImageInfo)
           (java.nio.file Files OpenOption StandardOpenOption)))

(defn- make-bitmap
  "Create a Bitmap with the pixels of the image filled in"
  {:added "0.2.6"}
  ^Bitmap [option colors]
  (let [{:keys [x-res y-res]} option
        color-info  (ColorInfo. ColorType/RGBA_8888 ColorAlphaType/UNPREMUL (ColorSpace/getSRGB))
        image-info  (ImageInfo. color-info x-res y-res)
        bitmap      (Bitmap.)
        byte-buffer (mandelbrot/create-bitmap-byte-buffer option colors)]
    (.allocPixels bitmap image-info)
    (.installPixels bitmap byte-buffer)
    bitmap))

(defn create-mandelbrot-png
  "Create a new bitmap image with the given options,
   and save the result as a PNG file"
  {:added "0.2.6"}
  [file-name option colors]
  (let [byte-buffer  (->> (make-bitmap option colors)
                          (Image/makeRasterFromBitmap)
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
        (.close file)))))
