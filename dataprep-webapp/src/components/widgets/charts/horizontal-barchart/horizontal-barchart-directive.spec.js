describe('horizontalBarchart directive', function () {
	'use strict';

	var createElement, element, scope, statsData;

	beforeEach(module('data-prep.horizontalBarchart'));
	beforeEach(inject(function ($rootScope, $compile) {
		statsData = [
						{'data':'Johnson','occurrences':9},
						{'data':'Roosevelt','occurrences':8},
						{'data':'Pierce','occurrences':6},
						{'data':'Wilson','occurrences':5},
						{'data':'Adams','occurrences':4},
						{'data':'Quincy','occurrences':4},
						{'data':'Clinton','occurrences':4},
						{'data':'Harrison','occurrences':4}
					];

		createElement = function () {

			scope = $rootScope.$new();
			scope.visData = null;
			scope.clicked = false;
			scope.onclck = function(){
				scope.clicked = true;
			};

			element = angular.element('<horizontal-barchart id="barChart" width="250" height="400"'+
											 'on-click="onclck"'+
											 'visu-data="visData"'+
											 'key-field="occurrences"'+
											 'value-field="data"'+
											 '></horizontal-barchart>');

			angular.element('body').append(element);
			$compile(element)(scope);
			scope.$digest();
		};
	}));

	afterEach(function () {
		scope.$destroy();
		element.remove();
	});

	it('should render all bars', function () {
		//given
		createElement();

		//when
		scope.visData = statsData;
		scope.$digest();

		//then
		expect(element.find('rect').length).toBe(statsData.length*2);
		expect(element.find('.value').length).toBe(statsData.length);
		expect(element.find('.bg-rect').length).toBe(statsData.length);
		expect(element.find('.bar').length).toBe(statsData.length);
	});

	//waiting for a solution for this issue PhantomJs + svg :
	// https://github.com/ariya/phantomjs/issues/13293
	// it('should call addFilter function on click', inject(function () {
	//	//given
	//	createElement();
	//	scope.visData = statsData;
	//	scope.$digest();
	//	var event = new angular.element.Event('click');
	//
	//	//when
	//	 element.find('.bg-rect').eq(5).trigger(event);
	//
	//	console.log(element.find('.bg-rect').eq(5));
	//	//then
	//}));
});