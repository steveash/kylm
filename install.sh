#!/bin/sh
mvn install:install-file -DgroupId=com.github.steveash \
  -DartifactId=kylm \
  -Dversion=20151124.1 \
  -Dpackaging=jar \
  -Dfile=$PWD/dist/lib/kylm-20151124.1.jar

