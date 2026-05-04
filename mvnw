#!/bin/bash
# Maven Wrapper script
set -e

DIR="$(cd "$(dirname "$0")" && pwd)"
MVNW_REPO="https://repo.maven.apache.org/maven2"
MVNW_VERSION="3.2.0"
MVNW_JAR="$DIR/.mvn/wrapper/maven-wrapper.jar"

if [ ! -f "$MVNW_JAR" ]; then
    mkdir -p "$(dirname "$MVNW_JAR")"
    curl -o "$MVNW_JAR" "$MVNW_REPO/org/apache/maven/wrapper/maven-wrapper/$MVNW_VERSION/maven-wrapper-$MVNW_VERSION.jar"
fi

java -jar "$MVNW_JAR" "$@"
