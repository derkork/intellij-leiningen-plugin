(defproject de.janthomae/leiningenplugin "1.0.0-SNAPSHOT"
  :description "IntelliJ plugin for controlling Leiningen"
  :dependencies [[org.clojure/clojure "1.1.0"]
                 [org.clojure/clojure-contrib "1.1.0"]]
  :source-path "src/"
  :resources-path "lein-resources/"
  :aot :all
  :disable-implicit-clean true
  :omit-source true
  :jar-dir "lib/"
  :jar-name "leiningen-interop.jar")