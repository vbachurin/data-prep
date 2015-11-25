describe('Navigation List controller', function () {
	'use strict';

	var createController, scope;

	beforeEach(module('talend.widget'));

	beforeEach(inject(function ($rootScope, $controller) {
		scope = $rootScope.$new();

		createController = function () {
			var ctrlFn = $controller('NavigationListCtrl', {
				$scope: scope
			});
			return ctrlFn;
		};
	}));

	it('should decrement borders', inject(function () {
		//given
		var ctrl = createController();
		ctrl.lastLabelIndex = 4;

		//when
		ctrl.showBack();

		//then
		expect(ctrl.firstLabelIndex).toBe(-1);
		expect(ctrl.lastLabelIndex).toBe(3);
	}));

	it('should increment borders', inject(function () {
		//given
		var ctrl      = createController();
		ctrl.lastLabelIndex = 4;

		//when
		ctrl.showForth();

		//then
		expect(ctrl.firstLabelIndex).toBe(1);
		expect(ctrl.lastLabelIndex).toBe(5);
	}));
});