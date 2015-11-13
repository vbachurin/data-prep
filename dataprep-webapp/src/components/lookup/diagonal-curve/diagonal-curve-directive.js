(function () {
	'use strict';

	function DiagonalCurve() {
		return {
			restrict: 'E',
			scope: {
				positions: '='
			},

			link: function (scope, element) {

				function renderDiagonal (){

					// Set the dimensions of the canvas / graph
					var	margin = {top: 5, right: 5, bottom: 5, left: 5},
						   width = 1000 - margin.left - margin.right,
						   height = 30 - margin.top - margin.bottom;

					// Adds the svg canvas
					var	svg = d3.select('#diagonal-curve')
						.append('svg')
						.attr('width', width + margin.left + margin.right)
						.attr('height', height + margin.top + margin.bottom)
						.append('g')
						.attr('transform', 'translate(' + margin.left + ',' + margin.top + ')');

					var diagonal = d3.svg.diagonal()
						.source({x:scope.positions.from, y:0})
						.target({x:scope.positions.to, y:25});


					svg.append('path')
						.attr('fill', 'none')
						.attr('stroke', '#f61A09')
						.attr('stroke-width', 3)
						.style('stroke-dasharray', ('3, 3'))
						.attr('d', diagonal);

				}

				scope.$watch('positions', function(){
					element.empty();
					renderDiagonal();
				});

			}
		};
	}

	angular.module('data-prep.lookup-diagonal-curve')
		.directive('diagonalCurve', DiagonalCurve);
})();