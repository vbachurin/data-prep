#!/bin/sh
docker stop $(docker ps -a -q) > /dev/null
docker rm $(docker ps -a -q) > /dev/null