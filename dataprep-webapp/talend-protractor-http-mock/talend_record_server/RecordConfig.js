module.exports = {
	recordMode: false,				// if true it will record request, if false no records, configs below are ignored
	
	backendConfig:{					//Just record request to this backend server. if port and host empty (""), it will record all requests
		port:"8888",			
		host:"10.42.10.99"
	},
	
	recordServer:{					//Your can configure the record server port here. the host is localhost
		listenPort:"1114"
	}
};