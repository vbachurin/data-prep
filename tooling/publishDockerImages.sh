#! /bin/bash

# given a version, tag all of data-prep images of this version, already publish to talend-registry with 'latest' and push them to the registry

version=$1

registry='talend-registry:5000'

if [ -z "$version"  ]; then
  echo "please specify a version"
  exit 1
fi

images='talend/dataprep-api talend/dataprep-dataset talend/dataprep-transformation talend/dataprep-preparation talend/dataprep-webapp talend/dataprep-data'

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

