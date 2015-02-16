FROM eboraas/apache:latest

# copy source of web-app to web server 
COPY dist /var/www/

# copy the scrit that will init ip & port to api rest service
COPY init_docker.sh /init_docker.sh
RUN chmod +x init_docker.sh

# this script will init host:port from service AND start the apache service
CMD /init_docker.sh

