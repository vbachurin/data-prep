FROM eboraas/apache:latest

# copy the script that will init host:port of api rest service
COPY docker/init_docker.sh /init_docker.sh
RUN chmod +x init_docker.sh

# copy source of web-app to web server 
COPY dist /var/www/

# this script will init host:port of api service AND start the apache service
CMD /init_docker.sh

