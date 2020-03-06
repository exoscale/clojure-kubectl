(ns clojure-kubectl.kubectl-test
  (:require [exoscale.kubectl :refer :all]
            [clojure.test :refer :all]))

(deftest sort-flags-test
  (is (= :-f (last (sort-flags [:-f :foo :bar]))))
  (is (= :-f (last (sort-flags [:foo :-f :bar]))))
  (is (= :-f (last (sort-flags [:foo :bar :-f]))))
  (is (= [:-f "foo.yaml"] (last (sort-flags [[:-f "foo.yaml"] :foo :bar]))))
  (is (= [:-f "foo.yaml"] (last (sort-flags [:foo [:-f "foo.yaml"] :bar]))))
  (is (= [:-f "foo.yaml"] (last (sort-flags [:foo :bar [:-f "foo.yaml"]])))))

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
  (is (= ["kubectl" "get" "pods" "-n" "backend" "-" :in "foo"]
         (build-shell-command {:path "kubectl"
                               :command :get
                               :type :pods
                               :stdin "foo"
                               :flags [[:-n :backend]]}))))
