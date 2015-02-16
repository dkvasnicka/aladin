(defproject aladin "0.1.0-SNAPSHOT"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0-alpha4"]
                 [http-kit "2.1.16"] 
                 [javax.servlet/servlet-api "2.5"]
                 [compojure "1.3.1"]
                 [hiccup "1.0.5"]
                 
                 ; cljs
                 [org.clojure/clojurescript "0.0-2760"]
                 [reagent "0.5.0-alpha3"]
                 [jayq "2.5.4"]]
  
  :source-paths ["src/clj"]
  :main aladin.server
  
  :cljsbuild {
    :builds [{
        :source-paths ["src/cljs"]
        :compiler {
          :output-to "public/app.js"
          :optimizations :whitespace
          :pretty-print true
        }}]
  })
