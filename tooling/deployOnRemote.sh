#! /bin/bash

#======================================================================================
#  Launch entire docker stack on a remote server
#======================================================================================

version=$1

path='/home/talend/deployedByJenkins'
server_host='10.42.10.99'

# improve following to support ipv6
IP_ADDRESS_REGEX='[0-9\.]*'

# Creates our folder on remote:
ssh talend@$server_host mkdir $path --parent

# Change TDP_API_HOST on remote with host IP:
sed "s|TDP_API_HOST: "$IP_ADDRESS_REGEX"|TDP_API_HOST: "$server_host"|g" ../dataprep-platform/src/main/resources/fig_backend_data_web.yml > /tmp/fig_custom.yml

# Copy custom fig file to remote:
scp /tmp/fig_custom.yml talend@$server_host:$path

ssh talend@$server_host fig -f $path'/fig_custom.yml' pull --allow-insecure-ssl
ssh talend@$server_host 'bash -s' < ../dataprep-platform/src/main/resources/clean.sh
ssh talend@$server_host 'bash -s' < ../dataprep-platform/src/main/resources/run.sh $path'/fig_custom.yml'

