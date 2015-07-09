#! /bin/bash

LOCATION='/etc/nginx/html/scripts'
# improve following to support ipv6
IP_ADDRESS_REGEX='[0-9\.]*'
PORT_REGEX='[0-9]*'

echo 'new values to set are:'
echo ' - api host=<'$TDP_API_HOST'>'
echo ' - api port=<'$TDP_API_PORT'>'

# this is the file to patch
JS_FILE=`ls "$LOCATION"/app*`
JS_FILE_BACKUP=$JS_FILE.orig
echo 'file to patch=<'$JS_FILE'> to <'$JS_FILE_BACKUP'>'

# replace default api service host:port by those taken from api container
cd $LOCATION
cp $JS_FILE    $JS_FILE_BACKUP
sed "s|['\"]apiUrl['\"],[ ]*['\"]http://"$IP_ADDRESS_REGEX":"$PORT_REGEX"['\"]|'apiUrl','http://"$TDP_API_HOST":"$TDP_API_PORT"'|g" $JS_FILE_BACKUP > $JS_FILE

echo 'before: '
sed 's/[.]constant/\n/g' $JS_FILE_BACKUP | grep '(.apiUrl'

echo 'after: '
sed 's/[.]constant/\n/g' $JS_FILE | grep '(.apiUrl'

# launch apache service (foreground to prevent command to finish, and container to stop)
nginx -g "daemon off;"

