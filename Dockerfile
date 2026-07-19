# Stage 1: Build the application jar file
# =========================================================
# Stage 1: Build the Spring Boot application jar
# =========================================================
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# =========================================================
# Stage 2: Configure the shared multi-process runtime environment
# =========================================================
FROM ubuntu:22.04

# Avoid prompt blockages during package installation installations
ENV DEBIAN_FRONTEND=noninteractive

# Install Java 21, MySQL Server, and clean up temporary apt caches
RUN apt-get update && apt-get install -y \
    openjdk-21-jre-headless \
    mysql-server \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy the built jar file from Stage 1
COPY --from=build /app/target/*.jar app.jar

# Copy and grant execution rights to your runtime startup script
COPY entrypoint.sh .
RUN chmod +x entrypoint.sh

# Expose ports for both your API and external database tools
EXPOSE 8080 3306

# Launch via the entrypoint script
ENTRYPOINT ["./entrypoint.sh"]

