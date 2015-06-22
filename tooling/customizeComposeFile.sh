#! /bin/bash
# this script takes a fig file, and replace FROM version with TO version

#version_from='#VERSION#'
#version_to=$3
#host_from='#HOST#'
#host_to=$4

fig_source=$1
fig_target=$2

#echo 'version_to='$version_to
#echo 'host_to='$host_to
echo 'fig_source='$fig_source
echo 'fig_target='$fig_target

#===========================================
# Add talend-registry to images
#===========================================
from='image: talend/'
to='image: talend-registry:5000/talend/'
sed "s|$from|$to|g" $fig_source > $fig_target
#===========================================


#sed s/$version_from/$version_to/g $fig_source > $fig_target'_1'
#sed s/$host_from/$host_to/g $fig_target'_1' > $fig_target

