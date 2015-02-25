(ns aladin.server
  (:require [org.httpkit.server :refer :all]
            [org.httpkit.client :as http]
            [compojure.core :refer [GET defroutes]]
            [compojure.handler :refer [site]]  
            [compojure.route :refer [not-found files]]
            [cheshire.core :refer :all]))

(def env (into {} (System/getenv)))

(defn get-weather [lat lon]
  (parse-string
    (:body
      @(http/get (str "http://aladinonline.androworks.org/get_data.php?latitude=" 
                      lat "&longitude=" lon)))
    true))

(defroutes allroutes
  (GET "/weather/:lat/:lon" [lat lon] 
       {:headers {"Content-Type" "application/clojure"}
        :body (pr-str (get-weather lat lon))})
  
  (files "/")
  (not-found "This is not the page you are looking for."))

(defn -main [& args]
  (run-server (site #'allroutes) 
              {:ip (env "HOST") :port (Integer/parseInt (env "PORT"))}))
