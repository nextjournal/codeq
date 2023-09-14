(ns explore
  {:nextjournal.clerk/no-cache true
   :nextjournal.clerk/budget 100}
  (:require [clojure.datafy :as datafy]
            [datomic.api :as d]
            [datomic.codeq.core :as codeq]
            [nextjournal.clerk :as clerk]))

(def conn
  (d/connect "datomic:mem://git"))

(def db
  (d/db conn))

(d/touch (d/entity db :tx/commit))


(def idents
  (sort-by :db/id
           (d/query {:query '{:find [[(pull ?ident [*]) ...]]
                              :where [[?ident :db/ident _]]}
                     :args [db]})))

(def ident-resolver
  (into {}
        (map (juxt #(select-keys % [:db/id]) :db/ident))
        idents))

(def resolved-idents
  (clojure.walk/postwalk-replace ident-resolver idents))

^{::clerk/viewer clerk/table}
(filterv #(> (:db/id %) 64) resolved-idents)

(def commit-entity
  (d/entity db [:git/sha "285bb99daacb8842063d627a8bd6265af1182752"]))

(def a-commit
  (d/touch commit-entity))

(datafy/datafy commit-entity)

(datafy/nav commit-entity :commit/author commit-author)


#_(nextjournal.clerk.viewer/present
   (clerk/with-viewers (clerk/add-viewers [{:pred (fn [x] (instance? datomic.query.EntityMap x))
                                            :transform-fn (clerk/update-val)}])
     (d/entity db [:git/sha "285bb99daacb8842063d627a8bd6265af1182752"])))
