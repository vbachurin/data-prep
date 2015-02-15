# Talend Data Preparation - API
![alt text](http://www.talend.com/sites/all/themes/talend_responsive/images/logo.png "Talend")

This folder contains the REST API service that provides all services and operation the Data Preparation user may need (i.e. 
all operations invoked by the Web UI). Compared to other "low" level services, this API service provides additional features such as:

* Orchestrating invocation of multiple services.
* Caching
* A fault tolerant service (e.g. in case of low level services failures).

## Prerequisites

You need Java *8* (or higher) and Maven 3.x (tested with 3.2.2 on Fedora 21 with OpenJDK 1.8.0_25-b18).

## Usage
To build and start an instance of the data set service, you just have to run this command:
```
$ mvn -Dserver.port=8080 clean spring-boot:run
```
This will start a server listening on port 8080 (you may customize the server port with the property server.port).
If no "server.port" argument is specified, it defaults to 8888.

It is also possible to start on a random port:
```
$ mvn -Dserver.port=0 clean spring-boot:run
```
You should look in the console the line that indicates the port:
```
$ mvn -Dserver.port=0 clean spring-boot:run
... (many lines omitted) ...
2014-12-31 10:27:04.499  INFO 8426 --- [lication.main()] s.b.c.e.t.TomcatEmbeddedServletContainer : Tomcat started on port(s): 58996/http
2014-12-31 10:27:04.501  INFO 8426 --- [lication.main()] org.talend.dataprep.dataset.Application  : Started Application in 7.297 seconds (JVM running for 10.494)
```
(in this example, server started on port 58996).

## Low level services URLs

This API service needs access to low level services such as Data Set Service and Transformation Service. Both services URLs are
declared in the configuration with the following properties:

* dataset.service.url: URL to the Data Set Service.
* transformation.service.url: URL to the Transformation Service.

This means you may start the API service with the following command to customize the location of the low level services:
```
$ mvn -Ddataset.service.url=http://localhost:8080/datasets -Dtransformation.service.url=http://localhost:8180/transform clean spring-boot:run
```
In this example, API service will expect Data Set Service operations to be in "http://localhost:8080/datasets" and all
Transformation Service operation in "http://localhost:8180/transform".

Although these URLs must be reachable, Data Set and Transformation Services are only called when necessary (starting low 
level services isn't actually a prerequisite but strongly advised if you wish to serve requests).

## Build a Docker image
This project can be packages as a Docker image. You simply have to run:
```
$ mvn -Pdocker install
```
By default, the profile 'docker' isn't active, you need to explicitly enable it ("-Pdocker"). Running this command performs the following:
* Compiles sources.
* Packages all classes.
* Build a docker image that run the services with name "talend/dataprep-dataset:${version}" (e.g. "talend/dataprep-dataset:1.0.M0-SNAPSHOT").
After "mvn install" completes, run the following command to quickly test the build:
```
$ docker -p 8888:8888 run talend/dataprep-api:latest
```
This runs the latest image version and forwards image's port 8888 to localhost 8888. After initialization, go to http://localhost:8888.

### Docker environment properties

Docker image comes with the following environment properties:
* TDP_API_SERVER_PORT: port for the API service (used for server.port).
* TDP_API_DATASET_SERVICE_URL: URL for the Data Set Service 
* TDP_API_TRANSFORMATION_SERVICE_URL: URL for the Transformation Service.
Note: value supports environment property placeholders, this means a environment property such as:
```
TDP_API_DATASET_SERVICE_URL: http://${DATASET_PORT_8080_TCP_ADDR}:${DATASET_PORT_8080_TCP_PORT}/datasets
```
...uses values from environment properties DATASET_PORT_8080_TCP_ADDR and DATASET_PORT_8080_TCP_PORT. Unresolved properties are kept as is.

## Documentation
REST service is self documented. Once started, go to http://localhost:8888 (modify 8888 if you choose a different port)
and then expand 'datasets' category. You can explore and even test the REST interface from this web page.

## License

Copyright (c) 2006-2015 Talend
