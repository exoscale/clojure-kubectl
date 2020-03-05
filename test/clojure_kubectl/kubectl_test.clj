(ns clojure-kubectl.kubectl-test
  (:require [exoscale.kubectl :refer :all]
            [clojure.test :refer :all]))

(deftest build-shell-command-test
  (is (= ["kubectl" "get" "pods"  "foo" "-n" "backend"]
         (build-shell-command {:path "kubectl"
                               :command :get
                               :type :pods
                               :resource :foo
                               :flags [[:-n :backend]]})))
  (is (= ["kubectl" "get" "pods" "-n" "backend"]
         (build-shell-command {:path "kubectl"
                               :command :get
                               :type :pods
                               :flags [[:-n :backend]]})))
  (is (= ["/tmp/kubectl" "get" "pods" "-n" "backend" "--dry-run" "-o" "yaml"]
         (build-shell-command {:path "/tmp/kubectl"
                               :command :get
                               :type :pods
                               :flags [[:-n :backend]
                                       :--dry-run
                                       [:-o :yaml]]})))
  (is (= ["kubectl" "get" "pods" "-n" "backend" "-" "foo"]
         (build-shell-command {:path "kubectl"
                               :command :get
                               :type :pods
                               :stdin "foo"
                               :flags [[:-n :backend]]}))))
