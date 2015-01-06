# Talend Data Preparation - Data Set Service
![alt text](http://www.talend.com/sites/all/themes/talend_responsive/images/logo.png "Talend")

This folder contains the REST service to all Data Set related operations (creation, removal...).

## Prerequisites

You need Java *8* (or higher) and Maven 3.x (tested with 3.1.1 on Fedora).

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

## Data set content storage
Current implementation stores content to <i>${java.io.tmpdir}/talend/tdp/datasets/</i>.

## Documentation
REST service is self documented. Once started, go to http://localhost:8080 (modify 8080 if you choose a different port)
and then expand 'datasets' category. You can explore and even test the REST interface from this web page.

## License

Copyright (c) 2006-2015 Talend
