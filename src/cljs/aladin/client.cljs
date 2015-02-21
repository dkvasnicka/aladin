(ns aladin.client 
  (:require [reagent.core :as r]
            [cljs-time.format :refer [parse formatter]]
            [cljs-time.core :refer [now in-hours interval]]
            [goog.string :as gstring]
            [jayq.core :refer [document-ready ajax]])
  (:require-macros [jayq.macros :as jqm]))

(enable-console-print!)

(defonce weather-data (r/atom {}))
(defonce place (r/atom ""))

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
  [:div {:id "current-weather" :class "pure-u-1-2"}
    [:img {:src (str "/img/meteo/" (current-value @weather-data :icons) ".svg")}]
    (gstring/format "%.1f" (current-value @weather-data :TEMPERATURE)) " °C"])

(defn place-label []
  [:div {:id "place" :class "pure-u-1-2"} @place])

(defn app []
  [:div {:class "pure-g"}
   [place-label]
   [weather-now]])

(defn xpath-string [doc xpath]
  (.-stringValue 
    (.evaluate doc xpath doc nil (.-STRING_TYPE js/XPathResult) nil)))

(defn show-weather [loc]
  (let [lat (-> loc .-coords .-latitude)
        lon (-> loc .-coords .-longitude)]
    (jqm/let-ajax [weather {:url (str "/weather/" lat "/" lon)}
                   rgeocode {:url (str "http://api4.mapy.cz/rgeocode?lat=" lat "&lon=" lon) 
                             :dataType :xml}]
      (reset! place (xpath-string rgeocode "/rgeocode/@label"))
      (update-weather-state weather)
      (r/render-component [app]
                          (.-body js/document)))))

(jqm/ready
  (let [geo (.-geolocation js/navigator)]
    (if geo
      (.getCurrentPosition geo show-weather)
      (println "GTFO"))))
