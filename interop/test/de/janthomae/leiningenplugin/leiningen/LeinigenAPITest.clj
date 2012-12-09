(ns de.janthomae.leiningenplugin.leiningen.LeinigenAPITest
  "Test the LeiningenAPI functions"
  (:use midje.sweet
        de.janthomae.leiningenplugin.leiningen.LeiningenAPI)
  (:require [leiningen.core.project :as p]
            [leiningen.core.classpath :as cp]))


(facts
  "About the way we flatten the dependency tree"
  (let [project (assoc p/defaults :dependencies '[[midje "1.4.0"]])
        hierarchy (cp/dependency-hierarchy :dependencies project)
        result (find-all-artifacts hierarchy)]
    (count result) => 12
    (map #(contains? % :dependency ) result) => (has every? true?)
    (map #(contains? % :version ) result) => (has every? true?)
    (map #(contains? % :scope ) result) => (has every? true?)
    (map #(contains? % :artifactid ) result) => (has every? true?)
    (map #(contains? % :file ) result) => (has every? true?)
    (map #(contains? % :groupid ) result) => (has every? true?)))
