FROM tomcat:9.0.65-jre11-openjdk

ARG TOMCAT_HOME=/usr/local/tomcat
ARG WAR_FILE=build/libs/parasoft-demo-app-*.war

USER root:root

COPY ${WAR_FILE} ${TOMCAT_HOME}/webapps/ROOT.war

# To enable injecting Virtualize JDBC driver into PDA.
RUN unzip ${TOMCAT_HOME}/webapps/ROOT.war -d ${TOMCAT_HOME}/webapps/ROOT
RUN rm ${TOMCAT_HOME}/webapps/ROOT.war

EXPOSE 8080 9001 61623 61624 61626
