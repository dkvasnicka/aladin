(ns aladin.client 
  (:require [reagent.core :as r]
            [cljs-time.format :refer [parse formatter unparse]]
            [cljs-time.core :refer [now in-hours interval plus hours]]
            [goog.string :as gstring]
            [jayq.core :refer [document-ready ajax]])
  (:require-macros [jayq.macros :as jqm]))

(defonce weather-data (r/atom {}))
(defonce place (r/atom ""))

(defn start-timestamp [weather]
  (parse (formatter "yyyy-MM-dd HH:mm:ss Z") 
         (str (:forecastTimeIso weather) " +0100")))

(defn update-weather-state [weather]
  (let [curtime (now)
        forecast-from (start-timestamp weather)
        index (in-hours (interval forecast-from curtime))]
    (reset! weather-data
            (-> (:parameterValues weather)
                (assoc :icons (mapcat (partial repeat 2) 
                                      (:weatherIconNames weather)))
                (assoc :current-index index)
                (assoc :times (iterate #(plus % (hours 1)) forecast-from))))))

(defn current-value [data val-kw]
  (nth (val-kw data)
       (:current-index data)))

(defn deg-c [n]
  (str (gstring/format "%.1f" n) " °C"))

(defn icon [iname & {:keys [attrs] :or {attrs {}}}]
  [:img (merge attrs {:src (str "/img/meteo/" iname ".svg")})])

(defn weather-now []
  [:h2
    (icon (current-value @weather-data :icons))
    (deg-c (current-value @weather-data :TEMPERATURE))])

(defn place-label []
  [:h1 @place])

(defn every-other-since [idx]
  (comp (partial take-nth 2) 
        (partial drop (inc idx))))

(defrecord Forecast [datetime icon-name temp])

(defn forecast-block [hourly-data]
  (vector :div {:class "forecast-block"}
          [:h5 (unparse (formatter "EEE HH:mm") (:datetime hourly-data))]
          [:h4 (deg-c (:temp hourly-data))]
          (icon (:icon-name hourly-data) :attrs {:class "forecast-icon"})))

(defn forecast []
  (into
    [:div {:class "pure-u-1"}]
    (map forecast-block
         (apply map ->Forecast 
                (map (every-other-since (:current-index @weather-data))
                     ((juxt :times :icons :TEMPERATURE) @weather-data))))))

(defn app []
  [:div {:class "pure-g"}
   [:div {:class "pure-u-1"}
    [place-label]
    [weather-now]]
   [forecast]])

(defn xpath-string [doc xpath]
  (-> doc 
      (.evaluate xpath doc nil (.-STRING_TYPE js/XPathResult) nil)
      (.-stringValue)))

(defn show-weather [loc]
  (let [lat (-> loc .-coords .-latitude)
        lon (-> loc .-coords .-longitude)]
    (jqm/let-ajax [weather {:url (str "/weather/" lat "/" lon)}
                   rgeocode {:url (str "http://api4.mapy.cz/rgeocode?lat=" lat "&lon=" lon) 
                             :dataType :xml}]
      (reset! place (xpath-string rgeocode "/rgeocode/@label"))
      (update-weather-state weather)
      (r/render-component [app] (.getElementById js/document "placeholder")))))

(jqm/ready
  (if-let [geo (.-geolocation js/navigator)]
    (.getCurrentPosition geo show-weather)
    (js/alert "Bez geolokace to (zatím) nejde :(")))
