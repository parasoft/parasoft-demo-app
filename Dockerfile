# Keep the Java 11 baseline, but use a runtime newer than 11.0.16 to avoid its cgroup-v2 metrics startup defect.
FROM tomcat:9.0.122-jre11-temurin-noble

ARG TOMCAT_HOME=/usr/local/tomcat
ARG WAR_FILE=build/libs/parasoft-demo-app-*.war

USER root:root

# The minimal Temurin JRE image does not include unzip, which is required to expand the WAR for JDBC-driver injection.
RUN apt-get update \
    && apt-get install --no-install-recommends -y unzip \
    && rm -rf /var/lib/apt/lists/*

COPY ${WAR_FILE} ${TOMCAT_HOME}/webapps/ROOT.war

# To enable injecting Virtualize JDBC driver into PDA.
RUN unzip ${TOMCAT_HOME}/webapps/ROOT.war -d ${TOMCAT_HOME}/webapps/ROOT
RUN rm ${TOMCAT_HOME}/webapps/ROOT.war

EXPOSE 8080 9001 50051 61623 61624 61626
