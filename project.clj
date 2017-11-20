(defproject muramasa "0.0.1"
  :description "git -> datomic"
  :url "http://github.com/Navgeet/muramasa"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.eclipse.jgit/org.eclipse.jgit "4.1.1.201511131810-r"]
                 [clj-jgit "0.8.8"]
                 [com.datomic/datomic-free "0.9.5344"]]
  :resource-paths ["resources" "blobs"])
