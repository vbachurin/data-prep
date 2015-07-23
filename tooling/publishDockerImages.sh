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
original_fig_file='../dataprep-platform/src/main/resources/fig_backend_data_web.yml'

# use to name produced files in code bellow
timestamp=`date +%Y%m%d%H%M%S`

tar_archive='dataprep-images_'$version'_'$timestamp'.tar'
final_fig_file='dataprep-images_'$version'_'$timestamp'.yml'

# tags locally produces docker images with registry name (in order to push them later)
docker_tag() {
  echo '==========================================='
  echo 'docker tag'
  echo '==========================================='
  for image in $images;
  do
    completeName=$image:$version
    docker tag --force $completeName $registry/$completeName
  done
  echo ' '
}

build_archive_images() {
  echo '==========================================='
  echo 'archive images'
  echo '==========================================='
  for image in $images;
  do
    list+=$registry/$image:$version
    list+=' '
  done
  list+=$registry'/talend/dataprep-data:'$version' mongo:latest'

  docker pull $registry'/talend/dataprep-data:'$version
  docker pull mongo:latest
  echo 'docker save to '$tar_archive
  time docker save --output=$tar_archive $list

  echo 'gzip tar'
  time gzip $tar_archive
  tar_archive=$tar_archive'.gz'
  md5sum $tar_archive > $tar_archive'.md5sum'
}

produce_compose_file() {
  # Add talend-registry to images
  from='image: talend/'
  to='image: talend-registry:5000/talend/'
  sed "s|$from|$to|g" $original_fig_file > $final_fig_file
}

# Current implementation is to move them in a folder on newbuild, where builds can be downloaded
publish_files() {
  mkdir $path_for_bins --parents
  mv $tar_archive $tar_archive.md5sum $final_fig_file $path_for_bins
}

push_docker_images() {
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
}

docker_tag
produce_compose_file
build_archive_images
publish_files
push_docker_images

