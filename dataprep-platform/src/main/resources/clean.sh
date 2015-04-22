#!/bin/bash

DOCKER_IDS=`docker ps --all --filter name=tdp --quiet`

if [[ -z "$DOCKER_IDS" ]]; then
  echo 'no docker containers to stop'
else
  echo 'docker containers to stop: '$DOCKER_IDS
  docker stop $DOCKER_IDS > /dev/null
  docker rm --volumes=true $DOCKER_IDS > /dev/null
fi

