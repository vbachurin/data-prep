describe('stats details directive', function() {
	'use strict';

	var scope, element, createElement;

	beforeEach(module('data-prep.stats-details'));
	beforeEach(module('htmlTemplates'));

	beforeEach(inject(function($rootScope, $compile) {
		scope = $rootScope.$new();

		createElement = function() {
			scope = $rootScope.$new();
			element = angular.element('<stats-details></stats-details>');
			$compile(element)(scope);
			scope.$digest();
		};
	}));

	afterEach(function() {
		scope.$destroy();
		element.remove();
	});

	it('should set "Action" in title when no column is selected', inject(function(ColumnSuggestionService) {
		//given
		ColumnSuggestionService.currentColumn = null;

		//when
		createElement();

		//then
		expect(element.find('.title').text().trim()).toBe('Stats');
	}));

	it('should set column name in title', inject(function() {
		//given
		createElement();

		element.controller('statsDetails').statsByColType = [
			{'Count':101},
			{'Distinct': 99},
			{'Duplicate': 2},
			{'Empty':0},
			{'Invalid':0},
			{'Max':101},
			{'Mean':51},
			{'Min':1},
			{'Lower': 26},
			{'Median':51},
			{'Upper ': 76},
			{'Valid':101},
			{'Variance':858.50}
		];
		element.controller('statsDetails').updatedColumn = true;
		scope.$apply();

		//when
		var event = angular.element.Event('click');
		element.find('li').eq(2).trigger(event);

		//then
		expect(element.find('.keys-text').length).toBe(13);
	}));
});
