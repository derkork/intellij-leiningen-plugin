(ns de.janthomae.leiningenplugin.leiningen.LeiningenProjectFile
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
  `(let [m# (apply hash-map ~(cons 'list (args)))]
    (alter-var-root #'project
                     (fn [_#] (assoc m#
                               :name ~(name project-name)
                               :group ~(or (namespace project-name)
                                           (name project-name))
                               :version ~version
                               )))
     (def ~(symbol (name project-name)) project))
  )


(defn read-project ([file]
  (load-file file)))

(defn -init [file]
   (read-project file))

(defn -getName [this]
    (:name project)
  )




