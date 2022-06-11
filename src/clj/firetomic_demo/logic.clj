(ns firetomic-demo.logic
  (:require [datahike-firebase.core]
            [firetomic-demo.schema  :as ss]
            [datahike.migrate :refer [export-db import-db]]
            [datahike.api :as d]
            [org.httpkit.client :as http]
            [clojure.edn :as edn]
            [clj-http.client :as client]))

(def datahike-config nil)

(defn parse-body [{:keys [body]}]
  (if-not (empty? body)
    (edn/read-string body)
    ""))


(defn api-request
  ([method url]
   (api-request method url nil nil))
  ([method url data]
   (api-request method url data nil))
  ([method url data opts]
   (-> (client/request (merge {:url (str "http://localhost:4000" url)
                               :method method
                               :throw-exceptions? false
                               :content-type "application/edn"
                               :accept "application/edn"}
                              (when (or (= method :post) data)
                                {:body (str data)})
                              opts))
       parse-body)))

(defn transact [txn]
  (api-request :post "/transact" {:tx-data txn} {:headers {:db-name "clojured"}}))

(defn q 
  ([query]
    (q query []))
  ([query args]
    (api-request :post "/q"  {:query query :args args} {:headers {:db-name "clojured"}}))
  ([query args history-type]
    (api-request :post "/q"  {:query query :args args} {:headers {:db-name "clojured" :db-history-type history-type}})))

(defn init-db [_]
  (let [dbs (api-request :get "/databases")]
    (when (-> dbs :databases count (= 1))
      (api-request :post "/create-database" 
          { :db "http://host.docker.internal:9000"
            :name "clojured"
            :keep-history? true
            :schema-flexibility :read})
      (transact ss/schema))
    (when (empty? (api-request :get "/schema" {} {:headers {:db-name "clojured"}}))
        (transact ss/schema))))

(defn delete-db []
    (api-request :post "/delete-database" 
      { :db "http://host.docker.internal:9000"
        :name "clojured"
        :keep-history? true
        :schema-flexibility :read}))

(defn list-food []
  (let [res (q  '[:find ?e ?id ?name ?r ?p ?ri
                    :where 
                    [?e :food/name ?name]
                    [?e :food/id ?id]
                    [?e :food/rating ?r]
                    [?e :food/price ?p]
                    [?e :food/restaurantid ?ri]])]
    [200 {:food res :server-message "the food you've eaten"}]))

(defn list-food-with-pull []
  (let [res (q  '[:find (pull ?e [* {:food/restaurantid [*]}])
                    :where 
                    [?e :food/id _]])]
    [200 {:food res :server-message "the food you've eaten"}]))

(defn upsert-food [data]
  (let [res (transact 
            [{:food/id (:id data)
              :food/name (:name data)
              :food/rating (:rating data)
              :food/price (:price data)
              :food/restaurantid [:restaurant/id (:restaurantid data)]}])]
    (println res)
    [200 (assoc data :tx-data (:tx-data res) :server-message "thanks for the food data")]))

(defn delete-food [data]
  (let [res (transact
              [[:db.fn/retractEntity [:food/id (:id data)]]])]
    [200 (assoc data :tx-data (:tx-dat res) :server-message "bye-bye food data")]))

(defn list-restaurants []
  (let [res (q  '[:find ?e ?id ?name ?l
                    :where 
                    [?e :restaurant/name ?name]
                    [?e :restaurant/id ?id]
                    [?e :restaurant/location ?l]])]
    [200 {:restaurants res :server-message "some cool places to eat"}]))

(defn list-restaurants-with-pull []
  (let [res (q  '[:find (pull ?e [*])
                    :where 
                    [?e :restaurant/id _]])]
    [200 {:restaurants res :server-message "some cool places to eat"}]))

(defn upsert-restaurant [data]
   (let [res (transact 
              [{:restaurant/id (:id data)
                :restaurant/name (:name data)
                :restaurant/location (:location data)}])]
    [200 (assoc data :tx-data (:tx-data res) :server-message "thanks for the restaurant data")]))

(defn delete-restaurant [data]
  (let [res (transact 
              [[:db.fn/retractEntity [:food/id (:id data)]]])]
    [200 (assoc data :tx-data (:tx-data res) :server-message "bye-bye restaurant data")]))

(defn query-fave [_]
  (let [res1 (q  '[:find (max ?r)
                    :where 
                    [?e :food/id ?id]
                    [?e :food/rating ?r]])
        res2 (q  '[:find (pull ?e [*])
                    :in $ ?max
                    :where 
                    [?e :food/id ?id]
                    [?e :food/rating ?max]] 
              (-> res1 flatten))]
    [200 {:food res2 :server-message "your favourite is"}]))

(defn query-value [_]
  (let [res (->>
             (q '[:find (max ?v)
                     :where [?e :food/rating ?r] 
                            [?e :food/price ?p] 
                            [(/ ?r ?p) ?v]])
              (flatten)
              (q ' [:find (pull ?e [*])
                    :in $ ?value 
                    :where [?e :food/id ?id] 
                          [?e :food/rating ?r] 
                          [?e :food/price ?p] 
                          [(/ ?r ?p) ?v]
                          [(= ?v ?value)]]))
        _ (println res)]
    [200 {:food res :server-message "best value for money is"}]))

(defn query-best-in-town [data]
    (let [res (->> 
                (q  '[:find (max ?r) 
                      :in $ ?loc
                      :where 
                      [?e :food/id ?id]
                      [?e :food/rating ?r]
                      [?e :food/restaurantid ?ri]
                      [?ri :restaurant/location ?loc]]
                    [(:location data)])
                (flatten)
                (concat [(:location data)])
                (q  '[:find (pull ?e [*])
                      :in $ ?loc ?best
                      :where 
                      [?e :food/id ?id]
                      [?e :food/rating ?r]
                      [(= ?r ?best)]
                      [?e :food/restaurantid ?ri]
                      [?ri :restaurant/location ?loc]]))]
  [200 {:food res :server-message "the best in town is"}]))

(defn query-best-5 [_]
  (let [res (->> 
              (q  '[:find (max 5 ?r) .
                    :where 
                    [?e :food/id ?id]
                    [?e :food/rating ?r]])
              flatten
              vec
              (conj [])
              (q  '[:find (pull ?e [*])
                    :in $ ?max
                    :where 
                    [?e :food/id ?id]
                    [?e :food/rating ?r]
                    [(.contains ?max ?r)]]))]
  [200 {:food res :server-message "your best 5 are"}]))

(defn query-best-best [data]
  (println "do stuff with" data)
  [200 (assoc data :server-message "the best of them all is")])

(defn query-evolution [data]
  (let [res (q  '[:find ?tx ?id ?rating
                  :in $ ?id 
                  :where 
                  [?e :food/id ?id ?tx]
                  [?e :food/rating ?rating]] 
              [(:id data)]
              "history")]
  [200 {:evolution (->> res (sort-by first) reverse) :server-message "the best change too"}]))