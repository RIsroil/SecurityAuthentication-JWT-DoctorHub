# ==== Stage 1: Build ====
FROM maven:3.9.6-eclipse-temurin-24 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package -DskipTests


# ==== Stage 2: Run ====
FROM eclipse-temurin:24-jre
WORKDIR /app

COPY --from=build /app/target/*.jar doctorhub.jar

EXPOSE 9091

CMD ["java", "-jar", "doctorhub.jar"]
