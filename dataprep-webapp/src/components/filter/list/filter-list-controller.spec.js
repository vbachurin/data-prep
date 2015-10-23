describe('filter list controller', function() {
	'use strict';

	var createController, scope;
	var stateMock;

	beforeEach(module('data-prep.filter-list', function ($provide) {
		stateMock = {
			playground: {
				grid: {}
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

});