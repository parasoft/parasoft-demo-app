FROM tomcat:11.0-jre17-temurin

ARG TOMCAT_HOME=/usr/local/tomcat
ARG WAR_FILE=build/libs/parasoft-demo-app-*.war

USER root:root

COPY docker/context.xml ${TOMCAT_HOME}/conf/context.xml
COPY ${WAR_FILE} ${TOMCAT_HOME}/webapps/ROOT.war

# To enable injecting Virtualize JDBC driver into PDA.
RUN apt-get update \
    && apt-get dist-upgrade -y \
    && apt-get install -y --no-install-recommends unzip \
    && unzip ${TOMCAT_HOME}/webapps/ROOT.war -d ${TOMCAT_HOME}/webapps/ROOT \
    && apt-get autoremove -y \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*
RUN rm ${TOMCAT_HOME}/webapps/ROOT.war

EXPOSE 8080 9001 50051 61623 61624 61626
