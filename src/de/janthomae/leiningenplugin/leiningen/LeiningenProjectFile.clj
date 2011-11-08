(ns de.janthomae.leiningenplugin.leiningen.LeiningenProjectFile
  (:import [java.io File])
  (:gen-class
    :init init
    :methods [[getName [] String]
              [getNamespace [] String]
              [getVersion [] String]
              [getSourcePath [] String]
              [getTestPath [] String]
              [getResourcesPath [] String]
              [getCompilePath [] String]
              [getLibraryPath [] String]
              [getTargetDir [] String]
              [getProjectFile [] String]
              [isValid [] Boolean]
              [getError [] Throwable]]
    :constructors {[String] []}
    :state state))

;; Internal functions

(defmacro defproject [project-name version & args]
  `(apply hash-map :name (quote ~project-name) :version ~version (quote ~args)))

(defn project [this]
  (:project @(.state this)))

(defn status [this]
  (:status @(.state this)))

(defn exception [this]
  (:exception @(.state this)))

(defn project-path [this]
  (:path @(.state this)))

(defn read-project [file]
  (binding [*ns* (the-ns 'de.janthomae.leiningenplugin.leiningen.LeiningenProjectFile)]
    (try
      {:status true :project (load-file file)}
      (catch Exception e
        {:status false :exception e}))))

;; Class interface

(defn -init [file]
  (let [p (read-project file)]
    [[] (atom (assoc p :path file))]))

(defn -isValid [this]
  (status this))

(defn -getError [this]
  (exception this))

(defn -getName [this]
  (name (:name (project this))))

(defn -getNamespace [this]
  (namespace (:name (project this))))

(defn -getVersion [this]
  (:version (project this)))

(defn -getSourcePath [this]
  (:source-path (project this) "src"))

(defn -getTestPath [this]
  (:test-path (project this) "test"))

(defn -getResourcesPath [this]
  (:resources-path (project this) "resources"))

(defn -getCompilePath [this]
  (:compile-path (project this) "classes"))

(defn -getLibraryPath [this]
  (:library-path (project this) "lib"))

(defn -getTargetDir [this]
  (let [p (project this)]
    (:target-dir p (:jar-dir p ""))))

(defn -getProjectFile [this]
  (project-path this))


