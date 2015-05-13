# Talend Data Preparation - Data Set Service
![alt text](http://www.talend.com/sites/all/themes/talend_responsive/images/logo.png "Talend")

This folder contains the REST service to all Data Set related operations (creation, removal...).

## Prerequisites

You need Java *8* (or higher) and Maven 3.x (tested with 3.2.2 on Fedora 21 with OpenJDK 1.8.0_25-b18).

## Usage
To build and start an instance of the data set service, you just have to run this command:
```
$ mvn -Dserver.port=8080 clean spring-boot:run
```
This will start a server listening on port 8080 (you may customize the server port with the property server.port).
If no "server.port" argument is specified, it defaults to 8080.

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

## Data set information storage

By default (for ease of deployment), all data set related information is stored in memory (information such as id, column metadata...).
You may switch to a local Mongo DB instance with the property "dataset.metadata.store".
```
$ mvn -Ddataset.metadata.store=mongodb clean spring-boot:run
```
Look for this line in the log:
```
2015-01-06 12:03:57.459  INFO 13798 --- [lication.main()] o.t.d.d.c.DataSetStoreConfiguration      : Data Set metadata store: mongodb
```
(it defaults to "in-memory" value when nothing is specified).

By default, service looks for a Mongo DB instance running on localhost and default port. You may override these settings using properties
during startup. For example, the following command will connect to a Mongo DB instance running on "mongoserver" and port "27017":
```
$ mvn -Ddataset.metadata.store=mongodb -Dspring.data.mongodb.host=mongoserver -Dspring.data.mongodb.port=27017 clean spring-boot:run
```

## Data set content storage

You can choose between 2 content storage:
* local: stores content to <i>${java.io.tmpdir}/talend/tdp/datasets/</i>. You may customize this path using the property "dataset.content.store.local.location" as follows:
```
$ mvn -Ddataset.content.store.local.location=/home/user/permanent_store/
```
* hdfs (experimental):
```
$ mvn -Ddataset.content.store=hdfs -Ddataset.content.store.hdfs.location=file:/
```
In this configuration, dataset.content.store.hdfs.location takes a URI to initialize a HDFS FileSystem instance (currently no credential support).

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
$ docker -p 8080:8080 run talend/dataprep-dataset:latest
```
This runs the latest image version and forwards image's port 8080 to localhost 8080. After initialization, go to http://localhost:8080.

### Multiple instances
To run multiple instances, you may use the command:
```
[fhuaulme@tlnd-fhuaulme ~]$ docker run -p 8080 -d talend/dataprep-dataset:1.0.M0-SNAPSHOT
336c348ba4dd683d859938b3caee2d7f6c5742e33d06debce8fbbd57ff3de9c4
[fhuaulme@tlnd-fhuaulme ~]$ docker run -p 8080 -d talend/dataprep-dataset:1.0.M0-SNAPSHOT
578d7da8218abcf90f8ac4c72dab33c29311e202dbfca17ac3b74cd6737def89
[fhuaulme@tlnd-fhuaulme ~]$ docker run -p 8080 -d talend/dataprep-dataset:1.0.M0-SNAPSHOT
234a75e85f109d185efc6c6640a763869abc641ccf1f3bd03e105b9be1ea6801
[fhuaulme@tlnd-fhuaulme ~]$ docker ps
CONTAINER ID        IMAGE                                     COMMAND                CREATED             STATUS              PORTS                     NAMES
234a75e85f10        talend/dataprep-dataset:1.0.M0-SNAPSHOT   "java -jar /dataprep   5 seconds ago       Up 4 seconds        0.0.0.0:49158->8080/tcp   angry_heisenberg
578d7da8218a        talend/dataprep-dataset:1.0.M0-SNAPSHOT   "java -jar /dataprep   8 seconds ago       Up 7 seconds        0.0.0.0:49157->8080/tcp   thirsty_yonath
336c348ba4dd        talend/dataprep-dataset:1.0.M0-SNAPSHOT   "java -jar /dataprep   10 seconds ago      Up 9 seconds        0.0.0.0:49156->8080/tcp   insane_hopper
```
There are 3 instances of the same image accessible from http://localhost:49156, http://localhost:49157, http://localhost:49158.

### Docker environment properties

Docker image comes with the following environment properties:
* TDP_DATASET_SERVER_PORT: port for the API service (used for server.port).
* TDP_DATASET_METADATA_STORE: type of storage for metadata (mongodb, in-memory).
* TDP_DATASET_CONTENT_STORE: type of storage for the data sets (hdfs, local).
* TDP_DATASET_CONTENT_STORE_LOCAL_LOCATION: when TDP_DATASET_CONTENT_STORE = local, path to the local store directory.
* TDP_DATASET_MONGODB_HOST: when using TDP_DATASET_METADATA_STORE = mongodb, host name (or IP address) of the MongoDB instance.
* TDP_DATASET_MONGODB_PORT: when using TDP_DATASET_METADATA_STORE = mongodb, port of the MongoDB instance.
Note: value supports environment property placeholders, this means a environment property such as:
```
TDP_API_DATASET_SERVICE_URL: http://${DATASET_PORT_8080_TCP_ADDR}:${DATASET_PORT_8080_TCP_PORT}
```
...uses values from environment properties DATASET_PORT_8080_TCP_ADDR and DATASET_PORT_8080_TCP_PORT. Unresolved properties are kept as is.


## Documentation
REST service is self documented. Once started, go to http://localhost:8080 (modify 8080 if you choose a different port)
and then expand 'datasets' category. You can explore and even test the REST interface from this web page.

## License

Copyright (c) 2006-2015 Talend
