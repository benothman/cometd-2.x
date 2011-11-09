#!/bin/sh





rm -rf /Applications/eclipse/apache-tomcat-7.0.22/webapps/cometd*
mvn clean install
cp target/cometd.war /Applications/eclipse/apache-tomcat-7.0.22/webapps/

