describe('horizontalBarchart directive', function () {
	'use strict';

	var createElement, element, scope, statsData;

	beforeEach(module('data-prep.horizontalBarchart'));
	beforeEach(inject(function ($rootScope, $compile, $timeout, StatisticsService) {
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
			StatisticsService.data = statsData;
			scope = $rootScope.$new();
			element = angular.element('<horizontal-barchart id="barChart" width="250" height="600"></horizontal-barchart>');

			angular.element('body').append(element);
			$compile(element)(scope);
			scope.$digest();
		};
	}));

	afterEach(inject(function (StatisticsService) {
		scope.$destroy();
		element.remove();
		StatisticsService.resetCharts();
	}));

	it('should render all bars', function () {
		//when
		createElement();

		//then
		expect(element.find('rect').length).toBe(statsData.length*2);
		expect(element.find('.value').length).toBe(statsData.length);
		expect(element.find('.bg-rect').length).toBe(statsData.length);
		expect(element.find('.bar').length).toBe(statsData.length);
	});

	//waiting for a solution for this issue PhantomJs + svg :
	// https://github.com/ariya/phantomjs/issues/13293
	// it('should call addFilter function on click', inject(function (StatisticsService) {
	//	//when
	//	createElement();
	//	spyOn(StatisticsService, 'addFilter').and.returnValue();
	//	var event = new angular.element.Event('click');
	//
	//	//when
	//	element.find('.bg-rect').eq(5).trigger(event);
	//	console.log(element.find('.bg-rect').eq(5));
	//	//then
	//	expect(StatisticsService.addFilter).toHaveBeenCalled();
	//}));
});