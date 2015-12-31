# Protractor Mock
A NodeJS module to be used alongside [Protractor](https://github.com/angular/protractor) to facilitate setting up mocks for HTTP calls for the AngularJS applications under test. 

This allows the developer to isolate the UI and client-side application code in our tests without any dependencies on an API.

**This plugin does not depend on Angular Mocks (ngMockE2E) being loaded into your app; therefore, there is no need to modify anything within your current Angular web application.**

## Installation
	npm install protractor-http-mock --save-dev
## Configuration
In your protractor configuration file, we can set the following options:

### Mocks
We can set a collection of default mocks to load for every test, and the name of the folder where your mocks will reside. More on this later.

  	mocks: {
    	default: ['mock-login'], // default value: []
    	dir: 'my-mocks' // default value: 'mocks'
  	},

### Directories and file names
We can also configure our root directory where the mocks and protractor configuration will be located; as well as, the name of the protractor configuration file.

  	onPrepare: function(){
    	require('protractor-http-mock').config = {
			rootDirectory: __dirname, // default value: process.cwd()
			protractorConfig: 'my-protractor-config.conf' // default value: 'protractor.conf'
    	};
  	}

## Usage
Mocks are defined as JSON objects describing the request and response for a particular HTTP call:

  	  {
		request: {
	      path: '/users/1',
	      method: 'GET'
	    },
	    response: {
	      data: {
	        userName: 'pro-mock',
	        email: 'pro-mock@email.com'
	      }
	    }
	  }

And then set the mock at the beginning of your test before your application loads:

	var mock = require('protractor-http-mock');
	...

	  mock([{
	    request: {
	      path: '/users/1',
	      method: 'GET'
	    },
	    response: {
	      data: {
	        userName: 'pro-mock',
	        email: 'pro-mock@email.com'
	      }
	    }
	  }]);

Make sure to clean up after test execution. This should be typically done in the `afterEach` call to ensure the teardown is executed regardless of what happens in the test execution:

	afterEach(function(){
	  mock.teardown();
	});
	
Please note that the `mock()` function needs to be called before the browser opens. If you have different mock data for different tests, please make sure that, either the tests always start in a new browser window, or that its possible to setup all the mocks for each test case before any of tests start running.

### Mock files
Mocks can also be loaded from physical files located in the `mocks.dir` directory that we defined in our configuration: 

  	tests
	    e2e
	      protractor.conf.js
	      mocks
	        users.js
	      specs
	        ...


You could simply load your mocks as follows:

	mock(['users']);

Files are structured as standard node modules. The strings passed are the path of the file relative to the mocks directory - the same as if you would be doing a standard `require()` call.

	module.exports = { ... }; // for a single mock.

or

	module.exports = [ ... ]; // for multiple mocks.


### Schema
The full GET schema for defining your mocks is as follows:

	  request: {
	    path: '/products/1/items',
	    method: 'GET',
	    params: { // These match params as they would be passed to the $http service. This is an optional field.
	      page: 2,
	      status: 'onsale'
	    },
	    queryString: { // These match query string parameters that are part of the URL as passed in to the $http service. This is an optional field.
	      param1: 'My first qs parameters',
	      param2: 'Second parameter'
	    },
	    headers: { //These match headers as the end result of the expression provided to the $http method.
	    	head1: 'val1',
	    	head2: 'val2'
	    }
	  },
	  response: {
	  	data: {}, // This is the return value for the matched request
	    status: 500 // The HTTP status code for the mocked response. This is an optional field.
	  }

A full mock for a POST call takes the following options:

	  request: {
	    path: '/products/1/items',
	    method: 'POST',
	    data: { // These match POST data. This is an optional field.
	      status: 'onsale',
	      title: 'Blue Jeans',
          price: 24.99
	    }
	  },
	  response: {
	    data: { // This is the return value for the matched request
	      status: 'onsale',
	      title: 'Blue Jeans',
          id: 'abc123',
          price: 24.99
        },
	    status: 204 // The HTTP status code for the mocked response. This is an optional field.
	  }

PUT, DELETE, HEAD, PATCH, and JSONP methods are also supported. Please see the examples in the source code for more information.

#### Request
Defining `params`, `queryString`, `headers`, or `data` will help the plugin match more specific responses but neither is required. Both correspond to their matching objects as they would be passed into the $http object call.

Headers must be defined as the headers that will be used in the http call. Therefore, if in the code to be tested, the headers are defined using properties with function values, these functions will be evaluated as per the $http specification and matched by end result.

Protractor mock will respond with the **last** matched request in case there are several matches.

#### Response
The default `status` value is 200 if none is specified.

### Inspection
For testing or debugging purposes, it is possible to extract a list of http requests. Simply call the `requestsMade` function as follows:

	var mock = require('protractor-http-mock');
	...

	  expect(mock.requestsMade()).toEqual([
			{ url : '/default', method : 'GET' },
			{ url : '/users', method : 'GET' }
		]);

It is also possible to clear the list of requests with the `clearRequests()` method.
		
### Examples
Included in the code base is an extensive list examples on how to use all the features of this plugin. Please take a look if you have any questions.

To run these tests locally, please follow these steps from the root directory:

1. `npm install`
2. `node_modules/.bin/webdriver-manager update`
3. `npm run example`


# IMPROVEMENTS
By default the protractor mock framework provides on the mock capability. Mock files have to be created and maintained manually. In other word depending on the number of tests and mock files we have in case of request schemas change, we might face a huge maintenance impact.
This improvement will give the ability to record http request and store the response in the right format. 
The idea is to create a node server responsible to store the request and response in the disk.
The record flow can be described as follow: 
-	Client make a call to the server
-	Mock framework intercept and forward it to the server (if does not match some mock configuration).
-	The server reply.
-	We intercept the response 
-	Then send it to our record server to store it.
-	Response forwarded to client.

What we added:

<Talend-protractor-http>
├── talend_record_server                      - main folder
│   ├── record_generated                      - generated records are stored here
│   └── RecordConfig.js                       - Node js server to store record
│	└── server.js


Usage:

Scenario: I want to write a new integration test.

	A- First start recording backend calls that your scenario might perform (Record Mode):
		Record mode: In this mode we’re storing request and response in a file. Then it is important that the response comes from the server and not the mock file. So please make sure that there is no mock define in your test. If yes disable it by just passing an empty array to the mock method like: mock([]).
	
		1-	Configure the record mode.
				Open the record configuration file /talend-protractor-http-mock/talend_record_server/RecordConfig.js (changing the name of this config file or its parent dir would imply to change them in the framework code (/lib/httpMock.js and /lib/initdata.js) )
		
				module.exports = {
					recordMode: true,				// if true it will record request, if false no records, configs below are ignored
					
					backendConfig:{					//Just record request to this backend server. if port and host empty (""), it will record all requests
						port:"8888",			
						host:"10.42.10.99"
					},
					
					recordServer:{					//Your can configure the record server port here. the host is localhost
						listenPort:"1114"
					}
				};
		
				Set the property recordMode to true, also you can set to which backend call you want to record.
				Make sure to have the following in you test.spec:
				
				afterEach(function(){		
						mock.teardown();
					});
				beforeEach(function(){	
						mock([]);
					});
				Note that the content of the mock should be empty (‘mock([]);’) in recordMode.
 
		2-	Launch the gulp server
				Open the ruby command line, navigate to the /dataprep-webapp folder of the dataprep project then execute the command: gulp serve

		3-	Launch your test.spec and generate mock files
				Make sure to have big timeout or a break point in order to have enough time to manually complete your scenario once you’ve initiated a browser with protractor.
				To launch the test, open a command line then navigate to /dataprep-webapp/talend-protractor-http-mock and execute the command: npm run example (this should launch a browser with dataprep welcome page)
				Note that ‘example’ is a combination of tasks defined in the / talend-protractor-http-mock/gruntFile.js. We’ve added a new task ‘run_node’ to it for starting the record server.
				Now from the welcome page you can manually complete your scenario (mock files are generated for requests call sent to the specified backend server defined in the recordConfig file).
				Goto the folder:
				
				As illustrated in the screenshot above, in the /record_generated, a folder containing you scenario mock files has been created.  It contains *.js files corresponding to the mock files and one *.config file which contains list mock files names. This .config file is handful, the point is to avoid the one by one copy/pasting of mock files names in the test mock configuration method.

	B- Secondly test generated mock files (mock replay mode):
		Replay mode: if your test defines some mock file, any request that matches a defined mock will be intercepted and mock response replied.
  
		1-	Configure replay mode 

				Open the record configuration file /talend-protractor-http-mock/talend_record_server/RecordConfig.js (changing the name of this config file or its parent dir would imply to change them in the framework code (/lib/httpMock.js and /lib/initdata.js) )
				
				module.exports = {
					recordMode: true,				// if true it will record request, if false no records, configs below are ignored
					
					backendConfig:{					//Just record request to this backend server. if port and host empty (""), it will record all requests
						port:"8888",			
						host:"10.42.10.99"
					},
					
					recordServer:{					//Your can configure the record server port here. the host is localhost
						listenPort:"1114"
					}
				};
				Set the property recordMode to false, backendConfig property will be ignored.

		2-	Configure mock
				Go to the folder where previously generated mock files are stored:
				
				Open mock.config file and copy the content.
				Open your test.spec paste the content in the highlighted section as shown below: 
				To make the mock files test more relevant, you can change the backend address in the web application configuration (not record config one) by setting a non-existing address.


		3-	Run your scenario without backend
				Important thing you have to run exactly the same scenario made during the generation of mock files.

	C- For ending finalize your integration test automation:
	You’re done.

SOME IDEAS FOR MORE IMPROVEMENT
1-	Selenium server test is launched for each test execution, this could impact the test execution performance. We can disable the task dedicated for starting the server in gruntfile.js and started externally with the CI tool before the test execution and stop it at the end of the execution.
2-	Make the test automatically point to the generated mock file folder by passing the test name as the folder to be created.
3-	If we want a jbehave like script but in js, we can use coffee script.
4-	Integrate results to the unit tests report or current Jbehave one.



#TIPS HOW TO
Protractor Config file:
	1-	Configure tests folder:
		In talend-protractor-http-mock/example/protractor.conf section :
		specs: [
			  		'spec/casing.spec.js'
			 ], 	
	2-	Configure mock directory and default mock files (default mock file are usually overridden in the test using mock method mock([])):
		In talend-protractor-http-mock/example/protractor.conf section :
		mocks: {
			  		dir: 'mocks/export-api',
			  		default: []
			 },

So if we change the name of /spec or /mock folders, the configuration above has to be updated accordantly.

Grunt config file:
	1-	Configure protractor config file:
		In /talend-protractor-http-mock/Gruntfile.js section:
		protractor: {
		            options: {
		                configFile: 'example/protractor.conf',
		                //debug: true
		            },
		            example: {}
		        },



	2-	Configure the Record server

		In /talend-protractor-http-mock/Gruntfile.js section:

		run_node: {
		            start: {
		                options: {
		                    cwd: process.cwd(),
		                    stdio: ['pipe', 'pipe', 'pipe'],
		                    env: {
		                       
		                    },
		                    detached: true
		               },
		                files: { src: [ './talend_record_server/server.js'] } //here
		            }
		        },

Therefore in case of renaming of the /example folder gruntConfig must be updated.

In case of rename of the /talend_record_server/server.js path , the following file has to be updated:
	-	talend-protractor-http-mock/Gruntfile.js
	-	/talend-protractor-http-mock/lib/httpMock.js
	-	/talend-protractor-http-mock/lib/initData.js




### Contributions and recognition

* `jdgblinq` for their contribution to query string matching.
* `ReactiveRaven` for adding convenience methods key for getting this to work with ngResource.
* `nielssj` for the `requestsMade` functionality and for pointing out a few bugs.
* `nadersoliman` for their input and pull request on allowing the user to set custom name for the protractor configuration file.
* `kharnt0x` for submitting a bug fix for proper callback handling.
* `zigzackattack` for their contribution on post data matching.
* `matthewjh` for their pull request to allow the plugin to play nice with interceptors.
* `stevehenry13` for their pull request to allow the plugin to work with transforms.
* `brandonroberts` for the fantastic work on interceptors and promises.
