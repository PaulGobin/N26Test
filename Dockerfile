FROM openjdk:8-jdk-alpine
MAINTAINER Paul Gobin <PaulGobin@gmail.com>

VOLUME /tmp
ARG JAR_FILE
ADD target/app.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]