(ns build
  (:require
    [clojure.tools.build.api :as build])
  (:import
    (java.time LocalDateTime ZoneId)
    (java.time.format DateTimeFormatter)))

(def latest-revision (build/git-process {:git-args "rev-parse --short HEAD"}))
(defn -get-patch-version []
  (let [last-version-bump-commit "bbd0da60"                 ;; Count the commits since the version bump as the patches
        git-arg                  (format "rev-list %s...%s --count" last-version-bump-commit latest-revision)]
    (build/git-process {:git-args git-arg})))

(def lib 'mandelbrot)
(def version (format "0.2.%s" (-get-patch-version)))
(def class-dir "target/classes")
(def uber-file (format "target/%s-%s-standalone.jar" (name lib) version))

;; delay to defer side effects (artifact downloads)
(def basis (delay (build/create-basis {:project "deps.edn"})))

(defn clean [_]
  (build/delete {:path "target"}))

(defn uber [{:keys [env]
             :or   {env :debug}}]
  (clean nil)
  (build/copy-dir {:src-dirs   ["resources"]
                   :target-dir class-dir})
  (build/compile-clj {:basis        @basis
                      :bindings     {#'*warn-on-reflection* true}
                      :compile-opts {:direct-linking          true
                                     :disable-locals-clearing (= env :debug)}
                      :ns-compile   '[com.geistindersh.mandelbrot.core]
                      :class-dir    class-dir})
  (build/uber {:class-dir class-dir
               :uber-file uber-file
               :manifest  {"Revision"      latest-revision
                           "Build-Date"    (.format (LocalDateTime/now (ZoneId/of "America/Chicago"))
                                                    (DateTimeFormatter/ofPattern "yyyy/MM/dd HH:mm:SS"))
                           "Built-By"      (System/getenv "USER")
                           "Build-Machine" (System/getenv "HOSTNAME")
                           "Release-Env"   (name env)}
               :basis     @basis
               :main      'com.geistindersh.mandelbrot.core}))

(defn jar [args]
  (uber args))