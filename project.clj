(defproject clojure-kubectl "0.1.0-SNAPSHOT"
  :description "A wrapper around the kubectl CLI"
  :url "https://github.com/exoscale/clojure-kubectl"
  :dependencies [[exoscale/ex "0.3.4"]
                 [org.clojure/clojure "1.10.1"]]
  :repl-options {:init-ns clojure-kubectl.core})
