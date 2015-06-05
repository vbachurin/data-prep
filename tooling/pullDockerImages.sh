#! /bin/bash

# given a version, pull all data-prep images with this version from talend-registry

version=$1

if [ -z "$version"  ]; then
  echo "please specify a version"
  exit 1
fi

images=`more docker_images.txt | grep images | cut --delimiter='=' --fields=2`
registry=`more docker_images.txt | grep registry | cut --delimiter='=' --fields=2`

for image in $images;
do
  completeName=$image:$version
  echo ========================================
  echo $completeName
  echo ========================================
  docker pull $registry/$completeName
  echo ========================================
  echo
done

