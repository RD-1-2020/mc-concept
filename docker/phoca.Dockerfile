# Stage 1: Build the phoca service
FROM gradle:8.14-jdk24 AS build
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . .
# Skip tests as they might require a running database
RUN ./gradlew :phoca:bootJar --no-daemon -x test

# Stage 2: Create the runtime image
FROM openjdk:24-slim
# Install curl for healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /home/gradle/src/phoca/build/libs/*.jar app.jar
# Expose HTTP and gRPC ports
EXPOSE 8888
EXPOSE 9090
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]
