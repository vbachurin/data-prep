describe('Recipe Bullet controller', function () {
	'use strict';

	var createController, scope, element;
	var allSvgs = [2, 5, 8, 58, 4, 212, 87, 52];

	beforeEach(module('data-prep.recipeBullet'));

	beforeEach(inject(function ($rootScope, $controller, RecipeService/*, PlaygroundService, PreparationService, PreviewService*/) {
		scope   = $rootScope.$new();
		element = angular.element("<div>toto</div>");

		createController = function () {
			var ctrl = $controller('RecipeBulletCtrl', {
				$scope: scope,
				$element: element
			});
			return ctrl;
		};
	}));

	it('should return be a slice of an Array step is active', inject(function (RecipeService) {
		//given
		spyOn(RecipeService, 'getCurrentStepIndex').and.returnValue(5);
		spyOn(RecipeService, 'getActiveThresholdStepIndexOnLaunch').and.returnValue(2);

		var ctrl  = createController();
		var step1 = {
			inactive: true
		};

		//when
		var result = ctrl.getBulletsTochange(allSvgs, step1);

		//then
		expect(result).toEqual([58, 4, 212]);
	}));

	it('should return be a slice of an Array step is inactive', inject(function (RecipeService) {
		//given
		spyOn(RecipeService, 'getCurrentStepIndex').and.returnValue(3);
		spyOn(RecipeService, 'getActiveThresholdStepIndexOnLaunch').and.returnValue(4);

		var ctrl  = createController();
		var step2 = {
			inactive: false
		};

		//when
		var result = ctrl.getBulletsTochange(allSvgs, step2);

		//then
		expect(result).toEqual([58, 4]);
	}));
});