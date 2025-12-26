#!/bin/bash

# Clean the project
./gradlew clean

# Assemble the debug APK
./gradlew assembleDebug

# Display success message
echo "Build successful!"