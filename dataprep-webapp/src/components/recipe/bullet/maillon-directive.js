(function () {
	'use strict';

	function ChainLink ($timeout) {
		return {
			restrict: 'E',
			transclude: true,
			require: '^recipe',
			scope: {
				type: '@',
				stepIndex: "=",
				step: "="
			},
			templateUrl: 'components/recipe/bullet/maillon.html',
			link: function (scope, element, attrs, recipeCtrl) {
				scope.effectiveClass  = "maillon-circle";
				scope.parentHeight    = angular.element(element[0]).parent().height();
				scope.remainingHeight = ((scope.parentHeight - 20) / 2) + 5;//5 is the distance between directives
				scope.topPath                    = 'M 15 0 L 15 ' + scope.remainingHeight + ' Z';
				scope.circleCenterY              = scope.remainingHeight + 10;
				scope.bottomPath                 = 'M 15 ' + (scope.circleCenterY + 10) + ' L 15 ' + (scope.circleCenterY + 10 + scope.remainingHeight) + ' Z';
				var bulletCircleElement          = angular.element(angular.element(element[0]).children()[0]).children()[1];
				var allSingles                   = angular.element('.single-maillon-cables-disabled');
				angular.forEach(allSingles, activateBranch);
				bulletCircleElement.onmouseenter = function () {
					recipeCtrl.stepHoverStart(scope.stepIndex);

					var enabledCircles       = angular.element(".maillon-circle");
					var enabledCirclesNumber = enabledCircles.length;
					var disabledCircles      = angular.element(".maillon-circle-disabled");

					if (+scope.stepIndex <= enabledCirclesNumber - 1) {//we hovered on an enabled bullet
						var bulletsToBeChanged = enabledCircles.slice(+scope.stepIndex, enabledCircles.length);
						angular.forEach(bulletsToBeChanged, function (circle) {
							circle.children[1].setAttribute("class", "maillon-circle-enabled-hovered");
						});
					} else {//we hovered on a disabled bullet
						var bulletsToBeChanged = disabledCircles.slice(0, +scope.stepIndex - enabledCirclesNumber + 1);
						angular.forEach(bulletsToBeChanged, function (circle) {
							circle.children[1].setAttribute("class", "maillon-circle-disabled-hovered");
						});
					}
				};

				bulletCircleElement.onmouseleave = function () {
					$timeout(recipeCtrl.stepHoverEnd);
					var enabledCircles = angular.element(".maillon-circle-enabled-hovered");
					angular.forEach(enabledCircles, function (circle) {
						circle.setAttribute("class", "");
					});

					var enabledCircles = angular.element(".maillon-circle-disabled-hovered");
					angular.forEach(enabledCircles, function (circle) {
						circle.setAttribute("class", "");
					});
				};

				function activateBranch (branch) {
					branch.setAttribute("class", "");
				}

				function deActivateBranch (branch) {
					branch.setAttribute("class", "single-maillon-cables-disabled");
				}

				bulletCircleElement.onclick = function (event) {
					event.stopPropagation();
					recipeCtrl.toggleStep(scope.step);
					var allBulletsSvgs = angular.element(angular.element(".all-svg-cls"));
					var enabledCircles = angular.element(".maillon-circle");
					if (+scope.stepIndex <= enabledCircles.length - 1 && +scope.stepIndex !== allBulletsSvgs.length - 1) {
						var linkToChange = angular.element(allBulletsSvgs[scope.stepIndex - 1]).children()[2];
						deActivateBranch(linkToChange);
					} else if (+scope.stepIndex === allBulletsSvgs.length - 1) {
						var svgEl      = angular.element(allBulletsSvgs[scope.stepIndex])[0];
						var allSingles = angular.element('.single-maillon-cables-disabled');//[0].setAttribute("class","");
						angular.forEach(allSingles, activateBranch);
						if (svgEl.getAttribute("class").indexOf("maillon-circle-disabled") > 0) {
							var linkToChange = angular.element(allBulletsSvgs[scope.stepIndex - 1]).children()[2];
							activateBranch(linkToChange);
						} else {
							var linkToChange = angular.element(allBulletsSvgs[scope.stepIndex - 1]).children()[2];
							deActivateBranch(linkToChange);
						}
					} else if (+scope.stepIndex > enabledCircles.length - 1) {
						var allSingles   = angular.element('.single-maillon-cables-disabled');//[0].setAttribute("class","");
						angular.forEach(allSingles, activateBranch);
						var linkToChange = angular.element(allBulletsSvgs[scope.stepIndex]).children()[2];
						deActivateBranch(linkToChange);
					}
				};
			}
		};
	}

	angular.module('data-prep.chainLink')
		.directive('chainLink', ChainLink);
})();