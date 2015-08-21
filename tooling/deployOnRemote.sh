#! /bin/bash

#======================================================================================
#  Launch entire docker stack on a remote server
#======================================================================================

path='/home/talend/deployedByJenkins'
server_host=$1
registry='talend-registry:5000'

# TODO: improve following to support ipv6
IP_ADDRESS_REGEX='[0-9\.]*'

# Creates our folder on remote:
ssh talend@$server_host mkdir $path --parent

# Change TDP_API_HOST in fig file with host IP:
sed "s|TDP_API_HOST: "$IP_ADDRESS_REGEX"|TDP_API_HOST: "$server_host"|g" ../dataprep-platform/src/main/resources/fig_backend_data_web.yml > /tmp/fig_custom.yml

# Add registry prefix for our images:
cp /tmp/fig_custom.yml /tmp/fig_custom_1.yml
from='image: talend/'
to='image: '$registry'/talend/'
sed "s|$from|$to|g" /tmp/fig_custom_1.yml > /tmp/fig_custom.yml

# Copy custom fig file to remote:
scp /tmp/fig_custom.yml talend@$server_host:$path

ssh talend@$server_host fig -f $path'/fig_custom.yml' pull --allow-insecure-ssl
ssh talend@$server_host 'bash -s' < ../dataprep-platform/src/main/resources/clean.sh
ssh talend@$server_host 'fig -p tdp -f '$path'/fig_custom.yml up --allow-insecure-ssl > '$path'/fig.log &'

