# Kie Server Extension to create custom query service

## Build

	mvn clean package

## Deploy

Copy the extension in the kieserver classpath (`<kie-server-war-path>\WEB-INF\lib`)

	mv target/kie-server-ext-queries-1.0-SNAPSHOT.jar ~/apps/rhpam-790/standalone/deployments/kie-server.war/WEB-INF/lib

## Usage

e.g.

	curl -u donato:donato -X GET "http://localhost:8080/kie-server/services/rest/server/queries/containers/order-management_1.1-SNAPSHOT/tasks/instances/pot-owners?page=0&pageSize=10&sortOrder=true" -H "accept: application/json"

