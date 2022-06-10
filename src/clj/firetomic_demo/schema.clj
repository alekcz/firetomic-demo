(ns firetomic-demo.schema 
  (:gen-class))

(def schema 
  [{:db/ident :food/id
    :db/valueType :db.type/string
    :db/unique :db.unique/identity}

   {:db/ident :restaurant/id
    :db/valueType :db.type/string
    :db/unique :db.unique/identity}

   {:db/ident :food/restaurantid
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one} 
  
   {:db/ident :restaurant/menu
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/many}])   