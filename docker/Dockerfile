FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY shared-messaging shared-messaging
COPY student-service student-service
COPY enrolment-service enrolment-service
COPY notification-service notification-service
COPY library-service library-service
COPY reporting-service reporting-service
COPY load-test load-test

ARG SERVICE_MODULE=student-service
RUN mvn -pl ${SERVICE_MODULE} -am package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
RUN apt-get update && apt-get install -y --no-install-recommends wget && rm -rf /var/lib/apt/lists/*
ARG SERVICE_MODULE=student-service
ARG SERVICE_PORT=8081
COPY --from=build /app/${SERVICE_MODULE}/target/${SERVICE_MODULE}-1.0.0-SNAPSHOT.jar app.jar
EXPOSE ${SERVICE_PORT}
ENV SPRING_PROFILES_ACTIVE=docker
ENTRYPOINT ["java", "-jar", "app.jar"]
