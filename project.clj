(defproject exoscale/kubectl "0.1.14"
  :description "A wrapper around the kubectl CLI"
  :url "https://github.com/exoscale/clojure-kubectl"
  :plugins [[lein-cljfmt "0.7.0"]
            [lein-cloverage "1.1.2"]]
  :license {:name "ISC"}
  :dependencies [[exoscale/ex "0.3.14"]
                 [org.clojure/clojure "1.10.1"]]
  :repl-options {:init-ns exoscale.kubectl}
  :profiles {:test {:plugins [[lein-test-report-junit-xml "0.2.0"]]}}
  :deploy-repositories [["releases" :clojars] ["snapshots" :clojars]]
  :release-tasks [["vcs" "assert-committed"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["vcs" "commit"]
                  ["vcs" "tag" "--no-sign"]
                  ["deploy"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["vcs" "commit"]
                  ["vcs" "push"]])
