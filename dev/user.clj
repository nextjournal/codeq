(ns user
  (:require [nextjournal.clerk :as clerk]
            [datomic.codeq.core :as codeq]))

(clerk/serve! {:port 7777})

(codeq/main "datomic:mem://git" "master")
