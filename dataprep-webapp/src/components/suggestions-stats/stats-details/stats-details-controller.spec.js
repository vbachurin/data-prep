describe('Stats-details controller', function() {
	'use strict';

	var createController, scope;

	beforeEach(module('data-prep.stats-details'));

	beforeEach(inject(function ($rootScope, $controller) {
		scope = $rootScope.$new();

		createController = function () {
			var ctrl = $controller('StatsDetailsCtrl', {
				$scope: scope
			});
			return ctrl;
		};
	}));

	it('should trigger watcher on the columnSuggestionService service', inject(function (ColumnSuggestionService) {
		//given
		var ctrl = createController();
		var col = {'id':'0001',
			type:'boolean',
			statistics:{
				count:4,
				distinctCount:4,
				duplicateCount:4,
				empty:4,
				invalid:4,
				valid:4
			}
		};
		ColumnSuggestionService.currentColumn = null;

		//when
		ColumnSuggestionService.currentColumn = col;
		scope.$digest();

		//then
		expect(ctrl.updatedColumn).toBe(col);
		expect(ctrl.statsByColType).toEqual([
			{'Count':col.statistics.count},
			{'Distinct Count':col.statistics.distinctCount},
			{'Duplicate Count':col.statistics.duplicateCount},
			{'Empty':col.statistics.empty},
			{'Invalid':col.statistics.invalid},
			{'Valid':col.statistics.valid}
		]);

	}));

});