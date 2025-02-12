# Use OpenJdk base image
FROM openjdk:21-jdk-slim AS builder

# Set author name
LABEL author="Valentyn Sharshon"

# Set the worjimg directory inside the container
WORKDIR /app

# Copy the application JAR file (ensure it`s build first)
COPY target/currency-rate-service.jar app.jar

# Expose the application port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
