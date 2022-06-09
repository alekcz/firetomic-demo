(ns firetomic-demo.routes.services
  (:require
    [reitit.swagger :as swagger]
    [reitit.swagger-ui :as swagger-ui]
    [reitit.ring.coercion :as coercion]
    [reitit.coercion.spec :as spec-coercion]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [reitit.ring.middleware.multipart :as multipart]
    [reitit.ring.middleware.parameters :as parameters]
    [firetomic-demo.middleware.formats :as formats]
    [ring.util.http-response :refer [ok]]
    [firetomic-demo.logic :as logic]))

(defn service-routes []
  ["/api"
   {:coercion spec-coercion/coercion
    :muuntaja formats/instance
    :swagger {:id ::api}
    :middleware [;; query-params & form-params
                 parameters/parameters-middleware
                 ;; content-negotiation
                 muuntaja/format-negotiate-middleware
                 ;; encoding response body
                 muuntaja/format-response-middleware
                 ;; exception handling
                 coercion/coerce-exceptions-middleware
                 ;; decoding request body
                 muuntaja/format-request-middleware
                 ;; coercing response bodys
                 coercion/coerce-response-middleware
                 ;; coercing request parameters
                 coercion/coerce-request-middleware
                 ;; multipart
                 multipart/multipart-middleware]}

   ;; swagger documentation
   ["" {:no-doc true
        :swagger {:info {:title "firetomic-demo"
                         :description "https://github.com/alekcz/firetomic-demo"}}}

    ["/swagger.json"
     {:get (swagger/create-swagger-handler)}]

    ["/api-docs/*"
     {:get (swagger-ui/create-swagger-ui-handler
             {:url "/api/swagger.json"
              :config {:validator-url nil}})}]]

   ["/ping"
    {:get (constantly (ok {:message "pew pew"}))}]
   

   ["/food"
    {:swagger {:tags ["food"]}}
    
    ["/list"
     {:get  {:summary "list the foods you've eaten"
             :handler (fn [_]
                        (let [res (logic/list-food)]
                          {:status (first res)
                           :body (second res)}))}}]
    ["/upsert"
     {:post {:summary "insert or update food records"
             :parameters {:body {:id any? :name any? :rating number? :price double? :restaurantid any?}}
             :handler (fn [{{:keys [body]}  :parameters}]
                        (let [res (logic/upsert-food body)]
                          {:status (first res)
                           :body (second res)}))}}]
    ["/delete"
     {:post {:summary "delete a food record"
             :parameters {:body {:id any?}}
             :handler (fn [{{:keys [body]}  :parameters}]
                        (let [res (logic/delete-food body)]
                          {:status (first res)
                           :body (second res)}))}}]]
   ["/restaurant"
    {:swagger {:tags ["restaurant"]}}

    ["/list"
     {:get  {:summary "list the restaurants you've eaten at"
             :handler (fn [_]
                        (let [res (logic/list-restaurants)]
                          {:status (first res)
                           :body (second res)}))}}]

    ["/upsert"
     {:post {:summary "insert or update restaurant records"
             :parameters {:body map?}
             :handler (fn [{{:keys [body]}  :parameters}]
                        (let [res (logic/upsert-restaurant body)]
                          {:status (first res)
                           :body (second res)}))}}]
    ["/delete"
     {:post {:summary "delete a restaurant record"
             :parameters {:body map?}
             :handler (fn [{{:keys [body]}  :parameters}]
                        (let [res (logic/delete-restaurant body)]
                          {:status (first res)
                           :body (second res)}))}}]]

   ["/query"
    {:swagger {:tags ["query"]}}

    ["/fave"
     {:post {:summary "favourite food"
             :parameters {:body map?}
             :handler (fn [{{:keys [body]}  :parameters}]
                        (let [res (logic/query-fave body)]
                          {:status (first res)
                           :body (second res)}))}}]
    ["/value"
     {:post {:summary "the best value for money"
             :parameters {:body map?}
             :handler (fn [{{:keys [body]}  :parameters}]
                        (let [res (logic/query-value body)]
                          {:status (first res)
                           :body (second res)}))}}]
                           
    ["/best-in-town"
     {:post {:summary "the best value for money"
             :parameters {:body map?}
             :handler (fn [{{:keys [body]}  :parameters}]
                        (let [res (logic/query-best-in-town body)]
                          {:status (first res)
                           :body (second res)}))}}]
    ["/best-5"
     {:post {:summary "the best value for money"
             :parameters {:body map?}
             :handler (fn [{{:keys [body]}  :parameters}]
                        (let [res (logic/query-best-5 body)]
                          {:status (first res)
                           :body (second res)}))}}]
    ["/best-best"
     {:post {:summary "the best value for money"
             :parameters {:body map?}
             :handler (fn [{{:keys [body]}  :parameters}]
                        (let [res (logic/query-best-best body)]
                          {:status (first res)
                           :body (second res)}))}}]]])
