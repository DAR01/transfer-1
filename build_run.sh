#!/usr/bin/env bash
mvn package -DskipTests; java -jar target/transfer-1.0.0-SNAPSHOT-fat.jar
