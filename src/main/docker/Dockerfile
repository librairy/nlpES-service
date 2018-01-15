FROM frolvlad/alpine-oraclejdk8:slim
MAINTAINER Librairy <librairy.framework@gmail.com>
VOLUME /tmp
ADD nlp-es-service.jar /app.jar
ADD bin /bin
ENTRYPOINT exec java $JAVA_OPTS -server -jar /app.jar
