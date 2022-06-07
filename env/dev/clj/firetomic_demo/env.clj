(ns firetomic-demo.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [firetomic-demo.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[firetomic-demo started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[firetomic-demo has shut down successfully]=-"))
   :middleware wrap-dev})
