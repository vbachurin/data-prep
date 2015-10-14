describe('filter list controller', function() {
	'use strict';

	var createController, scope;
	var stateMock;

	beforeEach(module('data-prep.filter-list', function ($provide) {
		stateMock = {
			playground: {
				shownLinesLength: 5,
				allLinesLength:10
			}
		};
		$provide.constant('state', stateMock);
	}));

	beforeEach(inject(function ($rootScope, $controller) {
		scope = $rootScope.$new();

		createController = function () {
			return $controller('FilterListCtrl', {
				$scope: scope
			});
		};
	}));

	it('should calculate the percentage', inject(function () {
		//given
		var ctrl = createController();

		//when
		var percentLabel = ctrl.calculateLinesPercentage();

		//then
		expect(percentLabel).toBe('50%');
	}));
});