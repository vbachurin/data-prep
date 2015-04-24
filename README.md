
#![alt text](http://www.talend.com/sites/all/themes/talend_responsive/images/logo.png "Talend") Data Preparation Project  

## Folders description
| _Project_                                          | _Description_                                                        |
|:---------------------------------------------------|----------------------------------------------------------------------|
| [dataprep-api](dataprep-api)                       | *rest service on top of transformation & dataset, used by webapp*    |
| [dataprep-backend-common](dataprep-backend-common) | *set of common objects shared by multiple services*                  |
| [dataprep-backend](dataprep-backend)               | *parent project for all backend projects*                            |
| [dataprep-dataset](dataprep-dataset)               | *rest service to manage datasets (import, export, sample, stats)*    |
| [dataprep-metrics](dataprep-metrics)               | *library to monitor rest services*                                   |
| [dataprep-platform-tests](dataprep-platform_tests) | *Gatling config for stress testing on rest services*                 |
| [dataprep-platform](dataprep-platform)             | *fig files*                                                          |
| [dataprep-preparation](dataprep-preparation)       | *rest service to manage preparations (create, list steps, get data)* |
| [dataprep-transformation](dataprep-transformation) | *rest service to apply transformations (actions are here)*           |
| [dataprep-webapp](dataprep-webapp)                 | *web app (AngularJS)*                                                |
| [tooling](tooling)                                 | *IDE specific config files + some other stuff*                       |

### Back-end

#### Services
The following projects provides back-end services exposed as REST API.

| _Project_                  | _Description_ |
|:-------------------------|-------------|
| [dataprep-api](dataprep-api)         | *Back-end API service for Data Preparation Web UI.* |
| [dataprep-transformation](dataprep-transformation)         | *Back-end service for data set transformation.* |
| [dataprep-dataset](dataprep-dataset)         | *Back-end service for data set operations (create, update, content index, get data set metadata...)* |


#### Common and module management
The following projects don't build any back-end artifacts but provide build utilities.

| _Project_                  | _Description_ |
|:-------------------------|-------------|
| [dataprep-backend](dataprep-backend)          | *Common POM for all back-end modules (defines all libraries & versions, as well as common build behaviors)* |
| [dataprep-backend-common](dataprep-backend-common)         | *Common resources and classes for back-end modules (CORS Filter, Branded Swagger UI...)* |
| [dataprep-metrics](dataprep-metrics)         | *Spring AOP based annotations to monitor method execution times and volume per user* |

#### Deployment and tests
The following projects provide scripts to automate platform deployment as well as tests to ensure platform health.

| _Project_                  | _Description_ |
|:-------------------------|-------------|
| [dataprep-platform](dataprep-platform)         | *Scripts for starting a back-end platform (based on Fig & Docker)* |
| [dataprep-platform-tests](dataprep-platform-tests)         | *Stress tests and reporting for back-end (based on Gatling)* |

## Build
- All project are maven based.
- A parent in pom build the web-app and its dependencies.
- See [dataprep-dataset](/dataprep-dataset/) for specific build instructions

## IDE setup
see the [tooling](/tooling/) folder.