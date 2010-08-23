(defproject de.janthomae/leiningenplugin "1.0.0-SNAPSHOT"
  :description "IntelliJ plugin for controlling Leiningen"
  :dependencies [[org.clojure/clojure "1.1.0"]
                 [org.clojure/clojure-contrib "1.1.0"]]
  :aot :all
  :source-path "src/"
  :omit-source "yes"
  :jar-dir "lib/"
  :jar-name "leiningen-interop.jar")