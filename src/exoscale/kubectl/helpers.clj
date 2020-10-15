(ns exoscale.kubectl.helpers
  "Helper functions to build kubectl commands"
  (:require [clojure.string :as s])
  (:refer-clojure :exclude [type namespace])
  (:import (clojure.lang Named)))

(def default-path "kubectl")

(defn kubectl
  ([] (kubectl default-path))
  ([path]
   {:path  path
    :flags []}))

(defn command
  "Specifies the command to use."
  [m cmd]
  (assoc m :command cmd))

(defn type
  "Specifies the resource type to use."
  [m t]
  (assoc m :type t))

(defn resource
  "Add the name of a specific resource to target."
  [m r]
  (assoc m :resource r))

(defn flag
  "Add a flag to the command."
  ([m f]
   (update m :flags conj f))
  ([m f value]
   (update m :flags conj [f value])))

(defn labels
  "Add flags to the command"
  [m kvs]
  (let [stringify (fn [v] (s/trim (if (instance? Named v) (name v) (str v))))
        reducing-fn (fn [acc [k v]] (flag acc :-l (str (stringify k) "=" (stringify v))))]
    (reduce reducing-fn m kvs)))

(defn namespace
  "Specifies the namespace."
  [m ns]
  (flag m :-n ns))

(defn json
  "Asks for a json output."
  [m]
  (flag m :-o :json))

(defn yaml
  "Asks for a yaml output."
  [m]
  (flag m :-o :yaml))

(defn stdin
  "Pass value to kubectl from stdin"
  [m value]
  (assoc m :stdin value))

(defn kubectl-builder
  "Generic kubectl command builder.

  resource-type: k8s resource type
  operation: kubectl command that you want to perform
  config:
    - `:path`: kubectl path.
    - `:namespace`: namespace.
    - `:resource`: the resource name.
    - `:labels`: labels as {key1 value1, key2 value2 ...}
    - `:stdin`: the value of stdin.
    - `:flags`: additional flags for the command.
    - `:yaml?`: enable yaml formatting.
    - `:json?`: enable json formatting."
  ([resource-type operation config]
   (cond-> (-> (kubectl (or (:path config) default-path))
               (type resource-type)
               (command operation))

     (:namespace config)
     (namespace (:namespace config))

     (:resource config)
     (resource (:resource config))

     (seq (:labels config))
     (labels (:labels config))

     (:flags config)
     (update :flags concat (:flags config))

     (:json? config) json
     (:yaml? config) yaml)))

(def get-pods (partial kubectl-builder :pods :get))
(def get-deployments (partial kubectl-builder :deployments :get))
(def delete-deployment (partial kubectl-builder :deployments :delete))
(def delete-secrets (partial kubectl-builder :secrets :delete))
(def get-secrets (partial kubectl-builder :secrets :get))
(def get-ingresses (partial kubectl-builder :ingress.networking.k8s.io :get))
(def delete-ingresses (partial kubectl-builder :ingress.networking.k8s.io :delete))
(def get-services (partial kubectl-builder :service :get))
(def delete-services (partial kubectl-builder :service :delete))

(defn apply-stdin
  "Apply a configuration from stdin. Valid options are:

  - `:path`: kubectl path.
  - `:stdin`: the value of stdin.
  - `:json?`: enable json formatting."
  [config]
  (cond-> (-> (kubectl (or (:path config) default-path))
              (command :apply)
              (flag :-f)
              (stdin (:stdin config)))

    (:json? config) json))
