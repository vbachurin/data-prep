#! /bin/bash

# given a version, tag & push all data-prep LOCAL images with this version to talend-registry

version=$1

if [ -z "$version"  ]; then
  echo "please specify a version"
  exit 1
fi

images=`more docker_images.txt | grep images | cut --delimiter='=' --fields=2`
registry=`more docker_images.txt | grep registry | cut --delimiter='=' --fields=2`
list=''
for image in $images;
do
  list+=$registry/$image:$version
  list+=' '
done
list+='mongo:latest'
docker save --output=/tmp/dataprep-images_$version.tar $list
