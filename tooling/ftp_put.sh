#!/bin/sh

SOURCE=$1
TARGET=$2
HOST='ftp.talend.com'
USER='dataprep'
PASSWD='-_V 8bS){'

ftp -n $HOST <<END_SCRIPT
quote USER $USER
quote PASS $PASSWD
passive
cd $TARGET
put $SOURCE
quit
END_SCRIPT

