FROM maven:3.9-eclipse-temurin-21 AS build
ARG MODULE
WORKDIR /app
COPY pom.xml .
COPY shared shared
COPY warehouse-service warehouse-service
COPY central-service central-service
RUN --mount=type=cache,target=/root/.m2 mvn -B -q -pl ${MODULE} -am package -DskipTests

FROM eclipse-temurin:21-jre-alpine
ARG MODULE
WORKDIR /app
COPY --from=build /app/${MODULE}/target/${MODULE}-*.jar service.jar
ENTRYPOINT ["java", "-jar", "service.jar"]
