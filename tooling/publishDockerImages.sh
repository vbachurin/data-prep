#! /bin/bash

cd `dirname $0`

version=$1

if [ -z "$version"  ]; then
  echo "please specify a version"
  exit 1
fi

registry='talend-registry:5000'

# folder (on local machine) where to put bins at the end (where builds can be downloaded)
path_for_bins=/home/build-admin/Products/data-prep/

# the fig file, that will be publish with the docker images
original_fig_file='../dataprep-platform/src/main/resources/fig_backend_data_web.yml'

# external images pattern:
external_images_pattern='mongo|data:'

# use to name produced files in code bellow
timestamp=`date +%Y%m%d%H%M%S`

tar_archive='dataprep-images_'$version'_'$timestamp'.tar'
final_fig_file_mongo='dataprep-images_'$version'_'$timestamp'-mongo.yml'
final_fig_file_filesystem='dataprep-images_'$version'_'$timestamp'-filesystem.yml'

# tags locally produced docker images with registry name (in order to push them later)
docker_tag() {
  echo '==========================================='
  echo 'docker tag'
  echo '==========================================='
  images_to_tag=`more $original_fig_file | grep image | cut --delimiter=':' --fields=2- | grep -v -E $external_images_pattern`

  for image in $images_to_tag;
  do
    echo 'docker tag --force '$image' '$registry/$image
    docker tag --force $image $registry/$image
  done

  echo ' '
}

# based on the modified docker-compose file, computes 3 lists:
computes_docker_images_lists() {
  verbose=$1

  # list of images that are not built on this server, and then should be pulled before added to tar:
  external_list=`less $final_fig_file_mongo | grep image | cut --delimiter=':' --fields=2- | grep -E $external_images_pattern`

  # list of images that are built on this server:
  internal_list=`less $final_fig_file_mongo | grep image | cut --delimiter=':' --fields=2- | grep -v -E $external_images_pattern`

  # all images:
  list=$internal_list' '$external_list

  if [[ "$verbose" = "true" ]]; then
    echo 'external images: '$external_list
    echo 'internal images: '$internal_list
    echo 'all images: '$list
  fi
}

# explicitely pulls images produced externally, to be sure to put the last version in the archive:
pull_external() {
  for image in $external_list;
  do

    docker pull $image
  done
}

build_archive_images() {
  echo '==========================================='
  echo 'archive images'
  echo '==========================================='

  echo 'docker save to '$tar_archive
  time docker save --output=$tar_archive $list

  echo 'gzip tar'
  time gzip $tar_archive
  tar_archive=$tar_archive'.gz'
  md5sum $tar_archive > $tar_archive'.md5sum'
}

# produce the .yml file with mongo storage
produce_compose_file_on_mongo() {
  # Add talend-registry to images
  from='image: talend/'
  to='image: '$registry'/talend/'
  sed "s|$from|$to|g" $original_fig_file > $final_fig_file_mongo
}

# produce the .yml file with file system storage
produce_compose_file_on_filesystem() {
  # update TDP*STORE from mongodb to file
  from_1='(TDP_.*_STORE: )mongodb'
  to_1='\1file'

  sed -E "s|$from_1|$to_1|g" $final_fig_file_mongo > $final_fig_file_filesystem
}


# Current implementation is to move them in a folder on newbuild, where builds can be downloaded
publish_files() {
  mkdir $path_for_bins --parents
  mv $tar_archive $tar_archive.md5sum $final_fig_file_mongo $final_fig_file_filesystem $path_for_bins
}

push_docker_images() {
  echo '==========================================='
  echo 'docker push'
  echo '==========================================='
  images_to_push=`more $original_fig_file | grep image | cut --delimiter=':' --fields=2- | grep -v -E $external_images_pattern`
  for image in $images_to_push;
  do
    time docker push $registry/$image
    docker rmi $registry/$image $image
  done
  echo '==========================================='
}

produce_compose_file_on_mongo
produce_compose_file_on_filesystem
computes_docker_images_lists true
docker_tag
pull_external
build_archive_images
publish_files
push_docker_images


