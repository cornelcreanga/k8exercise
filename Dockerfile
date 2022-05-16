ARG SPARK_IMAGE=gcr.io/spark-operator/spark:v3.1.1-hadoop3
FROM ${SPARK_IMAGE}

# using root
USER root:root
# create directory for applications
RUN mkdir -p /app
#COPY target/k8-exercise-1.0-SNAPSHOT-jar-with-dependencies.jar $SPARK_HOME/jars
#COPY target/k8-exercise-1.0-SNAPSHOT-jar-with-dependencies.jar /
COPY target/k8exercise-1.0-SNAPSHOT.jar /app/app.jar
COPY src/main/resources/log4j.properties /app/log4j.properties

WORKDIR /app

#user
USER root

