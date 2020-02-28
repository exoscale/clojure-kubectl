(defproject clojure-kubectl "0.1.0-SNAPSHOT"
  :description "A wrapper around the kubectl CLI"
  :url "https://github.com/exoscale/clojure-kubectl"
  :plugins [[lein-codox "0.10.7"]]

  :dependencies [[exoscale/ex "0.3.4"]
                 [org.clojure/clojure "1.10.1"]]
  :repl-options {:init-ns clojure-kubectl.core}
  :profiles {:test {:plugins [[lein-test-report-junit-xml "0.2.0"]]}}
  :deploy-repositories [["releases" {:url "s3p://exo-artifacts/releases"
                                     :no-auth       true
                                     :sign-releases false}]]
  :release-tasks [["vcs" "assert-committed"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["vcs" "commit"]
                  ["vcs" "tag" "--no-sign"]
                  ["deploy"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["vcs" "commit"]
                  ["vcs" "push"]]
  :codox {:source-uri   "https://github.com/exoscale/clojure-kubectl/blob/master/{filepath}#L{line}"
          :doc-files ["README.md"]
          :metadata     {:doc/format :markdown}
          :source-paths ["src"]})
