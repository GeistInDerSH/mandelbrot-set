(ns build
  (:require
    [clojure.java.io :as io]
    [clojure.tools.build.api :as build]
    [clojure.tools.logging :as log])
  (:import
    (java.io File InputStream)
    (java.time LocalDateTime ZoneId)
    (java.time.format DateTimeFormatter)
    (java.util Collections)
    (org.apache.logging.log4j.core.config.plugins.processor PluginCache)))

(def latest-revision (delay (build/git-process {:git-args "rev-parse --short HEAD"})))
(defn- get-patch-version []
  (let [last-version-bump-commit "f0559f6b"                 ;; Count the commits since the version bump as the patches
        git-arg                  (format "rev-list %s...%s --count" last-version-bump-commit @latest-revision)
        patches-since-bump       (build/git-process {:git-args git-arg})]
    (format "1.0.%s" patches-since-bump)))

(def class-dir "target/classes")
(def uber-file "target/mandelbrot.jar")

;; delay to defer side effects (artifact downloads)
(def basis (delay (build/create-basis {:project "deps.edn"})))

(defn log4j2-plugin-merger
  "Conflict resolution function for merging Log4J2.dat files from different dependencies.

   This is a modification of: https://github.com/seancorfield/build-uber-log4j2-handler/tree/v2.23.1
   with some updates to clean up temp files, and use the Collections/enumeration over an
   anonymous function"
  [{:keys [^InputStream in ^File existing]}]
  (let [cache (PluginCache.)
        temp  (File/createTempFile "Log4j2Plugins" ".dat")]
    (try
      (io/copy in temp :buffer-size 4096)
      (->> [existing temp]
           (map io/as-url)
           Collections/enumeration
           (.loadCacheFiles cache))
      (with-open [stream (io/output-stream existing)]
        (.writeCache cache stream))
      (catch Exception e
        (log/errorf "While merging log4j plugins: %s" e))
      (finally
        (.delete temp)))
    nil))

(defn clean [_]
  (build/delete {:path "target"}))

(defn uber [{:keys [env]
             :or   {env :debug}}]
  (clean nil)
  (build/copy-dir {:src-dirs   ["src/main/resources"]
                   :target-dir class-dir})
  (build/compile-clj {:basis        @basis
                      :bindings     {#'*warn-on-reflection* true}
                      :compile-opts {:direct-linking          true
                                     :disable-locals-clearing (= env :debug)}
                      :ns-compile   '[com.geistindersh.mandelbrot.core]
                      :class-dir    class-dir})
  (build/uber {:class-dir         class-dir
               :uber-file         uber-file
               :conflict-handlers {"^META-INF/org/apache/logging/log4j/core/config/plugins/Log4j2Plugins.dat$" log4j2-plugin-merger}
               :manifest          {"Revision"      @latest-revision
                                   "Build-Version" (get-patch-version)
                                   "Build-Date"    (.format (LocalDateTime/now (ZoneId/of "America/Chicago"))
                                                            (DateTimeFormatter/ofPattern "yyyy/MM/dd HH:mm:SS"))
                                   "Built-By"      (System/getenv "USER")
                                   "Build-Machine" (System/getenv "HOSTNAME")
                                   "Release-Env"   (name env)}
               :basis             @basis
               :main              'com.geistindersh.mandelbrot.core}))

(defn jar [args]
  (uber args))