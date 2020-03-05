(ns clojure-kubectl.helpers-test
  (:require [exoscale.kubectl.helpers :refer :all]
            [clojure.test :refer :all]))

(deftest kubectl-test
  (testing "default path for kubectl"
    (is (= {:path "kubectl"
            :flags []}
           (kubectl))))
  (testing "override kubectl path"
    (is (= {:path "/tmp/kubectl"
            :flags []}
           (kubectl "/tmp/kubectl")))))

(deftest command-test
  (is (= {:path "kubectl"
          :flags []
          :command :get}
         (-> (kubectl)
             (command :get)))))

(deftest type-test
  (is (= {:path "kubectl"
          :flags []
          :type :pod}
         (-> (kubectl)
             (type :pod)))))

(deftest resource-test
  (is (= {:path "kubectl"
          :flags []
          :resource :my-resource}
         (-> (kubectl)
             (resource :my-resource)))))

(deftest flag-test
  (testing "simple flag"
    (is (= {:path "kubectl"
            :flags [:--dry-run]}
         (-> (kubectl)
             (flag :--dry-run)))))
  (testing "simple flag"
    (is (= {:path "kubectl"
            :flags [:--dry-run]}
         (-> (kubectl)
             (flag :--dry-run))))))

(deftest namespace-test
  (is (= {:path "kubectl"
          :flags [[:-n :backend]]}
         (-> (kubectl)
             (namespace :backend)))))

(deftest json-test
  (is (= {:path "kubectl"
          :flags [[:-o :json]]}
         (-> (kubectl)
             json))))

(deftest yaml-test
  (is (= {:path "kubectl"
          :flags [[:-o :yaml]]}
         (-> (kubectl)
             yaml))))

(deftest stdin-test
  (is (= {:path "kubectl"
          :flags []
          :stdin "foo"}
         (-> (kubectl)
             (stdin "foo")))))

(deftest get-pod-test
  (is (= {:path "kubectl"
          :command :get
          :type :pods
          :resource :foo
          :flags [[:-n :backend]
                  [:-o :json]]}
         (get-pod {:namespace :backend
                   :resource :foo
                   :json? true}))))

(deftest get-pods-test
  (is (= {:path "kubectl"
          :command :get
          :type :pods
          :flags [[:-n :backend]
                  [:-o :json]]}
         (get-pods {:namespace :backend
                    :json? true}))))

(deftest apply-stdin-test
  (is (= {:path "kubectl"
          :command :apply
          :stdin "foo"
          :flags [:-f
                  [:-o :json]]}
         (apply-stdin {:json? true
                       :stdin "foo"}))))
