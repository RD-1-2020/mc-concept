# Stage 1: Build the pusa service
FROM gradle:8.14-jdk24 AS build
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . .
# Skip tests as they might require a running database
RUN ./gradlew :pusa:bootJar --no-daemon -x test

# Stage 2: Create the runtime image
FROM openjdk:24-slim
WORKDIR /app
COPY --from=build /home/gradle/src/pusa/build/libs/*.jar app.jar
# Expose HTTP port
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]
