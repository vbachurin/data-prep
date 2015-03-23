#! /bin/bash

version=$1

registry='talend-registry:5000'

if [ -z "$version"  ]; then
  echo "please specify a version"
  exit 1
fi

images='talend/dataprep-api talend/dataprep-dataset talend/dataprep-transformation talend/dataprep-webapp'

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

