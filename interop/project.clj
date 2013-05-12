(defproject de.janthomae.leiningenplugin/interop "1.0.0-SNAPSHOT"
  :description "Interoperability module for Java interop with Leiningen"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [leiningen-core "2.1.3"]
                 [midje "1.5.1" :scope "test"]]
  :source-path "src"
  :aot [de.janthomae.leiningenplugin.leiningen.LeiningenAPI]
  :profiles
    {:dev
      {:plugins [[lein-midje "3.0.1"]]}})