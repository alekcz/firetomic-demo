(ns firetomic-demo.logic
  (:require [datahike-firebase.core]
            [firetomic-demo.schema  :as ss]
            [datahike.migrate :refer [export-db import-db]]
            [datahike.api :as d]))


(defonce datahike-config {:store {:backend :firebase 
                              :db "http://localhost:9000"
                              :root "datahike"
                              :env "GOOGLE_APPLICATION_CREDENTIALS"}
                      :schema-flexibility :read
                      :keep-history? true})

(defonce conn nil)

(defn build-db [datahike-config]
  (d/create-database datahike-config)
  (->  datahike-config d/connect (d/transact ss/schema)))

(defn init-db [config]
  (try
    (alter-var-root #'conn (fn [_] (d/connect config)))
    (catch Exception e 
      (println "Failed to connect to db. Building new db")
      (build-db config)
      (alter-var-root #'conn (fn [_] (d/connect config))))))


(defn upsert-food [data]
  (println "do stuff with" data)
  [200 (assoc data :server-message "thanks for the food data")])

(defn delete-food [data]
  (println "do stuff with" data)
  [200 (assoc data :server-message "bye-bye food data")])

(defn upsert-restaurant [data]
  (println "do stuff with" data)
  [200 (assoc data :server-message "thanks for the restaurant data")])

(defn delete-restaurant [data]
  (println "do stuff with" data)
  [200 (assoc data :server-message "bye-bye restaurant data")])
