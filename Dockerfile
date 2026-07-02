# Build stage
FROM maven:3.8.6-eclipse-temurin-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre
COPY --from=build /target/turf-booking-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8086
ENTRYPOINT ["java","-jar","app.jar","--server.port=8086"]
