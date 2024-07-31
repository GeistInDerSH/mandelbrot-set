(ns com.geistindersh.mandelbrot.image
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [com.geistindersh.mandelbrot.color-map :as color]
            [com.geistindersh.mandelbrot.mandelbrot :as mandelbrot]
            [com.geistindersh.mandelbrot.options :as opt])
  (:import (io.github.humbleui.skija Bitmap ColorAlphaType ColorInfo ColorSpace ColorType EncoderPNG Image ImageInfo)
           (java.awt Color)
           (java.nio.file Files OpenOption StandardOpenOption)))

(defn- make-bitmap
  "Create a Bitmap with the pixels of the image filled in"
  {:added "0.2.6"}
  ^Bitmap [options color-map]
  (let [{:keys [y-res x-res]} options
        color-info  (ColorInfo. ColorType/RGBA_8888 ColorAlphaType/UNPREMUL (ColorSpace/getSRGB))
        image-info  (ImageInfo. color-info y-res x-res)
        bitmap      (Bitmap.)
        byte-buffer (mandelbrot/create-bitmap-byte-buffer options color-map)]
    (.allocPixels bitmap image-info)
    (.installPixels bitmap byte-buffer)
    bitmap))

(defn create-mandelbrot-png
  "Create a new bitmap image with the given options,
   and save the result as a PNG file"
  {:added "0.2.6"}
  ([file-name color-map options]
   {:pre [string/ends-with? file-name ".png"]}
   (let [byte-buffer  (->> (make-bitmap options color-map)
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
  ([file-name color-map]
   (create-mandelbrot-png file-name color-map (opt/make-options)))
  ([file-name]
   (create-mandelbrot-png file-name
                          (color/vec->ColorMap [Color/RED Color/DARK_GRAY]))))
