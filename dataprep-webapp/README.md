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

### root file structure
The file structure is inspired by yeoman project called generator-gulp-angular : https://github.com/Swiip/generator-gulp-angular

<pre>
.
├── README.md                               <- this file.
├── bower.json                              <- javascript app external dependencies.
├── bower.json_latest_angular_no_boostrap   <- js dependencies without bootstrap and using the latest angular (no used today).
├── bower_components                        <- untracked generated folder where external javascript dependencies are located.
├── coverage                                <- untracked generated folder where karma istanbul plugin will put coverage files.
├── dev                                     <- untracked generated folder where you find an autonomous deployment of the app dev.
├── docker.base.dev.img                     <- dockefile generated to create a base image with npm, bower, git.
├── gulp                                    <- folder that contains all build files.
├── gulpfile.js                             <- gulp initial file.
├── node                                    <- untracked generated folder created when using maven.
├── node_modules                            <- untracked generated folder for gulp build node modules.
├── karma.conf.js                           <- unit tests configuration.
├── package.json                            <- npm description for build tools dependencies.
├── pom.xml                                 <- maven description file.
├── src                                     <- sources.
└── target                                  <- untracked generated folder for maven build.
</pre>


### Sources file structure
The source file structure is based on the [Best Practice Recommendations for Angular App Structure](https://docs.google.com/document/d/1XXMvReO8-Awi1EZXAXS4PzDzdNvV6pGcuaF4Q9821Es/pub), except for css files that have their own structure

<pre>
.
├── app                                         <- app source folder
│   ├── home                                    <- home page sources
│       ├── home.html                           <- home page
│       ├── home-controller.js                  <- home page controller
│       ├── home-controller-spec.js             <- home page controller unit tests
│       ├── home-subheader.html                 <- home page subheader included in home.html
│   ├── ...                                     <- other page folder
│
├── assets                                      <- assets source folder
│   ├── fonts                                   <- fonts folder
│   └── images                                  <- images folder
│
├── components                                  <- reusable components folder
│   ├── dataset                                 <- dataset module folder
│       ├── dataset-grid                        <- dataset module folder
│           ├── ...
│           ├── dataset-grid-directive.html     <- data grid directive template
│           ├── dataset-grid-directive.js       <- data grid directive
│           └── dataset-grid-directive.spec.js  <- data grid directive unit tests
│       ├── ...
│       ├── dataset-module.js                   <- dataset angular module
│       ├── dataset-service.js                  <- dataset angular service registered in dataset module
│       └── dataset-service.spec.js             <- dataset service unit tests
│
│   ├── utils                                   <- filters, constants, ...
│   ├── ...                                     <- other component folder
│
├── css                                         <- style folder
│   ├── base                                    <- base styles (reset, typography, ...)
│       ├── _base.scss                          <- file that only imports all base styles
│       └── ...                                 <- other base styles, imported in _base.scss
│   ├── components                              <- components styles (Buttons, Carousel, Cover, Dropdown, ...)
│       ├── _components.scss                    <- file that only imports all components styles
│       └── ...                                 <- other components styles, imported in _components.scss
│   ├── layout                                  <- layout styles (Navigation, Grid system, Header, Footer, Sidebar, Forms, ...)
│       ├── _layout.scss                        <- file that only imports all layout styles
│       └── ...                                 <- other layout styles, imported in _layout.scss
│   ├── pages                                   <- pages specific styles (home page, ...)
│       ├── _pages.scss                         <- file that only imports all pages styles
│       └── ...                                 <- other pages styles, imported in _pages.scss
│   ├── utils                                   <- utils styles (Mixins, Colors, ...)
│       ├── _utils.scss                         <- file that only imports all utils styles
│       └── ...                                 <- other utils styles, imported in _utils.scss
│   ├── vendors                                 <- vendors styles (third party frameworks)
│       ├── _vendors.scss                       <- file that only imports all vendors styles
│       └── ...                                 <- other vendors styles, imported in _vendors.scss
│   └── main.scss                               <- main scss that only imports _base.scss, _components.scss, _layout.scss, _pages.scss, _utils.scss, _vendors.scss
│
├── app.js                                      <- main module
├── app.spec.js                                 <- main module config unit tests
└── index.html                                  <- main page
</pre>

### build structure
All the structure is inspired by yeoman project called [generator-gulp-angular](https://github.com/Swiip/generator-gulp-angular).
The build of the web app is required because we use pre-compiler tools in order to use Jade and scss templating.
use `gulp --tasks-simple` to find out which build tasks are available.

### Run
when in folder _checkouts/data-prep/dataprep-webapp_ type the command

`gulp serve`

This will precompile jade and scss templates, update the index.html with all the css, and javascript available. 
Then it will start a web server usually on port 3000 and watch any code change to refresh the browser. 

### Run tests
when in folder _checkouts/data-prep/dataprep-webapp_ type the command

`gulp test`

This will get all the vendors (bower_components) and source files, include them in karma config and run all the unit tests only once.
To run it continuously with source watch, type the command

`gulp test:auto`

### Test coverage
During each test run, Karma will generate coverage files, using [karma-coverage plugin](https://github.com/karma-runner/karma-coverage).
Open the index.html in the coverage folder to display coverage details for each js file.

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
