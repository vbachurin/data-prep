#! /bin/bash

status=`git status --short --untracked-files=no`
if [ -n "$status"  ]; then
  echo "git status non vide, dégage !!"
  exit 1
fi

echo 'Release to prepare '$1', message '$2
read -p "Proceed (NO to exit)? " yn
if [[ "$yn" = "NO" ]]; then
  exit 1
fi

execute () {
  echo "Next step is <$1>"
  read -p "Proceed (NO to exit)? " yn
  if [[ "$yn" = "NO" ]]; then
    exit 1
  fi
  echo ========================================================================
  echo '           Processing command <'$1'>'
  echo ========================================================================
  $1
  $2
  echo ========================================================================
  echo
  echo
  echo
}

execute 'git checkout release/'$1
execute 'git pull --rebase origin release/'$1

execute 'git branch -d release/'$1
echo "Release '$1' : $2" > /tmp/commit_msg
execute 'git tag -a '$1' --file=/tmp/commit_msg'
execute 'git push --tags'

# here publish docker images to registry
./tagAndPushDockerImages.sh $1 latest
