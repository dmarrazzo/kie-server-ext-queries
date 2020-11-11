# Kie Server Extension to create custom query service

## Goal

Add a new REST endpoint that retrieves task filtered by `containerId`

## Build

	mvn clean package

## Deploy

Copy the extension in the kieserver classpath (`<kie-server-war-path>\WEB-INF\lib`)

	mv target/kie-server-ext-queries-1.0-SNAPSHOT.jar ~/apps/rhpam-790/standalone/deployments/kie-server.war/WEB-INF/lib

## Usage

e.g.

	curl -u donato:donato -X GET "http://localhost:8080/kie-server/services/rest/server/queries/containers/order-management_1.1-SNAPSHOT/tasks/instances/pot-owners?page=0&pageSize=10&sortOrder=true" -H "accept: application/json"

## Reference Documentation

[Extending an existing KIE Server capability with a custom REST API endpoint](https://access.redhat.com/documentation/en-us/red_hat_process_automation_manager/7.9/html-single/managing_red_hat_process_automation_manager_and_kie_server_settings/index#kie-server-extensions-client-proc_execution-server)