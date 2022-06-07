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
    [cheshire.core :as json]
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
        :swagger {:info {:title "my-api"
                         :description "https://cljdoc.org/d/metosin/reitit"}}}

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

    ["/upsert"
     {:post {:summary "insert or update food records"
             :parameters {:body map?}
             :handler (fn [{{:keys [body]}  :parameters}]
                        (let [res (logic/upsert-food body)]
                          {:status (first res)
                           :body (second res)}))}}]
    ["/delete"
     {:post {:summary "delete a food record"
             :parameters {:body map?}
             :handler (fn [{{:keys [body]}  :parameters}]
                        (let [res (logic/delete-food body)]
                          {:status (first res)
                           :body (second res)}))}}]]
   ["/restaurant"
    {:swagger {:tags ["restaurant"]}}

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
  ])
