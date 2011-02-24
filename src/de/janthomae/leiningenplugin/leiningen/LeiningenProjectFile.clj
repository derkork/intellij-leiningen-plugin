(ns de.janthomae.leiningenplugin.leiningen.LeiningenProjectFile
  (:import [java.io File])
  (:gen-class
    :init init
    :prefix -
    :methods [[getName [] String]]
    :constructors {[String] []}
  ))

(def project nil)

(defmacro defproject [project-name version & args]
  ;; This is necessary since we must allow defproject to be eval'd in
  ;; any namespace due to load-file; we can't just create a var with
  ;; def or we would not have access to it once load-file returned.
  `(do
     (let [m# (apply hash-map (quote ~args))
           root# ~(.getParent (java.io.File. *file*))]
;       (alter-var-root #'project
;                       (fn [_#] (assoc m#
;                                  :name ~(name project-name)
;                                  :group ~(or (namespace project-name)
;                                              (name project-name))
;                                  :version ~version
;                                  :compile-path (or (:compile-path m#)
;                                                    (str root# "/classes"))
;                                  :source-path (or (:source-path m#)
;                                                   (str root# "/src"))
;                                  :library-path (or (:library-path m#)
;                                                    (str root# "/lib"))
;                                  :test-path (or (:test-path m#)
;                                                 (str root# "/test"))
;                                  :resources-path (or (:resources-path m#)
;                                                      (str root# "/resources"))
;                                  :root root#))))
;     (def ~(symbol (name project-name)) project)))
)))

(defn read-project [file]
  (binding [*ns* (the-ns 'de.janthomae.leiningenplugin.leiningen.LeiningenProjectFile)]
  (load-file file))
)

(defn -init [file]
   (read-project file))

(defn -getName [this]
    (:name project)
  )




