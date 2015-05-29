(function () {
	'use strict';

	/**
	 * @ngdoc directive
	 * @name data-prep.recipe.directive:recipeBullet
	 * @description This directive display the recipe Bullet.
	 * @restrict E
	 * @usage
	 * <recipe-bullet class="step-trigger2" step="step" step-index="$index" ></recipe-bullet>
	 * */

	function RecipeBullet ($timeout) {
		return {
			restrict: 'E',
			require: '^recipe',
			scope: {
				type: '@',
				step: '='
			},
			templateNamespace: 'svg',
			controller: 'RecipeBulletCtrl',
			controllerAs: 'recipeBulletCtrl',
			bindToController: true,
			templateUrl: 'components/recipe/bullet/recipe-bullet.html',
			link: function (scope, element, attrs, recipeCtrl) {
				var bulletCircleElement = angular.element(element[0]).find('circle')[0];
				var bulletElement = element[0];
				var allBulletsSvgs      = [];
				var recipeBulletCtrl    = scope.recipeBulletCtrl;
				var bulletsToBeChanged  = [];

				/** onmouseenter **/
				bulletElement.onmouseenter = function () {
					recipeCtrl.stepHoverStart(recipeBulletCtrl.currentStepIndex);
					allBulletsSvgs = angular.element('.all-svg-cls');
					if (!recipeBulletCtrl.step.inactive) {
						bulletsToBeChanged = recipeBulletCtrl.getBulletsTochange(allBulletsSvgs, recipeBulletCtrl.step);
						_.each(bulletsToBeChanged, function (svg) {
							svg.children[1].setAttribute('class', 'maillon-circle-enabled-hovered');
						});
					} else {
						bulletsToBeChanged = recipeBulletCtrl.getBulletsTochange(allBulletsSvgs, recipeBulletCtrl.step);
						_.each(bulletsToBeChanged, function (svg) {
							svg.children[1].setAttribute('class', 'maillon-circle-disabled-hovered');
						});
					}
				};

				/** onmouseleave **/
				bulletElement.onmouseleave = function () {
					recipeCtrl.stepHoverEnd();
					_.each(bulletsToBeChanged, function (svg) {
						svg.children[1].setAttribute('class', '');
					});
				};

				function activateBranch (branch) {
					branch.setAttribute('class', '');
				}

				function deActivateBranch (index) {
					var branch = allBulletsSvgs.eq(index).find('>path').eq(1)[0];
					branch.setAttribute('class', 'single-maillon-cables-disabled');
				}

				var allSingles = angular.element('.single-maillon-cables-disabled');
				_.each(allSingles, activateBranch);

				/** onclick **/
				bulletCircleElement.onclick = function (event) {
					event.stopPropagation();
					recipeCtrl.toggleStep(recipeBulletCtrl.step);

					var allSingles = angular.element('.single-maillon-cables-disabled');
					_.each(allSingles, activateBranch);

					allBulletsSvgs = angular.element('.all-svg-cls');
					if (!recipeBulletCtrl.step.inactive && recipeBulletCtrl.currentStepIndex !== 0) {
						deActivateBranch(recipeBulletCtrl.currentStepIndex - 1);
					} else {
						if (recipeBulletCtrl.currentStepIndex !== recipeCtrl.recipe.length - 1) {
							deActivateBranch(recipeBulletCtrl.currentStepIndex);
						}
					}
				};
			}
		};
	}

	angular.module('data-prep.recipeBullet')
		.directive('recipeBullet', RecipeBullet);
})();