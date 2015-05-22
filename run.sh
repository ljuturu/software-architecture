#!/bin/sh

cp -u ./vehicleReview/target/vehicleReview.war $CATALINA_HOME/webapps
xdg-open http://localhost:8080/vehicleReview/
