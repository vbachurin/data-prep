#! /bin/bash

LOCATION='/usr/share/nginx/html'

# improve following to support ipv6
IP_ADDRESS_REGEX='[0-9\.]*'
PORT_REGEX='[0-9]*'

echo 'new values to set are:'
echo ' - api host=<'$TDP_API_HOST'>'
echo ' - api port=<'$TDP_API_PORT'>'

# this is the file to patch
JS_FILE=`ls "$LOCATION"/scripts/app*`
JS_FILE_BACKUP=$JS_FILE.orig
echo 'file to patch=<'$JS_FILE'>'

# replace default api service host:port by those taken from api container
cd $LOCATION/scripts
cp $JS_FILE $JS_FILE_BACKUP
sed 's|"apiUrl","http://'$IP_ADDRESS_REGEX':'$PORT_REGEX'"|"apiUrl","http://'$TDP_API_HOST':'$TDP_API_PORT'"|g' $JS_FILE_BACKUP > $JS_FILE

# launch apache service (foreground to prevent command to finish, and container to stop)
nginx -g "daemon off;"

