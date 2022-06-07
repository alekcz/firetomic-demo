(ns firetomic-demo.core
  (:require
    [firetomic-demo.handler :as handler]
    [firetomic-demo.nrepl :as nrepl]
    [luminus.http-server :as http]
    [firetomic-demo.config :refer [env]]
    [clojure.tools.cli :refer [parse-opts]]
    [clojure.tools.logging :as log]
    [mount.core :as mount]
    [firetomic-demo.logic :as logic])
  (:gen-class))

;; log uncaught exceptions in threads
(Thread/setDefaultUncaughtExceptionHandler
  (reify Thread$UncaughtExceptionHandler
    (uncaughtException [_ thread ex]
      (log/error {:what :uncaught-exception
                  :exception ex
                  :where (str "Uncaught exception on" (.getName thread))}))))

(def cli-options
  [["-p" "--port PORT" "Port number"
    :parse-fn #(Integer/parseInt %)]])

(mount/defstate ^{:on-reload :noop} http-server
  :start
  (do
    (http/start
      (-> env
          (assoc  :handler (handler/app))
          (update :port #(or (-> env :options :port) %))
          (select-keys [:handler :host :port])))
    (logic/init-db logic/datahike-config))    
  :stop
  (http/stop http-server))

(mount/defstate ^{:on-reload :noop} repl-server
  :start
  (when (env :nrepl-port)
    (nrepl/start {:bind (env :nrepl-bind)
                  :port (env :nrepl-port)}))
  :stop
  (when repl-server
    (nrepl/stop repl-server)))


(defn stop-app []
  (doseq [component (:stopped (mount/stop))]
    (log/info component "stopped"))
  (shutdown-agents))

(defn start-app [args]
  (doseq [component (-> args
                        (parse-opts cli-options)
                        mount/start-with-args
                        :started)]
    (log/info component "started"))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))

(defn -main [& args]
  (start-app args))
