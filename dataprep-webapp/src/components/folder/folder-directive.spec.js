describe('navigationList directive', function() {
	'use strict';

	var scope, createElement, element, stateMock, controller;

	beforeEach(module('data-prep.folder', function($provide){
		stateMock = {
			folder : {
				foldersStack : [
					{id:'', path:'', name: 'HOME_FOLDER'},
					{id : '1', path: '1', name: '1'},
					{id : '1/2', path: '1/2', name: '2'}
				]
			}
		};
		$provide.constant('state', stateMock);
	}));

	beforeEach(module('htmlTemplates'));

	beforeEach(inject(function($rootScope, $compile) {
		scope = $rootScope.$new();
		createElement = function() {
			element = angular.element('<folder type="type"></folder>'
			);
			scope.type = 'dataset';
			$compile(element)(scope);
			scope.$digest();
		};
	}));

	afterEach(function() {
		scope.$destroy();
		element.remove();
	});

	it('should render items list', function() {
		//when
		createElement();
		controller = element.controller('folder');
		spyOn(controller, 'goToFolder').and.returnValue();
		spyOn(controller, 'initMenuChilds').and.returnValue();

		//then
		expect(element.find('li').length).toBe(3);
		//expect(element.find('button').length).toBe(2);
		//expect(element.find('.items-list').length).toBe(1);
		//expect(element.find('.item-label').length).toBe(2);
		//expect(element.find('.item-label').eq(0).text().trim()).toBe('us-customers-500');
		//expect(element.find('.item-label').eq(1).text().trim()).toBe('dates');
		//expect(element.find('.selected-item-label').length).toBe(1);
		//expect(element.find('.selected-item-label').eq(0).text().trim()).toBe('us-customers-500');
	});

	//it('should trigger item selection callback', function() {
	//	//given
	//	scope.trigger = jasmine.createSpy('clickCb');
	//	createElement();

	//	//when
	//	element.find('.item-label').eq(1).click();

	//	//then
	//	expect(scope.trigger).toHaveBeenCalledWith(list[1]);
	//});
});