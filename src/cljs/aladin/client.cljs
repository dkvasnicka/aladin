(ns aladin.client 
  (:require [reagent.core :as r]
            [jayq.core :refer [document-ready ajax]])
  (:require-macros [jayq.macros :as jqm]))

(enable-console-print!)

(-> js/navigator
    (.-geolocation)
    (.getCurrentPosition println))
