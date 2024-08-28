#!/bin/bash

# Step 1: Navigate to the React project directory
cd front

# Step 2: Build the React project
npm run build

# Step 3: Remove old static resources in the Spring Boot project
rm -rf ../api/src/main/resources/static/*

# Step 4: Copy new build files to the Spring Boot static resources directory
mkdir ../api/src/main/resources/static
cp -r build/* ../api/src/main/resources/static

# Step 5: Navigate to the Spring Boot project directory
cd ..

# Step 6: Build the Spring Boot project
./gradlew clean build
