(ns aladin.server
  (:require [org.httpkit.server :refer :all]
            [org.httpkit.client :as http]
            [compojure.core :refer [GET defroutes]]
            [compojure.handler :refer [site]]  
            [compojure.route :refer [not-found files]]))

(defn get-weather [lat lon]
  (select-keys
    @(http/get (str "http://aladinonline.androworks.org/get_data.php?latitude=" 
                    lat "&longitude=" lon))
    [:body]))

(defroutes allroutes
  (GET "/weather/:lat/:lon" [lat lon] 
       (merge
         (get-weather lat lon)
         {:headers {"Content-Type" "application/json"}}))
  
  (files "/")
  (not-found "This is not the page you are looking for."))

(defn -main [& args]
  (run-server (site #'allroutes) {:port 3000}))
