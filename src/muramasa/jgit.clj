(ns muramasa.jgit
  (:require [clj-jgit.porcelain :refer [load-repo]]
            [clj-jgit.querying :refer [rev-list]]
            [clj-jgit.internal :refer [new-rev-walk new-tree-walk]])
  (:import [org.eclipse.jgit.api Git]
           [org.eclipse.jgit.lib ObjectId Repository]
           [org.eclipse.jgit.revwalk RevWalk RevCommit RevTree RevTag RevBlob]
           [org.eclipse.jgit.treewalk TreeWalk]))

(defprotocol IGitObject
  (parse [object repo rev-walk]
    "Parse a JGit object.")

  (nodes [object]
    "Returns a list of node shas referenced by the object.")

  (serialize [object]
    "Serializes an object into datomic entity."))

(defn reducer [parse-fn acc object]
  (let [parsed (parse-fn object)]
    (if (acc (:sha parsed))
      acc
      (loop [acc acc
             refs (nodes parsed)]
        (if (empty? refs)
          (assoc acc (:sha parsed) parsed)
          (recur
           (reducer parse-fn
                    (assoc acc (:sha parsed) parsed)
                    (first refs))
           (rest refs)))))))


(defrecord Commit [^String sha
  IGitObject
  (parse [this & args]
    this)

  (nodes [this]
    [tree])

  (serialize [this]
    {:git/sha sha
     :git/type :git.types/commit
     :git.commit/tree tree}))

(defrecord Tree [^String sha
                 nodes]
  IGitObject
  (parse [this & args]
    this)

  (nodes [this]
    (map :sha nodes))

  (serialize [this]
    {:git/sha sha
     :git/type :git.types/tree
     :git.tree/nodes nodes}))

(defrecord Blob [^String sha
                 bytes]
  IGitObject
  (parse [this & args]
    this)

  (nodes [this]
    [])

  (serialize [this]
    {:git/sha sha
     :git/type :git.types/blob
     :bytes bytes}))


(extend-type String
  GitObject
  (parse [^String sha ^Git repo ^RevWalk rev-walk]
    (parse (.parseAny rev-walk (ObjectId/fromString sha))
           repo
           rev-walk)))

(extend-type RevCommit
  GitObject
  (parse [^RevCommit commit ^Git repo ^RevWalk rev-walk]
    (map->Commit {:sha (.getName commit)
                  :tree (.getName (.getTree commit))
                  :parents (map #(.getName %)
                                (.getParents commit))
                  :msg (.getFullMessage commit)
                  ;; :author (.getAuthorIdent commit)
                  ;; :committer (.getCommitterIdent commit)
                  :time (java.util.Date. (* (.getCommitTime commit) 1000))})))

(extend-type RevTree
  GitObject
  (parse [^RevTree tree ^Git repo ^RevWalk rev-walk]
    (let [tree-walk (doto (TreeWalk. (.getRepository repo))
                      (.addTree tree))
          nodes (loop [nodes []
                       next? (.next tree-walk)]
                  (if next?
                    (recur (conj nodes
                                 {:sha (.getName (.getObjectId tree-walk 0))
                                  :type (if (.isSubtree tree-walk)
                                          :tree
                                          :blob)
                                  :mode (str (.getFileMode tree-walk 0))
                                  :name (.getNameString tree-walk)})
                           (.next tree-walk))
                    nodes))]
      (map->Tree {:sha (.getName tree)
                  :nodes nodes}))))

(extend-type RevBlob
  GitObject
  (parse [^RevBlog blob ^Git repo ^RevWalk rev-walk]
    (map->Blob {:sha (.getName blob)
                :bytes (.getBytes (.open (.getRepository repo) blob))})))

(comment

  (def repo (load-repo "/home/nav/repos/dimebag/"))
  (def rev-walk (new-rev-walk repo))
  (def commits (vec (rev-list repo)))
  (def commit (first commits))
  (reduce (partial reducer #(parse % repo rev-walk))
          {}
          commits))
