#!/bin/bash

# Check if both latitude and longitude are provided
if [ "$#" -ne 2 ]; then
    echo "Usage: ./run.sh <latitude> <longitude>"
    exit 1
fi

# Run the program using Maven
# -q (quiet) ensures only the program output (the filename) is printed
# -Dexec.args passes the latitude and longitude to the Main class
mvn clean compile exec:java -q -Dexec.mainClass="Main" -Dexec.args="$1 $2"