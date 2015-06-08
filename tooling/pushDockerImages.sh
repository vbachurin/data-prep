#! /bin/bash

# given a version, tag & push all data-prep LOCAL images with this version to talend-registry

version=$1

if [ -z "$version"  ]; then
  echo "please specify a version"
  exit 1
fi

if [[ "$2" != "--ni" ]]; then
  echo 'WARNING: you are going to erase images already pushed to the registry!'
  read -p "Are you sure (YES to continue)? " yn
  if [[ "$yn" != "YES" ]]; then
    exit 1
  fi
fi

images=`more docker_images.txt | grep images | cut --delimiter='=' --fields=2`
registry=`more docker_images.txt | grep registry | cut --delimiter='=' --fields=2`

for image in $images;
do
  completeName=$image:$version
  echo ========================================
  echo $completeName
  echo ========================================
  docker tag --force $completeName $registry/$completeName
  docker push $registry/$completeName
  echo ========================================
  echo
done

