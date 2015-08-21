describe('RangeSlider controller', function () {
	'use strict';

	var createController, scope;

	beforeEach(module('data-prep.rangeSlider'));

	beforeEach(inject(function ($rootScope, $controller) {
		scope = $rootScope.$new();

		createController = function () {
			var ctrlFn = $controller('RangeSliderCtrl', {
				$scope: scope
			}, true);
			return ctrlFn();
		};

	}));

	it('should check numbers validity', inject(function () {
		//given
		var ctrl = createController();

		//when
		var amIaNumber = ctrl.toNumber('dqsfds10010');
		var amIaNumber2 = ctrl.toNumber(' 88');

		//then
		expect(amIaNumber).toBe(null);
		expect(amIaNumber2).toBe(88);
	}));

	it('should check "," existence', inject(function () {
		//given
		var ctrl = createController();

		//when
		var haveIaComma = ctrl.checkCommaExistence(',654');

		//then
		expect(haveIaComma).toBe(true);
	}));

	it('should check "," existence', inject(function () {
		//given
		var ctrl = createController();

		//when
		ctrl.setCenterValue(0,70);

		//then
		expect(ctrl.centerValue).toBe(35);
	}));
});