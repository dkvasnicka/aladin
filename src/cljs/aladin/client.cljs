(ns aladin.client 
  (:require [reagent.core :as r]
            [cljs-time.format :refer [parse formatter]]
            [cljs-time.core :refer [now in-hours interval]]
            [jayq.core :refer [document-ready ajax]])
  (:require-macros [jayq.macros :as jqm]))

(enable-console-print!)

(defn doweather [data]
   (jqm/let-ajax [weather {:url (str "/weather/" 
                                     (-> data .-coords .-latitude)
                                     "/"
                                     (-> data .-coords .-longitude))}]
     (println weather)
     (let [curtime (now)
           forecast-from (parse (formatter "yyyy-MM-dd HH:mm:ss Z") 
                                (str (:forecastTimeIso weather) " +0100"))]
       (println
         (nth (-> weather :parameterValues :TEMPERATURE)
              (in-hours (interval forecast-from curtime)))))))

(jqm/ready
  (let [geo (.-geolocation js/navigator)]
    (if geo
      (.getCurrentPosition geo doweather)
      (println "GTFO"))))
