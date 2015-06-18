#!/bin/sh
mvn install:install-file -DgroupId=com.github.steveash \
  -DartifactId=kylm \
  -Dversion=20150421.1 \
  -Dpackaging=jar \
  -Dfile=dist/lib/kylm-20150421.jar

