# Talend Data-prep web-application aka Talend Data Shaker
## How to run
### Prerequisites
1. nodejs : http://nodejs.org/
2. npm : it should be install with the above node
3. install development tools using this command :
 
`npm install -g bower gulp`

(You may have to use sudo before the command)

4. git clone git@github.com:Talend/data-prep.git in _checkouts_ (adapt others commands if clone in another folder)
5. go to _checkouts/data-prep/dataprep-webapp_ and type the following command

`npm install`

This will install all the dev package for dataprep as well as third party librairies required by this application.

### Run
when in folder _checkouts/data-prep/dataprep-webapp_ type the command

`gulp serve`

This will precompile jade and scss templates, update the index.html with all the css, and javascript available. 
Then it will start a web server usually on port 3000 and watch any code change to refresh the browser. 

### Build a standalone dev distrib

run
`gulp build:dev`

### Build a standalone prod distrib
WARNING : this is not working right now.
run
`gulp build`

### Build a war
in _checkouts/data-prep/dataprep-webapp_ call maven build

`mvn clean package`

### Deploy
#### One shot
1. Copy _target/dataprep-webapp-1.0.m0-SNAPSHOT.war_ in _tomcat_path/webapps as dataprep-webapp.war_
2. Restart Tomcat (or reload only this application)

### Perennial (linux)
Create a symbolic link in _tomcat/webapps_ to _target/dataprep-webapp-1.0.m0-SNAPSHOT.war_

`cd tomcat/webapps`

`ln -s checkouts/data-prep/dataprep-webapp/target/dataprep-webapp-1.0.m0-SNAPSHOT.war dataprep-webapp.war`

### Access
Open in your favorite browser [http://localhost:8080/dataprep-webapp](http://localhost:8080/dataprep-webapp)
