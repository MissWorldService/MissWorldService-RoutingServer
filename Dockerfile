FROM openjdk:8
ARG JAR_FILE=./target/routing-service-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} service.jar
ENTRYPOINT ["java","-jar","/service.jar"]