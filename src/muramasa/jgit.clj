(ns muramasa.jgit
  (:require [clj-jgit.porcelain :refer [load-repo]]
            [clj-jgit.querying :refer [rev-list]]
            [clj-jgit.internal :refer [new-rev-walk new-tree-walk]])
  (:import [org.eclipse.jgit.api Git]
           [org.eclipse.jgit.lib ObjectId Repository]
           [org.eclipse.jgit.revwalk RevWalk RevCommit RevTree RevTag RevBlob]
           [org.eclipse.jgit.treewalk TreeWalk]))

(defprotocol IGitObject
  (parse [object repo]
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
                   ^String tree
                   parents
                   msg
                   message
                   time]
  IGitObject
  (parse [this repo]
    this)

  (nodes [this]
    [tree])

  (serialize [this]
    {:git/sha sha
     :git/type :git.types/commit
     :git.commit/tree tree
     :git.commit/parents parents
     :git.commit/msg msg
     :git.commit/message message
     :git.commit/time time}))

(defrecord Tree [^String sha
                 nodes]
  IGitObject
  (parse [this repo]
    this)

  (nodes [this]
    (map :sha nodes))

  (serialize [this]
    {:git/sha sha
     :git/type :git.types/tree
     :git.tree/nodes nodes}))

(defrecord Node [sha
                 type
                 mode
                 filename]
  IGitObject
  (parse [this repo]
    this)

  (nodes [this]
    [sha])

  (serialize [this]
    {:git.node/object sha
     :git/type :git.types/node
     :git.node/type (keyword (str "git.types/" (name type)))
     :git.node/filename filename ;; TODO this should be a ref to a filename object
     :git.node/modeOctal mode
     }))

(defrecord Blob [^String sha
                 bytes]
  IGitObject
  (parse [this repo]
    this)

  (nodes [this]
    [])

  (serialize [this]
    {:git/sha sha
     :git/type :git.types/blob
     :bytes bytes}))


(extend-type String
  IGitObject
  (parse [^String sha ^Git repo]
    (parse (.parseAny (new-rev-walk repo) (ObjectId/fromString sha))
           repo)))

(extend-type RevCommit
  IGitObject
  (parse [^RevCommit commit ^Git repo]
    (map->Commit {:sha (.getName commit)
                  :tree (.getName (.getTree commit))
                  :parents (map #(.getName %)
                                (.getParents commit))
                  :msg (.getShortMessage commit)
                  :message (.getFullMessage commit)
                  ;; :author (.getAuthorIdent commit)
                  ;; :committer (.getCommitterIdent commit)
                  :time (java.util.Date. (* (.getCommitTime commit) 1000))})))

(extend-type RevTree
  IGitObject
  (parse [^RevTree tree ^Git repo]
    (let [tree-walk (doto (TreeWalk. (.getRepository repo))
                      (.addTree tree))
          nodes (loop [nodes []
                       next? (.next tree-walk)]
                  (if next?
                    (recur (conj nodes
                                 (map->Node {:sha (.getName (.getObjectId tree-walk 0))
                                             :type (if (.isSubtree tree-walk)
                                                     :tree
                                                     :blob)
                                             :mode (str (.getFileMode tree-walk 0))
                                             :filename (.getNameString tree-walk)}))
                           (.next tree-walk))
                    nodes))]
      (map->Tree {:sha (.getName tree)
                  :nodes nodes}))))

(extend-type RevBlob
  IGitObject
  (parse [^RevBlog blob ^Git repo]
    (map->Blob {:sha (.getName blob)
                :bytes (.getBytes (.open (.getRepository repo) blob))})))

(comment

  (def repo (load-repo "/home/nav/repos/dimebag/"))

  ;; walk the reachable commit graph
  (def commits (vec (rev-list repo)))

  (def commit (first commits))
  ;; org.eclipse.jgit.revwalk.RevCommit 0x5db00667
  ;; Dang!

  ;; jgit has a nasty api. We define a protool IGitObject
  ;; and extend jgit's RevCommit, RevTree, RevTag and RevBlob

  ;; The protocol  has 3 methods
  ;; parse - Parses a jgit object, returns a clojure record (called object)
  ;; nodes - returns tree nodes to be walked from a object
  ;; serialize - returns a datomic representation for the object

  ;; parse provides a saner way to access native jgit objects
  (parse commit repo)
  ;; #muramasa.jgit.Commit{:sha "ce709d145bbe6d65760fd5ec2dfe22434058dc80",
  ;;                       :tree "00edb8ff8f89067469f8c2bdfdf2b9cc65d0cf06",
  ;;                       :parents ("1be4453c53ecc79ba27db389c3ea8fb66ff26da0"),
  ;;                       :msg "Babelify the app source too.",
  ;;                       :message "Babelify the app source too.\n\nFixes #10509.\n",
  ;;                       :time #inst "2016-01-20T21:43:21.000-00:00"}

  ;; Strings implement the protocol too!
  (parse "86056f64256c605d13da7c83f81e6d4641d16862" repo)

  ;; we provide a basic reducer to walk all objects
  ;; this returns a map of sha vs object
  (reduce (partial reducer #(parse % repo))
          {}
          commits)

  ;; this is not very fast at the moment


  )
