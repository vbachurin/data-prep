#! /bin/bash
version=$1

path='/home/talend/demo4jenkins'
server_host='10.42.10.99'

scp ../dataprep-platform/src/main/resources/fig_backend_data_web.yml talend@$server_host:$path
ssh talend@$server_host 'bash -s ' < customizeComposeFile.sh $path'/fig_backend_data_web.yml' $path'/fig_generated.yml'
ssh talend@$server_host fig -f $path'/fig_generated.yml' pull --allow-insecure-ssl
ssh talend@$server_host 'bash -s' < ../dataprep-platform/src/main/resources/clean.sh
ssh talend@$server_host 'bash -s' < ../dataprep-platform/src/main/resources/run.sh $path'/fig_generated.yml'

