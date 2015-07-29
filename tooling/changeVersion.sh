#! /bin/bash

VERSION_FROM=$1
VERSION_TO=$2

cd ..

echo 'Change version from '$VERSION_FROM' to '$VERSION_TO
read -p "Proceed (NO to exit)? " yn
if [[ "$yn" = "NO" ]]; then
  exit 1
fi

mvn versions:set -DnewVersion=$VERSION_TO -DgenerateBackupPoms=false
cd dataprep-backend
mvn versions:set -DnewVersion=$VERSION_TO -DgenerateBackupPoms=false
cd ../dataprep-webapp
mvn versions:set -DnewVersion=$VERSION_TO -DgenerateBackupPoms=false
cd ..


changeVersionInFile () {
  sed s/$VERSION_FROM/$VERSION_TO/g $1 > /tmp/tmp_file
  cp /tmp/tmp_file $1
}

changeVersionInFile dataprep-webapp/src/components/navbar/navbar.html
changeVersionInFile dataprep-platform/src/main/resources/fig_backend.yml
changeVersionInFile dataprep-platform/src/main/resources/fig_backend_data.yml
changeVersionInFile dataprep-platform/src/main/resources/fig_backend_data_web.yml

