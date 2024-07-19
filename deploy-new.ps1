.\mvnw package;
scp .\target\lib\* root@docker:/media/daten/json-mirror/lib;
scp .\target\json-mirror-*-SNAPSHOT-runner.jar root@docker:/media/daten/json-mirror/json-mirror.jar;
ssh root@docker 'docker stop json-mirror && docker rm json-mirror && docker run -d -p 8087:8080 --restart always -v /media/daten/json-mirror:/json-mirror --name json-mirror maven:latest java -jar /json-mirror/json-mirror.jar'