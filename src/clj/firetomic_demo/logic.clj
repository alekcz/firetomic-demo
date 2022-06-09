(ns firetomic-demo.logic
  (:require [datahike-firebase.core]
            [firetomic-demo.schema  :as ss]
            [datahike.migrate :refer [export-db import-db]]
            [datahike.api :as d]))


(defonce datahike-config {:store {;:db "http://localhost:9000"
                                  ;:root "datahike"
                                  ;:env "GOOGLE_APPLICATION_CREDENTIALS"
                                  :backend :mem}
                      :schema-flexibility :read
                      :keep-history? true})

(defonce conn nil)

(defn build-db [datahike-config]
  (d/create-database datahike-config)
  (->  datahike-config d/connect (d/transact ss/schema)))

(defn init-db [config]
  (try
    (alter-var-root #'conn (fn [_] (d/connect config)))
    (catch Exception _ 
      (println "Failed to connect to db. Building new db")
      (build-db config)
      (alter-var-root #'conn (fn [_] (d/connect config))))))


(defn list-food []
  (println "list stuff")
  [200 (assoc {} :server-message "the food you've eaten")])

(defn upsert-food [data]
  (println "do stuff with" data)
  [200 (assoc data :server-message "thanks for the food data")])

(defn delete-food [data]
  (println "do stuff with" data)
  [200 (assoc data :server-message "bye-bye food data")])

(defn list-restaurants []
  (println "list stuff")
  [200 (assoc {} :server-message "some cool places to eat")])

(defn upsert-restaurant [data]
  (println "do stuff with" data)
  [200 (assoc data :server-message "thanks for the restaurant data")])

(defn delete-restaurant [data]
  (println "do stuff with" data)
  [200 (assoc data :server-message "bye-bye restaurant data")])

(defn query-fave [data]
  (println "do stuff with" data)
  [200 (assoc data :server-message "your favourite is")])

(defn query-value [data]
  (println "do stuff with" data)
  [200 (assoc data :server-message "best value for money is")])

(defn query-best-in-town [data]
  (println "do stuff with" data)
  [200 (assoc data :server-message "the best in town is")])

(defn query-best-5 [data]
  (println "do stuff with" data)
  [200 (assoc data :server-message "the best 5 are")])

(defn query-best-best [data]
  (println "do stuff with" data)
  [200 (assoc data :server-message "the best of them all is")])

