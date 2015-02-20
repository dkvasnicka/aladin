(ns aladin.client 
  (:require [reagent.core :as r]
            [cljs-time.format :refer [parse formatter]]
            [cljs-time.core :refer [now in-hours interval]]
            [jayq.core :refer [document-ready ajax]])
  (:require-macros [jayq.macros :as jqm]))

(enable-console-print!)

(def weather-data (r/atom {}))

(defn current-index [weather]
  (let [curtime (now)
        forecast-from (parse (formatter "yyyy-MM-dd HH:mm:ss Z") 
                             (str (:forecastTimeIso weather) " +0100"))]
    (in-hours (interval forecast-from curtime))))

(defn weather-now []
  [:h1 (nth (-> @weather-data :parameterValues :TEMPERATURE)
            (current-index @weather-data))])

(defn show-weather [data]
   (jqm/let-ajax [weather {:url (str "/weather/" (-> data .-coords .-latitude) "/" (-> data .-coords .-longitude))}]
     (reset! weather-data weather)
     (r/render-component [weather-now]
                         (.-body js/document))))

(jqm/ready
  (let [geo (.-geolocation js/navigator)]
    (if geo
      (.getCurrentPosition geo show-weather)
      (println "GTFO"))))
