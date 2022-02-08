FROM openjdk:17-alpine
MAINTAINER krish.kolukuluri
COPY target/access-management.jar access-management-server.jar
ENTRYPOINT ["java","-jar","/access-management-server.jar"]