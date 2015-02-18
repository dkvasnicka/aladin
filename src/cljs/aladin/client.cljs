(ns aladin.client 
  (:require [reagent.core :as r]
            [jayq.core :refer [document-ready ajax]])
  (:require-macros [jayq.macros :as jqm]))

(enable-console-print!)

(defn doweather [data]
   (jqm/let-ajax [weather {:url (str "/weather/" 
                                     (-> data .-coords .-latitude)
                                     "/"
                                     (-> data .-coords .-longitude)) 
                           :dataType :json}]
     (println weather)))

(jqm/ready
  (let [geo (.-geolocation js/navigator)]
    (if geo
      (.getCurrentPosition geo doweather)
      (println "GTFO"))))
