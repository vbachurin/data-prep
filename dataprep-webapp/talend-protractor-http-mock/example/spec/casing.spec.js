var mock  = require('../../index'), //substitute for require('protractor-http-mock')
	get = require('./lib/get') ; 
var testName='casing';
describe(testName, function(){
	
	afterEach(function(){		
		mock.teardown();
	});
	beforeEach(function(){
		
		mock(['-api-datasets-1451483747059','-api-export-formats-1451483747223','-api-preparations-1451483747381','-api-folders-datasets-1451483747493']);
	});
	it('ignores casing for request method', function(){
		
		get();
		
		browser.driver.wait(function() {
            return browser.driver.isElementPresent(By.xpath("//a[@class='introjs-button introjs-skipbutton']")); 
        }, 50000);			
		element(by.xpath("//a[@class='introjs-button introjs-skipbutton']")).click();
		
		browser.driver.wait(function() {
            return browser.driver.isElementPresent(By.xpath("//div[@id='dataset_0']/a[@class='btn-icon btn-transparent']")); 
        }, 200000);		
		element(by.xpath("//div[@id='dataset_0']/a[@class='btn-icon btn-transparent']")).click();
		//element(by.xpath("//div[@class='editable-text-container']/span[text()='DQ_WAVE1_3']")).click();
		
		
		
		browser.driver.wait(function() {
            return browser.driver.isElementPresent(By.xpath("//a[@class='introjs-button introjs-skipbutton']")); 
        }, 100000);			
		element(by.xpath("//a[@class='introjs-button introjs-skipbutton']")).click();
		
		
		browser.driver.wait(function() {
            return browser.driver.isElementPresent(By.xpath("//div[@class='grid-header-title']/div[text()='ID']")); 
        }, 50000);
		element(by.xpath("//div[@class='grid-header-title']/div[text()='ID']")).click();
		
		browser.driver.wait(function() {
            return browser.driver.isElementPresent(By.xpath("//ul[@class='actions-group']//a[text()='Fill Empty Cells with Text...']")); 
        }, 50000);
		element(by.xpath("//ul[@class='actions-group']//a[text()='Fill Empty Cells with Text...']")).click();
		
		browser.driver.wait(function() {
            return browser.driver.isElementPresent(By.xpath("//a[@class='introjs-button introjs-skipbutton']")); 
        }, 50000);			
		element(by.xpath("//a[@class='introjs-button introjs-skipbutton']")).click();
		
	});
});