ORIGINAL_HOST=10.42.10.99
ORIGINAL_PORT=8081

# this is the file to patch
JS_FILE=`ls /var/www/scripts/app*`

JS_FILE_BACKUP=$JS_FILE.orig

# replace default api service host:port by those taken from api container
cd /var/www/scripts
cp $JS_FILE $JS_FILE_BACKUP && sed s/$ORIGINAL_HOST:$ORIGINAL_PORT/$API_PORT_8888_TCP_ADDR:$API_PORT_8888_TCP_PORT/g $JS_FILE_BACKUP > $JS_FILE

# launch apache service (foreground to prevent command to finish, and container to stop)
/usr/sbin/apache2ctl -DFOREGROUND

