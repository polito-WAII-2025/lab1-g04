# Step 1: Use the official Gradle image with JDK 23 as the build environment
FROM gradle:8.10-jdk23 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Gradle configuration files and project source code
COPY build.gradle.kts settings.gradle.kts gradlew /app/
COPY gradle /app/gradle
COPY src /app/src

# Download dependencies and build the project
RUN gradle build --no-daemon

# Step 2: Use a minimal base image for running the application
FROM openjdk:23-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR from the previous step
COPY --from=build /app/build/libs/*-all.jar /app/app.jar

# Expose the application's port (if needed)
EXPOSE 8081

# Command to run the application
CMD ["java", "-jar", "app.jar", "./inputFiles/waypoints.csv", "./inputFiles/custom-parameters.yml", "./outputFiles/output.json"]

