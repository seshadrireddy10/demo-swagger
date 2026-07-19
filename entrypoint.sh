#!/bin/bash

# Initialize MySQL data directories if they don't exist
if [ ! -d "/var/lib/mysql/mysql" ]; then
    echo "Initializing MySQL data directories..."
    mysqld --initialize-insecure --user=mysql
fi

# Start MySQL daemon in the background
echo "Starting MySQL..."
mysqld --user=mysql &

# Wait for MySQL to be fully available and ready
echo "Waiting for MySQL to accept connections..."
until mysqladmin ping -h "localhost" --silent; do
    sleep 1
done

# Create the application database if it doesn't exist
echo "Creating application database..."
mysql -e "CREATE DATABASE IF NOT EXISTS productdb;"

# Start your Spring Boot application in the foreground
echo "Starting Spring Boot Application..."
exec java -jar app.jar

