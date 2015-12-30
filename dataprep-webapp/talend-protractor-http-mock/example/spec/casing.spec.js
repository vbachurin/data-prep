var mock  = require('../../index'), //substitute for require('protractor-http-mock')
	get = require('./lib/get') ; 
var testName='casing';
describe(testName, function(){
	
	afterEach(function(){		
		mock.teardown();
	});
	beforeEach(function(){
		//mock(['-api-datasets-1451416030064','-api-export-formats-1451416030132','-api-preparations-1451416030232','-api-folders-datasets-1451416030507','-api-datasets-9814b5bd-d300-471e-90d4-d564f51326c8-1451416035876','-api-transform-suggest-column-1451416037700','-api-transform-actions-column-1451416037727','-api-preparations-preview-add-1451416043902','-api-preparations-1451416043962','-api-preparations-1451416043991','-api-preparations-27510b998c7c14501b8c459779577bed56b92e12-actions-1451416045056','-api-preparations-27510b998c7c14501b8c459779577bed56b92e12-details-1451416045702','-api-preparations-27510b998c7c14501b8c459779577bed56b92e12-content-1451416045754','-api-transform-suggest-column-1451416046504','-api-transform-actions-column-1451416046503','-api-preparations-preview-update-1451416051434']);
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