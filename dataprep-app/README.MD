# Talend Data Preparation - Standalone application
![alt text](http://www.talend.com/sites/all/themes/talend_responsive/images/logo.png "Talend")

This folder contains all code and build script to combine all backend services as well as the web UI. It also automatically
starts a web browser to the web UI when ready.

## Prerequisites

You need Java *8* (or higher) and Maven 3.x (tested with 3.2.2 on Fedora 21 with OpenJDK 1.8.0_25-b18).
You need all data prep modules either built on your machine or in your local Maven repository (both backend and web ui).

## Build

Make sure all Data Prep modules are built on your machine. To ensure this, go to parent and build everything.

```
$ pwd
~/talend/data-prep/dataprep-app
$ cd ..
$ mvn install
```

Once you're sure all artifacts are available, you may package the standalone application:

```
$ pwd
~/talend/data-prep/dataprep-app
$ mvn package
```

The executable JAR is located in "target/" and named "dataprep-app-<version>.jar".

## Usage

You only need to run the executable JAR built by this module.

```
$ java -jar target/dataprep-app-1.0.m9-SNAPSHOT.jar 
16:18:39.579 [main] [] INFO  org.talend.dataprep.application.TalendDataPrepApplication - Starting Talend Data Preparation...
16:18:40.057 [main] [] INFO  org.talend.dataprep.application.TalendDataPrepApplication - Starting TalendDataPrepApplication v1.0.m9-SNAPSHOT on tlnd-fhuaulme with PID 3763 (/home/fhuaulme/dev/talend/data-prep/dataprep-app/target/dataprep-app-1.0.m9-SNAPSHOT.jar started by fhuaulme in /home/fhuaulme/dev/talend/data-prep/dataprep-app)
16:19:07.227 [main] [] INFO  org.talend.dataprep.application.TalendDataPrepApplication - Started TalendDataPrepApplication in 27.533 seconds (JVM running for 28.368)
16:19:07.228 [main] [] INFO  org.talend.dataprep.application.TalendDataPrepApplication - Talend Data Preparation started @ http://127.0.0.1:9090/ui/index.html.
```

Storage directories are located under "talend/" directory (folder created where you run "java -jar"). Logs are located in "talend/logs/app.log".

## License

Copyright (c) 2006-2015 Talend
