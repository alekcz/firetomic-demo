(ns firetomic-demo.logic
  (:require [datahike-firebase.core]
            [firetomic-demo.schema  :as ss]
            [datahike.migrate :refer [export-db import-db]]
            [datahike.api :as d]))


(defonce datahike-config {:store {:db "http://localhost:9000"
                                  :root "datahike"
                                  :env "GOOGLE_APPLICATION_CREDENTIALS"
                                  :backend :firebase}
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
  (let [res (d/q  '[:find ?e ?id ?name ?r ?p ?ri
                    :where 
                    [?e :food/name ?name]
                    [?e :food/id ?id]
                    [?e :food/rating ?r]
                    [?e :food/price ?p]
                    [?e :food/restaurantid ?ri]] 
              @conn)]
    [200 {:food res :server-message "the food you've eaten"}]))

(defn list-food-with-pull []
  (let [res (d/q  '[:find (pull ?e [* {:food/restaurantid [*]}])
                    :where 
                    [?e :food/id _]] 
              @conn)]
    [200 {:food res :server-message "the food you've eaten"}]))

(defn upsert-food [data]
  (let [res (d/transact conn 
            [{:food/id (:id data)
              :food/name (:name data)
              :food/rating (:rating data)
              :food/price (:price data)
              :food/restaurantid [:restaurant/id (:restaurantid data)]}])]
    [200 (assoc data :tx-data (:tx-dat res) :server-message "thanks for the food data")]))

(defn delete-food [data]
  (let [res (d/transact conn
              [[:db.fn/retractEntity [:food/id (:id data)]]])]
    [200 (assoc data :tx-data (:tx-dat res) :server-message "bye-bye food data")]))

(defn list-restaurants []
  (let [res (d/q  '[:find ?e ?id ?name ?l
                    :where 
                    [?e :restaurant/name ?name]
                    [?e :restaurant/id ?id]
                    [?e :restaurant/location ?l]] 
              @conn)]
    [200 {:restaurants res :server-message "some cool places to eat"}]))

(defn list-restaurants-with-pull []
  (let [res (d/q  '[:find (pull ?e [*])
                    :where 
                    [?e :restaurant/id _]] 
              @conn)]
    [200 {:restaurants res :server-message "some cool places to eat"}]))

(defn upsert-restaurant [data]
   (let [res (d/transact conn 
              [{:restaurant/id (:id data)
                :restaurant/name (:name data)
                :restaurant/location (:location data)}])]
    [200 (assoc data :tx-data (:tx-data res) :server-message "thanks for the restaurant data")]))

(defn delete-restaurant [data]
  (let [res (d/transact conn
              [[:db.fn/retractEntity [:food/id (:id data)]]])]
    [200 (assoc data :tx-data (:tx-data res) :server-message "bye-bye restaurant data")]))

(defn query-fave [_]
  (let [db @conn
        res1 (d/q  '[:find (max ?r) .
                    :where 
                    [?e :food/id ?id]
                    [?e :food/rating ?r]] 
              db)
        res2 (d/q  '[:find (pull ?e [*])
                    :in $ ?max
                    :where 
                    [?e :food/id ?id]
                    [?e :food/rating ?max]] 
              db
              res1)]
    [200 {:food res2 :server-message "your favourite is"}]))

(defn query-value [_]
  (let [db @conn
        res (->> db 
              (d/q '[:find (max ?v) .
                     :where [?e :food/rating ?r] 
                            [?e :food/price ?p] 
                            [(/ ?r ?p) ?v]])
              (d/q '[:find (pull ?e [*])
                     :in $ ?value 
                     :where [?e :food/id ?id] 
                            [?e :food/rating ?r] 
                            [?e :food/price ?p] 
                            [(/ ?r ?p) ?v]
                            [(= ?v ?value)]] 
                    db))
        _ (println res)]
    [200 {:food res :server-message "best value for money is"}]))

(defn query-best-in-town [data]
    (let [db @conn
        res (->> 
              (d/q  '[:find (max ?r) .
                        :in $ ?loc
                        :where 
                        [?e :food/id ?id]
                        [?e :food/rating ?r]
                        [?e :food/restaurantid ?ri]
                        [?ri :restaurant/location ?loc]]
                    db
                    (:location data))
              (d/q  '[:find (pull ?e [*])
                        :in $ ?loc ?best
                        :where 
                        [?e :food/id ?id]
                        [?e :food/rating ?r]
                        [(= ?r ?best)]
                        [?e :food/restaurantid ?ri]
                        [?ri :restaurant/location ?loc]] 
                    db
                    (:location data)))]
  [200 {:food res :server-message "the best in town is"}]))

(defn query-best-5 [_]
  (let [db @conn
        res (->> db 
              (d/q  '[:find (max 5 ?r)
                        :where 
                        [?e :food/id ?id]
                        [?e :food/rating ?r]])
              flatten
              vec
              (d/q  '[:find (pull ?e [*])
                        :in $ ?max
                        :where 
                        [?e :food/id ?id]
                        [?e :food/rating ?r]
                        [(.contains ?max ?r)]] db))]
  [200 {:food res :server-message "your best 5 are"}]))

(defn query-best-best [data]
  (println "do stuff with" data)
  [200 (assoc data :server-message "the best of them all is")])

(defn query-evolution [data]
  (let [res (d/q  '[:find ?tx ?id ?rating
                    :in $ ?id 
                    :where 
                    [?e :food/id ?id ?tx]
                    [?e :food/rating ?rating]] 
              (d/history @conn)
              (:id data))]
  [200 {:evolution (->> res (sort-by first) reverse) :server-message "the best change too"}]))