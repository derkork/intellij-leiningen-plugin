(ns de.janthomae.leiningenplugin.leiningen.LeiningenProjectFile
  (:gen-class
    :prefix lpf-
    :methods [[hello [String] String]]))

(defn lpf-hello [this msg]
  (str "Hello, World to " msg "!"))