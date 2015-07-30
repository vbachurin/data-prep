#! /bin/bash

# given a from version, pull all data-prep images with this version from talend-registry, tag them with version_to and push the new tags to registry.

version_from=$1
version_to=$2

if [ -z "$version_from"  ]; then
 echo "please specify a from version"
 exit 1
fi
if [ -z "$version_to"  ]; then
 echo "please specify a to version"
 exit 1
fi

if [[ "$3" != "--ni" ]]; then
 echo 'WARNING: you may erase images already pushed to the registry!'
 read -p "Are you sure (YES to continue)? " yn
 if [[ "$yn" != "YES" ]]; then
   exit 1
 fi
fi

images='talend/dataprep-api talend/dataprep-dataset talend/dataprep-transformation talend/dataprep-preparation talend/dataprep-webapp talend/dataprep-data'
registry='talend-registry:5000'

for image in $images;
do
 docker pull $registry/$image:$version_from
 docker tag --force $registry/$image:$version_from $registry/$image:$version_to
 docker push $registry/$image:$version_to
done

