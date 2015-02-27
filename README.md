# Aladin

[http://aladin-thetardis.rhcloud.com/](http://aladin-thetardis.rhcloud.com/)

A tiny webapp for presenting [Aladin](http://www.cnrm.meteo.fr/aladin/?lang=en)-based weather forecast for the Czech republic. Uses the HTML5 geolocation API. 
The original webapp can be found at [aladinonline.androworks.org](http://aladinonline.androworks.org/).

Designed to run on OpenShift. To run it on your local machine do:

```
env HOST=localhost PORT=3000 lein do cljsbuild once, run
```
