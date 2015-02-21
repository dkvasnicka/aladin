(ns aladin.client 
  (:require [reagent.core :as r]
            [cljs-time.format :refer [parse formatter]]
            [cljs-time.core :refer [now in-hours interval]]
            [goog.string :as gstring]
            [jayq.core :refer [document-ready ajax]])
  (:require-macros [jayq.macros :as jqm]))

(enable-console-print!)

(defonce weather-data (r/atom {}))

(defn update-weather-state [weather]
  (let [curtime (now)
        forecast-from (parse (formatter "yyyy-MM-dd HH:mm:ss Z") 
                             (str (:forecastTimeIso weather) " +0100"))
        index (in-hours (interval forecast-from curtime))]
    (reset! weather-data
            (-> (:parameterValues weather)
                (assoc :icons (mapcat (partial repeat 2) 
                                      (:weatherIconNames weather)))
                (assoc :current-index index)))))

(defn current-value [data val-kw]
  (nth (val-kw data)
       (:current-index data)))

(defn weather-now []
  [:h1 [:img {:src (str "/img/meteo/" (current-value @weather-data :icons) ".svg")}]
   (gstring/format "%.1f" (current-value @weather-data :TEMPERATURE)) " °C" ])

(defn show-weather [data]
   (jqm/let-ajax [weather 
                  {:url 
                   (str "/weather/" (-> data .-coords .-latitude) "/" (-> data .-coords .-longitude))}]
     (update-weather-state weather)
     (r/render-component [weather-now]
                         (.getElementById js/document "container"))))

(jqm/ready
  (let [geo (.-geolocation js/navigator)]
    (if geo
      (.getCurrentPosition geo show-weather)
      (println "GTFO"))))
