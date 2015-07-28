describe('ColumnProfile controller', function() {
	'use strict';

	var createController, scope;

	beforeEach(module('data-prep.column-profile'));

	beforeEach(inject(function ($rootScope, $controller) {
		scope = $rootScope.$new();

		createController = function () {
			var ctrl = $controller('ColumnProfileCtrl', {
				$scope: scope
			});
			return ctrl;
		};
	}));

	it('should call addFilter Function of the StatisticsService', inject(function (StatisticsService) {
		//given
		spyOn(StatisticsService,'addFilter').and.returnValue();
		var ctrl = createController();
		var obj = {'data':'Ulysse', 'occurrences':5};

		//when
		ctrl.barchartClickFn(obj);

		//then
		expect(StatisticsService.addFilter).toHaveBeenCalledWith(obj.data);
	}));
});