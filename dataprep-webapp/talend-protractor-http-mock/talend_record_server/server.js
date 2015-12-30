'use strict';
	
//var vm = require('vm');
var http = require('http');
var url = require('url');
var fs = require('fs');
var recordConfig =  require('./RecordConfig');
var recordMode=recordConfig.recordMode;
var listenPort=recordConfig.recordServer.listenPort
var mocks=[];

var folderName='talend_record_server/record_generated/'+getFormatedTime() ;
if (recordMode) fs.mkdirSync(folderName);

function getFormatedTime(){
	var d = new Date();
	return d.getFullYear()+'-'+d.getMonth()+'-'+d.getDate()+'-'+d.getHours()+'-'+d.getMinutes()+'-'+d.getSeconds()+d.getMilliseconds();
}
function getTimeInMs(){
	var d = new Date();
	return d.getTime();
}

function isEmpty(value){
	return (!value || value == undefined || value == "");
}


http.createServer(function (request, response) {
	var requestDataObj='';
    var url= request.url;
    var recordFileName=url.replace(/\//g,'-')+'-'+getTimeInMs();
    request.on('data', function (chunk) {
    	requestDataObj += chunk;
    	
     });
     
    request.on('end', function() {
    	//add the tag <END> to be able to locate the last char '}'
    	var requestDataObj1=requestDataObj.toString()+"<END>";
    	//replace the string TO_REPLACE inserted ealier in the request and remove } by replacing it by empty
    	requestDataObj1=requestDataObj1.replace(/{"TO_REPLACE":/g,'module.exports = ').replace(/}<END>/g,'');
    	//the framework seems to make two call for each request, this if filters empty bad ones and duplicates
    	if ((mocks.indexOf("'"+recordFileName+"'") < 0) && !(requestDataObj1.toString()=="<END>")){ 
    		mocks.push("'"+recordFileName+"'");
    	}
    	// store request data only if contend correct
    	if(!(requestDataObj1.toString()=="<END>") ){
	    	fs.writeFileSync(folderName+'/'+recordFileName+'.js',requestDataObj1);
	    	fs.writeFileSync(folderName+'/mock.config',mocks);
    	}
       	response.writeHead(200,{'Context-Type': 'application/json; charset=utf-8','Access-Control-Allow-Origin': '*','Access-Control-Allow-Credentials':'true','Access-Control-Allow-Headers':'Content-Type','Access-Control-Request-Headers':'Content-Type'});
    	response.write(requestDataObj);
    	response.end();

    });
       
    request.on('error', function(e) {
    	  console.error(e);
    	});
}).listen(listenPort);













