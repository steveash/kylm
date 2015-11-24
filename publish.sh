#!/bin/sh
mvn deploy:deploy-file -DgroupId=com.github.steveash \
  -DartifactId=kylm \
  -Dversion=20151124.1 \
  -Dpackaging=jar \
  -Dfile=dist/lib/kylm-20151124.1.jar \
  -Durl=http://sw-artifactory/artifactory/ext-release-local -DrepositoryId=sw-artifactory
