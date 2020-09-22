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

(deftest labels-test
  (is (= {:path "kubectl"
          :flags [[:-l "a=1"]]}
         (-> (kubectl)
             (labels {:a 1}))))
  (is (= {:path "kubectl"
          :flags [[:-l "a=keyword-value"]]}
         (-> (kubectl)
             (labels {:a :keyword-value}))))
  (is (= {:path "kubectl"
          :flags [[:-l "a=string-value"]]}
         (-> (kubectl)
             (labels {:a "string-value"}))))
  (is (= {:path "kubectl"
          :flags [[:-l "a=first-value"]
                  [:-l "b=second-value"]]}
         (-> (kubectl)
             (labels {:a "first-value"
                      :b "second-value"})))))

(deftest get-pod-test
  (is (= {:path "kubectl"
          :command :get
          :type :pods
          :resource :foo
          :flags [[:-n :backend]
                  [:-o :json]]}
         (get-pod {:namespace :backend
                   :resource :foo
                   :json? true})))

  (is (= {:path "kubectl"
          :command :get
          :type :pods
          :resource :foo
          :flags [[:-n :backend]
                  [:-l "x=one"]
                  [:-l "y=two"]
                  [:-o :yaml]]}
         (get-pod {:namespace :backend
                   :resource :foo
                   :labels {:x :one :y :two}
                   :yaml? true})))

  (is (= {:path "kubectl"
          :command :get
          :type :pods
          :resource :foos
          :flags [[:-n :backend]
                  [:-l "x=one"]
                  [:-l "y=two"]]}
         (get-pods {:namespace :backend
                    :resource :foos
                    :labels {:x :one :y :two}}))))

(deftest get-secrets-test
  (is (= {:path "kubectl"
          :command :get
          :type :secrets
          :flags [[:-n :backend]
                  [:-o :json]]}
         (get-secrets {:namespace :backend :json? true})))

  (is (= {:path "kubectl"
          :command :get
          :type :secrets
          :flags [[:-n :backend]
                  [:-l "x=one"]
                  [:-l "y=two"]
                  [:-o :yaml]]}
         (get-secrets {:namespace :backend
                       :labels {:x :one :y :two}
                       :yaml? true})))

  (is (= {:path "kubectl"
          :command :get
          :type :secrets
          :flags [[:-n :backend]
                  [:-l "x=one"]
                  [:-l "y=two"]]}
         (get-secrets {:namespace :backend
                       :labels {:x :one :y :two}}))))

(deftest delete-secrets-test
  (is (= {:path "kubectl"
          :command :delete
          :type :secrets
          :flags [[:-n :backend]
                  [:-l "x=one"]
                  [:-l "y=two"]]}
         (delete-secrets {:namespace :backend
                          :labels {:x :one :y :two}}))))

(deftest get-deployment-test
  (is (= {:path "kubectl"
          :command :get
          :type :deployments
          :resource :deployment-name
          :flags [[:-n :backend] [:-o :json]]}
         (get-deployment {:namespace :backend
                          :resource :deployment-name
                          :json? true})))

  (is (= {:path "kubectl"
          :command :get
          :type :deployments
          :flags [[:-n :backend]
                  [:-l "x=one"]
                  [:-l "y=two"]
                  [:-o :yaml]]}
         (get-deployment {:namespace :backend
                          :labels {:x :one :y :two}
                          :yaml? true})))

  (is (= {:path "kubectl"
          :command :get
          :type :deployments
          :resource :deployment-name
          :flags [[:-n :backend]
                  [:-l "x=one"]
                  [:-l "y=two"]]}
         (get-deployment {:namespace :backend
                          :resource :deployment-name
                          :labels {:x :one :y :two}}))))

(deftest delete-deployment-test
  (is (= {:path "kubectl"
          :command :delete
          :type :deployments
          :resource :deployment-name
          :flags [[:-n :backend]]}
         (delete-deployment {:namespace :backend
                             :resource :deployment-name}))))

(deftest apply-stdin-test
  (is (= {:path "kubectl"
          :command :apply
          :stdin "foo"
          :flags [:-f
                  [:-o :json]]}
         (apply-stdin {:json? true
                       :stdin "foo"}))))
