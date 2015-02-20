
#![alt text](http://www.talend.com/sites/all/themes/talend_responsive/images/logo.png "Talend") Data Preparation Project  
## Build statuses
| _Branches_                  | _Status_ |
|:-------------------------|-------------|
| master                  | [![Build Status](https://magnum.travis-ci.com/Talend/data-prep.svg?token=pZH8ZcJXLuMxijJexq7J&branch=master)](https://magnum.travis-ci.com/Talend/data-prep) |

## Folders description
| _Project_                  | _Description_ |
|:-------------------------|-------------|
| [dataprep-common](dataprep-common)          | *everything (utilities) common to all projects but not related to dataprep business* |
| [dataprep-schema-analysis](dataprep-schema-analysis) | *stuff used to analyse (format, separator, schema, to-json) input data* |
| [dataprep-webapp](dataprep-webapp)          | *web app client and dev server (real server will in another project)* |
| [json_evaluation](json_evaluation)          | *evaluation of different json lib performance (jackson is identified as the faster). To remove?* |
| [recipe_processor](recipe_processor)         | *currently different tests of the Spark engine + other technos tests (SparkSql)* |
| [sample_builder](sample_builder)           | *Talend job builder (based on jobscript) [file/stream] -> [sampling/json file]* |


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
