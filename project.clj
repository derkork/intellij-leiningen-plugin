(defproject de.janthomae/leiningenplugin "1.1.0-SNAPSHOT"
  :description "IntelliJ plugin for controlling Leiningen"
  :dependencies [[org.clojure/clojure "1.1.0"]]
  :dev-dependencies [[junit/junit "4.8.2"]]
  :source-path "src"
  :resources-path "lein-resources"
  :compile-path "classes"
  :aot :all
;  :disable-implicit-clean true
  :omit-source true
  :library-path "lib"
  :target-dir "out"
  :jar-name "leiningen-interop.jar")