(ns firetomic-demo.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[firetomic-demo started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[firetomic-demo has shut down successfully]=-"))
   :middleware identity})
