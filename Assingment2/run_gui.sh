#!/usr/bin/env bash
# Compile and run the ProjectAppGUI
set -e
cd "$(dirname "$0")"
mkdir -p out
javac -cp src -d out src/assingment2/*.java
java -cp out assingment2.ProjectAppGUI
