(defproject de.janthomae.leiningenplugin/interop "1.0.0-SNAPSHOT"
  :description "Interoperability module for Java interop with Leiningen"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [leiningen-core "2.0.0-preview10"]
                 [midje "1.4.0" :scope "test"]]
  :source-path "src"
  :aot [de.janthomae.leiningenplugin.leiningen.LeiningenAPI])