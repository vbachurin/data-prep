# Talend Data Preparation - Platform scripts
![alt text](http://www.talend.com/sites/all/themes/talend_responsive/images/logo.png "Talend")

This folder contains scripts to start a Data Preparation platform (all needed services such as DataSet service, MongoDB and load balancers).
It also provides ways to scale up or down the number of service instances.

## Prerequisites

You need Maven 3.x (tested with 3.2.2 on Fedora 21) to build the project. You also need:
* Fig (1.0.0)
* Docker (1.4.1)

Please add the following line to /etc/hosts:
```
127.0.0.1	dataset.tdp.talend.lan
```
This line allows you to redirect "dataset.tdp.talend.lan" to the load balancer Fig script will create.

### Fedora prerequisites
To install dependencies, you may use 'yum':
```
$ sudo yum install docker-io fig
```
*Warning*
SELinux prevents access to host directory. You may either disable SELinux or look for SELinux audit log and allow operations.

## Usage
To boot all docker containers, simply go to src/main/resources/ and run:
```
$ fig -p tdp up
Recreating tdp_mongodb_1...
Recreating tdp_nginx_1...
Recreating tdp_dataset_1...
Attaching to tdp_mongodb_1, tdp_nginx_1, tdp_dataset_1
nginx_1   | forego     | starting nginx.1 on port 5000
nginx_1   | forego     | starting dockergen.1 on port 5100
nginx_1   | dockergen.1 | 2015/01/25 13:58:15 Generated '/etc/nginx/conf.d/default.conf' from 2 containers
...

```
Then go to "http://dataset.tdp.talend.lan" using your web browser. The dataset service documentation should show up.

### Scaling up and down the number dataset service instances
Stay in src/main/resources/ and run:
```
$ fig scale dataset=2
```
This starts a new dataset service and automatically adds it to the load balancer. The next time someone accesses http://dataset.tdp.talend.lan
one of the 2 dataset service will be used.
Use the same command to scale up and down the number of instances you want.

## License

Copyright (c) 2006-2015 Talend
