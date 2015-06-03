(function () {
	'use strict';

	/**
	 * @ngdoc controller
	 * @name data-prep.recipe.controller:RecipeBulletCtrl
	 * @description recipeBullet controller.
	 * @requires data-prep.services.recipe.service:RecipeService
	 * @requires data-prep.services.playground.service:playground
	 **/
	function RecipeBulletCtrl (RecipeService, $element, BulletService) {
		var vm           = this;
		vm.recipeService = RecipeService;
		vm.bulletService = BulletService;
		var parentHeight    = $element.parent().height();
		var remainingHeight = ((parentHeight - 20) / 2) + 5;//5 is the distance between directives
		vm.topPath          = 'M 15 0 L 15 ' + remainingHeight + ' Z';
		vm.circleCenterY    = remainingHeight + 10;
		vm.bottomPath       = 'M 15 ' + (vm.circleCenterY + 12) + ' L 15 ' + (vm.circleCenterY + 10 + remainingHeight) + ' Z';
		vm.currentStepIndex = vm.recipeService.getCurrentStepIndex(vm.step);

		/**
		 * @ngdoc method
		 * @name getBulletsTochange
		 * @methodOf data-prep.recipe.controller:RecipeBulletCtrl
		 * @param the bullets Elements Array and the object Step
		 * @description according to the step.inactive, it slices the svg Array
		 * @returns returns the bullets Elements Array to be changed after a hover
		 */
		vm.getBulletsTochange = function (allSvgs, step) {
			var lastActiveStepIndex = vm.recipeService.getActiveThresholdStepIndexOnLaunch();
			if (!step.inactive) {
				return allSvgs.slice(vm.currentStepIndex, lastActiveStepIndex + 1);
			} else {
				return allSvgs.slice(lastActiveStepIndex + 1, vm.currentStepIndex + 1);
			}
		};
	}

	/**
	 * @ngdoc property
	 * @name recipe
	 * @propertyOf data-prep.recipeBullet.controller:RecipeBulletCtrl
	 * @description The recipe.
	 * It is bound to {@link data-prep.services.recipe.service:RecipeService RecipeService} property
	 * @type {object[]}
	 */
	Object.defineProperty(RecipeBulletCtrl.prototype,
		'recipe', {
			enumerable: true,
			configurable: false,
			get: function () {
				return this.recipeService.getRecipe();
			}
		});

	angular.module('data-prep.recipeBullet')
		.controller('RecipeBulletCtrl', RecipeBulletCtrl);
})();