(defproject de.janthomae/leiningenplugin "1.1.0-SNAPSHOT"
  :description "IntelliJ plugin for controlling Leiningen"
  :dependencies [[org.clojure/clojure "1.1.0"]
                 [org.clojure/clojure-contrib "1.1.0"]
                 [junit/junit "4.8.2"]]
  :source-path "src"
  :resources-path "lein-resources"
  :compile-path "classes"
  :aot :all
  :disable-implicit-clean true
  :omit-source true
  :target-dir "lib"
  :jar-name "leiningen-interop.jar")