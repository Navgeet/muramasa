(ns muramasa.core
  (:require [datomic.api :as d]
            [muramasa.schema :refer [schema]]))

(set! *warn-on-reflection* true)

(defn scratch-conn
  "Create a connection to an anonymous, in-memory database."
  []
  (let [uri (str "datomic:mem://" (d/squuid))]
    (d/delete-database uri)
    (d/create-database uri)
    (d/connect uri)))


(comment

  (def conn (scratch-conn))
  (def db (d/db conn))

  @(d/transact conn schema)

  (d/q '[:find ?e .
         :where [?e :db/doc "Hello world"]]
       db)

  ;; to refer to entities by sha don't need to do anything
  ;; just create a normal entity including the sha as an attr
  ;; This works because sha is a unique identity
  ;; so inserting a new tempid works lke upsert
  ;; since git objects are immutable, this should be okay
