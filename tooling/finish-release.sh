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

# first, get last changes from release branch:
execute 'git checkout release/'$1
execute 'git pull --rebase origin release/'$1

# second, get last changes from master:
execute 'git checkout master'
execute 'git pull --rebase'

# retrieve all bug fixes did on release branch onto master:
execute 'git merge release/'$1

# change version on release branch (removing SNAPSHOT. for example: from 1.0.m8-SNAPSHOT to 1.0.m8):
execute 'git checkout release/'$1
./changeVersion.sh $1'-SNAPSHOT' $1
echo "GIT admin: update pom.xml's version to "$1" on release/"$1 > /tmp/commit_msg
execute 'git commit --all --file=/tmp/commit_msg'

# put release tag:
echo "Release '$1' : $2" > /tmp/commit_msg
execute 'git tag -a '$1' --file=/tmp/commit_msg'
execute 'git push --tags'

#delete release branch?
git chechout master
execute 'git branch -D release/'$1
execute 'git push origin :release/'$1

