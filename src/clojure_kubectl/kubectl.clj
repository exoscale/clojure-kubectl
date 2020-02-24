(ns clojure-kubectl.kubectl
  (:require [clojure.java.shell :as shell]
            [exoscale.ex :as ex]))

(defn ->name
  [v]
  (if (instance? clojure.lang.Named v)
    (name v)
    v))

(defn add-flags
  [result flags]
  (apply conj result (->> flags flatten (map ->name))))

(defn build-shell-command
  "Takes a map representing a kubectl command.
  Returns a vector of string containing the command pass to `shell/sh`"
  [cmd]
  (->> (cond-> [(-> cmd :path)
                (-> cmd :command ->name)
                (-> cmd :type ->name)
                (-> cmd :resource ->name)]

         (not-empty (:flags cmd))
         (add-flags (:flags cmd))

         (:stdin cmd) (conj "-" (:stdin cmd)))
       (remove nil?)))

(defn run-command
  "Takes a map representing a kubectl command, executes it.
  Throws if the command returns an error."
  [cmd]
  (when-not (:path cmd)
    (throw  (ex/ex-fault "path is missing in command"
                         {:command cmd})))
  (let [result (apply shell/sh cmd)]
    (when-not (zero? (:exit result))
      (throw (ex/ex-fault "error executing kubectl command"
                          {:command cmd
                           :result result})))
    (:stdout result)))

(defprotocol IKubectlCommand
  (run [this cmd] "Run a kubectl command"))
