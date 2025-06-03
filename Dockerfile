# Use a base image with JDK 21 and Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 9090

CMD ["java", "-jar", "app.jar"]
