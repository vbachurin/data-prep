# data-prep

| _Project_                  | _Description_ |
|:-------------------------|-------------|
| dataprep-common          | *everything (utilities) common to all projects but not related to dataprep business* |
| dataprep-schema-analysis | *stuff used to analyse (format, separator, schema, to-json) input data* |
| dataprep-webapp          | *web app client and dev server (real server will in another project)* |
| json_evaluation          | *evaluation of different json lib performance (jackson is identified as the faster). To remove?* |
| recipe_processor         | *currently different tests of the Spark engine + other technos tests (SparkSql)* |
| sample_builder           | *Talend job builder (based on jobscript) [file/stream] -> [sampling/json file]* |

## build
- All project are maven based.
- A parent in pom build the web-app and its dependencies.
- Some special actions are required for grefine dependencies: *TODO describe these steps here*
