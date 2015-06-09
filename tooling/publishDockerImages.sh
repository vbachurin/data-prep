#! /bin/bash

# given a version, tag all of data-prep images of this version, already publish to talend-registry with 'latest' and push them to the registry

version=$1

if [ -z "$version"  ]; then
  echo "please specify a version"
  exit 1
fi

images=`more docker_images.txt | grep images | cut --delimiter='=' --fields=2`
registry=`more docker_images.txt | grep registry | cut --delimiter='=' --fields=2`

for image in $images;
do
  echo ========================================
  echo $image
  echo ========================================
  docker tag --force $registry/$image:$version $registry/$image:latest
  docker push $registry/$image:latest
  echo ========================================
  echo
done

