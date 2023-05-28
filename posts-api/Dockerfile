# Use the official Maven image as the build environment
FROM maven:3.8.4-openjdk-17-slim AS build

# Set the working directory inside the container
WORKDIR /build

# Copy the Maven project file and download the dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the application source code
COPY src/ ./src/

# Build the application
RUN mvn package -DskipTests

# Use the official OpenJDK 17 as the base image for the runtime environment
FROM openjdk:17-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /build/target/*.jar app.jar

# Expose the port your Spring Boot app runs on
EXPOSE 8080

# Set the entry point to run your Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]