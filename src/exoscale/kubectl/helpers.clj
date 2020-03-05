(ns exoscale.kubectl.helpers
  "Helper functions to build kubectl commands"
  (:refer-clojure :exclude [type namespace]))

(def default-path "kubectl")

(defn kubectl
  ([] (kubectl default-path))
  ([path]
   {:path path
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
   (update m :flags conj  f))
  ([m f value]
   (update m :flags conj [f value])))

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

(defn get-pod
  "Get a pod. Valid options are:

  - `:path`: kubectl path.
  - `:namespace`: the pod namespace.
  - `:resource`: the pod name.
  - `:json?`: enable json formatting"
  [config]
  (cond-> (-> (kubectl (or (:path config) default-path))
              (command :get)
              (type :pods)
              (resource (:resource config))
              (namespace (:namespace config)))

    (:json? config) json))

(defn get-pods
  "Get a list of pods. Valid options are:

  - `:path`: kubectl path.
  - `:namespace`: the pod namespace.
  - `:json?`: enable json formatting"
  [config]
  (cond-> (-> (kubectl (or (:path config) default-path))
              (command :get)
              (type :pods)
              (namespace (:namespace config)))

    (:json? config) json))

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
