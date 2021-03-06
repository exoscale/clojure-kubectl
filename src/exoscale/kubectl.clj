(ns exoscale.kubectl
  (:require [clojure.java.shell :as shell]
            [clojure.spec.alpha :as s]
            [exoscale.ex :as ex]))

(defn sort-flags
  [flags]
  (sort
   (fn [f1 f2]
     (or (= :-f f2)
         (and (sequential? f2)
              (= :-f (first f2)))))
   flags))

(defn add-flags
  [result flags]
  (into result
        (comp (mapcat (fn [flag]
                        (cond-> flag
                          (not (coll? flag))
                          vector)))
              (map name))
        (sort-flags flags)))

(defn build-shell-command
  "Takes a map representing a kubectl command.
  Returns a vector of string containing the command pass to `shell/sh`"
  [{:as cmd :keys [path command type resource flags stdin]}]
  (cond-> []
    (some? path)
    (conj path)

    (some? command)
    (conj (name command))

    (some? type)
    (conj (name type))

    (some? resource)
    (conj (name resource))

    (not-empty flags)
    (add-flags flags)

    stdin
    (conj "-" :in stdin)))

(defn run-command
  "Takes a map representing a kubectl command, executes it.
  Throws if the command returns an error."
  [cmd]
  (when-not (:path cmd)
    (throw  (ex/ex-fault "path is missing in command"
                         {:command cmd})))
  (let [result (apply shell/sh (build-shell-command cmd))]
    (when-not (zero? (:exit result))
      (throw (ex/ex-fault "error executing kubectl command"
                          {:command cmd
                           :result result})))
    (:out result)))

(defprotocol Runner
  (run [this cmd] "Run a kubectl command"))

(defn await!
  "Runs cmd every 2s until condition f is satisfied, quits after timeout expired"
  [cmd f timeout-ms]
  (ex/assert-spec-valid (s/int-in 2000 Long/MAX_VALUE) timeout-ms)
  (let [start-time (System/currentTimeMillis)]
    (loop []
      (let [result (run-command cmd)]
        (cond
          (f result)
          result

          (>= (- (System/currentTimeMillis) start-time)
              timeout-ms)
          (ex/ex-interrupted! "Operation timed out after retries")

          :else
          (do
            (Thread/sleep 2000)
            (recur)))))))
