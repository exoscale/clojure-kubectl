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
  (testing "multiple flags"
    (is (= {:path "kubectl"
            :flags [[:-l "foo"] [:-l "bar"]]}
           (-> (kubectl)
               (flag :-l "foo")
               (flag :-l "bar"))))))

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
                      :b "second-value"}))))
  (is (= {:path "kubectl"
          :flags [[:-l "qualified/keyword=first-value"]
                  [:-l "b=second-value"]]}
         (-> (kubectl)
             (labels {:qualified/keyword "first-value"
                      :b "second-value"}))))
  (is (= {:path "kubectl"
          :flags [[:-l "qualified/keyword=keyword-value"]
                  [:-l "b=qualified/value"]]}
         (-> (kubectl)
             (labels {:qualified/keyword :keyword-value
                      :b :qualified/value})))))

(deftest get-pods-test
  (is (= {:path "kubectl"
          :command :get
          :type :pods
          :resource :foo
          :flags [[:-n :backend]
                  [:-o :json]]}
         (get-pods {:namespace :backend
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
         (get-pods {:namespace :backend
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

(deftest get-deployments-test
  (is (= {:path "kubectl"
          :command :get
          :type :deployments
          :resource :deployment-name
          :flags [[:-n :backend] [:-o :json]]}
         (get-deployments {:namespace :backend
                           :resource :deployment-name
                           :json? true})))

  (is (= {:path "kubectl"
          :command :get
          :type :deployments
          :flags [[:-n :backend]
                  [:-l "x=one"]
                  [:-l "y=two"]
                  [:-o :yaml]]}
         (get-deployments {:namespace :backend
                           :labels {:x :one :y :two}
                           :yaml? true})))

  (is (= {:path "kubectl"
          :command :get
          :type :deployments
          :resource :deployment-name
          :flags [[:-n :backend]
                  [:-l "x=one"]
                  [:-l "y=two"]]}
         (get-deployments {:namespace :backend
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

(deftest delete-ingress-test
  (is (= {:path "kubectl"
          :command :delete
          :type :ingress.networking.k8s.io
          :resource :ingress-name
          :flags [[:-n :backend]]}
         (delete-ingresses {:namespace :backend
                            :resource :ingress-name})))

  (is (= {:path "kubectl"
          :command :delete
          :type :ingress.networking.k8s.io
          :resource :ingress-name
          :flags [[:-n :backend]
                  [:-l "foo"]]}
         (delete-ingresses {:namespace :backend
                            :flags [[:-l "foo"]]
                            :resource :ingress-name}))))

(deftest delete-service-test
  (is (= {:path "kubectl"
          :command :delete
          :type :service
          :resource :service-name
          :flags [[:-n :backend]
                  :--foo
                  [:--bar "value"]]}
         (delete-services {:namespace :backend
                           :resource :service-name
                           :flags [:--foo [:--bar "value"]]})))

  (is (= {:path "kubectl"
          :command :delete
          :type :service
          :resource :service-name
          :flags [[:-n :backend]
                  [:-l "env=production"]
                  [:--bar "value"]]}
         (delete-services {:namespace :backend
                           :labels {:env :production}
                           :resource :service-name
                           :flags [[:--bar "value"]]}))))

(deftest get-service-test
  (is (= {:path     "kubectl"
          :command  :get
          :flags    [[:-n :backend]
                     [:-l "env=production"]]
          :resource :deployment-name
          :type     :service}
         (get-services {:namespace :backend
                        :labels {:env :production}
                        :resource :deployment-name})))
  (is (= {:path     "kubectl"
          :command  :get
          :flags    [[:-n :backend]
                     [:-l "env=production"]
                     [:-l "bar"]]
          :resource :deployment-name
          :type     :service}
         (get-services {:namespace :backend
                        :flags [[:-l "bar"]]
                        :labels {:env :production}
                        :resource :deployment-name}))))

(deftest get-ingresses-test
  (is (= {:path     "kubectl"
          :command  :get
          :flags    [[:-n :backend]
                     [:-l "env=production"]]
          :resource :deployment-name
          :type     :ingress.networking.k8s.io}
         (get-ingresses {:namespace :backend
                         :labels {:env :production}
                         :resource :deployment-name})))
  (is (= {:path     "kubectl"
          :command  :get
          :flags    [[:-n :backend]
                     [:-l "env=production"]
                     [:-l "foo"]
                     [:-l "bar"]]
          :resource :deployment-name
          :type     :ingress.networking.k8s.io}
         (get-ingresses {:namespace :backend
                         :flags [[:-l "foo"] [:-l "bar"]]
                         :labels {:env :production}
                         :resource :deployment-name}))))

(deftest apply-stdin-test
  (is (= {:path "kubectl"
          :command :apply
          :stdin "foo"
          :flags [:-f
                  [:-o :json]]}
         (apply-stdin {:json? true
                       :stdin "foo"}))))
