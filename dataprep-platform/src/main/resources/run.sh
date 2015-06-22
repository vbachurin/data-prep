#! /bin/bash

fig_file=$1
log_file=$2

echo 'Start dockers for data-prep'
echo ' -> fig file='$fig_file

fig -p tdp -f $fig_file up --allow-insecure-ssl 

