#! /bin/bash

CONFIG_LOCATION='/etc/nginx/html/assets/config'
# improve following to support ipv6
IP_ADDRESS_REGEX='[0-9\.]*'
PORT_REGEX='[0-9]*'

echo 'new values to set are:'
echo ' - api host=<'$TDP_API_HOST'>'
echo ' - api port=<'$TDP_API_PORT'>'

# this is the file to patch
CONFIG_FILE=`ls "$CONFIG_LOCATION"/config.json`
CONFIG_FILE_BACKUP=$CONFIG_FILE.orig
echo 'file to patch=<'$CONFIG_FILE'> to <'$CONFIG_FILE_BACKUP'>'

# replace default api service host:port by those taken from api container
cd $CONFIG_LOCATION
cp $CONFIG_FILE    $CONFIG_FILE_BACKUP
sed "s|[\"]serverUrl[\"]:[ ]*[\"]http://"$IP_ADDRESS_REGEX":"$PORT_REGEX"['\"]|\"serverUrl\": \"http://"$TDP_API_HOST":"$TDP_API_PORT"\"|g" $CONFIG_FILE_BACKUP > $CONFIG_FILE

echo 'before: '
cat $CONFIG_FILE_BACKUP

echo 'after: '
cat $CONFIG_FILE

# launch apache service (foreground to prevent command to finish, and container to stop)
nginx -g "daemon off;"

