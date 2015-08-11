describe('boxplot chart directive', function () {
	'use strict';

	var createElement, element, scope, boxValues;

	beforeEach(module('data-prep.boxplotChart'));
	beforeEach(inject(function ($rootScope, $compile) {
		boxValues = {
			min:0,
			max:100,
			q1:8,
			q2:90,
			median:58,
			mean:59.79,
			variance:2051033.15
		};

		createElement = function () {

			scope = $rootScope.$new();
			scope.boxValues = null;
			element = angular.element('<boxplot-chart id="boxplotId" width="200" height="400" boxplot-data="boxValues"></boxplot-chart>');

			angular.element('body').append(element);
			$compile(element)(scope);
			scope.$digest();
		};
	}));

	beforeEach(function () {
		jasmine.clock().install();
	});
	afterEach(function () {
		jasmine.clock().uninstall();

		scope.$destroy();
		element.remove();
	});

	it('should render the different basic components of the boxplot after a 100ms delay', function () {
		//given
		createElement();

		//when
		scope.boxValues = boxValues;
		scope.$digest();
		jasmine.clock().tick(100);

		//then
		expect(element.find('rect').length).toBe(2);
		expect(element.find('.up-quantile-labels').length).toBe(2);
		expect(element.find('.low-quantile-labels').length).toBe(2);
		expect(element.find('.center').length).toBe(1);
		expect(element.find('.mean-labels').length).toBe(1);
		expect(element.find('.whiskerPolyg').length).toBe(2);
		expect(element.find('.max-min-labels').length).toBe(4);
	});

});