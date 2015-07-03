#! /bin/bash

status=`git status --short --untracked-files=no`
if [ -n "$status"  ]; then
  echo "git status non vide, dégage !!"
  exit 1
fi

echo 'Release to prepare '$1', next release '$2
read -p "Proceed (NO to exit)? " yn
if [[ "$yn" = "NO" ]]; then
  exit 1
fi

execute () {
  echo "Next step is <$1>"
  read -p "Proceed (EXIT/SKIP)? " yn
  if [[ "$yn" = "EXIT" ]]; then
    exit 1
  fi
  if [[ "$yn" = "SKIP" ]]; then
    return
  fi
  echo ========================================================================
  echo '           Processing command <'$1'>'
  echo ========================================================================
  $1
  echo ========================================================================
  echo
  echo
  echo
}

git checkout master
git pull --rebase

execute 'git branch release/'$1
./changeVersion.sh $1'-SNAPSHOT' $2'-SNAPSHOT'
echo "GIT admin: update pom.xml's version to "$2"-SNAPSHOT on master" > /tmp/commit_msg
execute 'git commit --all --file=/tmp/commit_msg'

execute 'git checkout release/'$1
./changeVersion.sh $1'-SNAPSHOT' $1
echo "GIT admin: update pom.xml's version to "$1" on release/"$1 > /tmp/commit_msg
execute 'git commit --all --file=/tmp/commit_msg'

execute 'git checkout master'
execute 'git merge -s ours release/'$1

