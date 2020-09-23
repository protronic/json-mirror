#!/bin/bash

# To Run use "bash deploy-script.sh"

user="protronic"
server="prot-subuntu"

ssh "${user}@${server}" "cd /media/daten/theia/project/java/json-mirror && sudo ./mvnw package -Pnative -Dquarkus.native.container-build=true -Dquarkus.container-image.build=true && docker stop json-mirror && docker rm json-mirror && docker run -p 8087:8080 -d --restart always --name json-mirror root/json-mirror:1.0.0-SNAPSHOT"
