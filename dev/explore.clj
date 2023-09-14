(ns explore
  {:nextjournal.clerk/budget nil}
  (:require [datomic.codeq.core :as codeq]
            [datomic.api :as d]
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

codeq/main
