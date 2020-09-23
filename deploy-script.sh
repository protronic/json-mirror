#!/bin/bash

# To Run use "bash deploy-script.sh"

REMOTE_USER="protronic"
REMOTE_HOST="prot-subuntu"

bash /home/project/bash/deploy-script-utils/git-push.sh

ssh "${REMOTE_USER}@${REMOTE_HOST}" "cd /media/daten/theia/project/java/json-mirror && docker stop json-mirror && sudo ./mvnw package -Pnative -Dquarkus.native.container-build=true -Dquarkus.container-image.build=true && docker rm json-mirror && docker run -p 8087:8080 -d --restart always --name json-mirror root/json-mirror:1.0.0-SNAPSHOT"
