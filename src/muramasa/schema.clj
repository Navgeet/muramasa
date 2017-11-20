(ns muramasa.schema)

(def schema
  [
   ;; copied from codeq
   {:db/id #db/id[:db.part/db]
    :db/ident :git/type
    :db/valueType :db.type/keyword
    :db/cardinality :db.cardinality/one
    :db/doc "Type enum for git objects - one of :commit, :tree, :blob, :tag"
    :db.install/_attribute :db.part/db}

   {:db/id #db/id[:db.part/db]
    :db/ident :git/sha
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "A git sha, should be in repo"
    :db/unique :db.unique/identity
    :db.install/_attribute :db.part/db}

   {:db/id #db/id[:db.part/db]
    :db/ident :git.repo/uri
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "A git repo uri"
    :db/unique :db.unique/identity
    :db.install/_attribute :db.part/db}

   {:db/id #db/id[:db.part/db]
    :db/ident :git.commit/parents
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/many
    :db/doc "Parents of a commit"
    :db.install/_attribute :db.part/db}

   {:db/id #db/id[:db.part/db]
    :db/ident :git.commit/tree
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/doc "Root node of a commit"
    :db.install/_attribute :db.part/db}

   {:db/id #db/id[:db.part/db]
    :db/ident :git.commit/msg
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "Short commit message"
    :db.install/_attribute :db.part/db}

   {:db/id #db/id[:db.part/db]
    :db/ident :git.commit/message
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "Full commit message"
    :db/fulltext true
    :db.install/_attribute :db.part/db}

   {:db/id #db/id[:db.part/db]
    :db/ident :git.commit/time
    :db/valueType :db.type/instant
    :db/cardinality :db.cardinality/one
    :db/doc "Timestamp of commit"
    :db.install/_attribute :db.part/db}

   ;; {:db/id #db/id[:db.part/db]
   ;;  :db/ident :commit/author
   ;;  :db/valueType :db.type/ref
   ;;  :db/cardinality :db.cardinality/one
   ;;  :db/doc "Person who authored a commit"
   ;;  :db.install/_attribute :db.part/db}
   ;;
   ;; {:db/id #db/id[:db.part/db]
   ;;  :db/ident :commit/authoredAt
   ;;  :db/valueType :db.type/instant
   ;;  :db/cardinality :db.cardinality/one
   ;;  :db/doc "Timestamp of authorship of commit"
   ;;  :db/index true
   ;;  :db.install/_attribute :db.part/db}
   ;;
   ;; {:db/id #db/id[:db.part/db]
   ;;  :db/ident :commit/committer
   ;;  :db/valueType :db.type/ref
   ;;  :db/cardinality :db.cardinality/one
   ;;  :db/doc "Person who committed a commit"
   ;;  :db.install/_attribute :db.part/db}
   ;;
   ;; {:db/id #db/id[:db.part/db]
   ;;  :db/ident :commit/committedAt
   ;;  :db/valueType :db.type/instant
   ;;  :db/cardinality :db.cardinality/one
   ;;  :db/doc "Timestamp of commit"
   ;;  :db/index true
   ;;  :db.install/_attribute :db.part/db}

   {:db/id #db/id[:db.part/db]
    :db/ident :git.tree/nodes
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/many
    :db/doc "Nodes of a git tree"
    :db/isComponent true
    :db.install/_attribute :db.part/db}

   {:db/id #db/id[:db.part/db]
    :db/ident :git.node/filename
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/doc "filename of a tree node"
    :db.install/_attribute :db.part/db}

   ;; {:db/id #db/id[:db.part/db]
   ;;  :db/ident :node/paths
   ;;  :db/valueType :db.type/ref
   ;;  :db/cardinality :db.cardinality/many
   ;;  :db/doc "paths of a tree node"
   ;;  :db.install/_attribute :db.part/db}

   {:db/id #db/id[:db.part/db]
    :db/ident :git.node/object
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/doc "Git object (tree/blob) in a tree node"
    :db.install/_attribute :db.part/db}

   {:db/id #db/id[:db.part/db]
    :db/ident :git.node/type
    :db/valueType :db.type/keyword
    :db/cardinality :db.cardinality/one
    :db/doc "Object type of node - :tree or :blob"
    :db.install/_attribute :db.part/db}

   {:db/id #db/id[:db.part/db]
    :db/ident :git.node/modeOctal
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "File mode as octal string"
    :db.install/_attribute :db.part/db}

   ;; {:db/id #db/id[:db.part/db]
   ;;  :db/ident :git/prior
   ;;  :db/valueType :db.type/ref
   ;;  :db/cardinality :db.cardinality/one
   ;;  :db/doc "Node containing prior value of a git object"
   ;;  :db.install/_attribute :db.part/db}

   ;; {:db/id #db/id[:db.part/db]
   ;;  :db/ident :email/address
   ;;  :db/valueType :db.type/string
   ;;  :db/cardinality :db.cardinality/one
   ;;  :db/doc "An email address"
   ;;  :db/unique :db.unique/identity
   ;;  :db.install/_attribute :db.part/db}

   {:db/id #db/id[:db.part/db]
    :db/ident :file/name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "A filename"
    :db/fulltext true
    :db/unique :db.unique/identity
    :db.install/_attribute :db.part/db}

   {:db/id #db/id[:db.part/db]
    :db/ident :git.blob/uri
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "uri for a blob's content"
    :db/unique :db.unique/identity
    :db.install/_attribute :db.part/db}
   ])
