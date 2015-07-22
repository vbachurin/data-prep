#! /bin/bash

# called after a build. Do the following:
# - tags images with talend-registry
# - saves images as a tar
# - gzip this tar
# - upload tar.gz to ftp
# - push images to talend-registry

cd `dirname $0`

version=$1

if [ -z "$version"  ]; then
  echo "please specify a version"
  exit 1
fi

images='talend/dataprep-api talend/dataprep-dataset talend/dataprep-transformation talend/dataprep-preparation talend/dataprep-webapp'
registry=talend-registry:5000
path_for_bins=/home/build-admin/Products/data-prep/

# old method, not used at this time
uploadToFtp () {
  echo '==========================================='
  echo 'FTP upload'
  echo '==========================================='
  FTP_HOST='ftp.talend.com'
  FTP_USER='dataprep'
  FTP_PASSWD='-_V 8bS){'

ftp -n $FTP_HOST <<END_SCRIPT
quote USER $FTP_USER
quote PASS $FTP_PASSWD
passive
cd dataprep/builds
put $tar_archive
put $tar_archive.md5sum
quit
END_SCRIPT
  echo '==========================================='
}

echo '==========================================='
echo 'docker tag'
echo '==========================================='
for image in $images;
do
  completeName=$image:$version
  echo 'docker tag --force '$completeName $registry/$completeName
  docker tag --force $completeName $registry/$completeName
done
echo '==========================================='


echo '==========================================='
echo 'archive images'
echo '==========================================='
for image in $images;
do
  list+=$registry/$image:$version
  list+=' '
done
list+=$registry'/talend/dataprep-data:'$version' mongo:latest'

timestamp=`date +%Y%m%d%H%M%S`
tar_archive='dataprep-images_'$version'_'$timestamp'.tar'
docker pull $registry'/talend/dataprep-data:'$version
docker pull mongo:latest
echo 'docker save to '$tar_archive
time docker save --output=$tar_archive $list

echo 'gzip tar'
time gzip $tar_archive
tar_archive=$tar_archive'.gz'
md5sum $tar_archive > $tar_archive'.md5sum'
echo '==========================================='

echo '==========================================='
echo 'Move archive to definitive place'
echo '==========================================='
mkdir $path_for_bins --parents
mv $tar_archive $tar_archive.md5sum $path_for_bins
echo '==========================================='

echo '==========================================='
echo 'docker push'
echo '==========================================='

for image in $images;
do
  completeName=$image:$version
  time docker push $registry/$completeName
  docker rmi $registry/$completeName
done
echo '==========================================='

