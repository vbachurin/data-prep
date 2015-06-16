(function () {
	'use strict';

	/**
	 * @ngdoc directive
	 * @name data-prep.horizontalBarchart.directive:horizontalBarchart
	 * @description This directive renders the horizontal bar chart.
	 * @restrict E
	 * @usage
	 * <horizontal-barchart id="containerId" width="width" height="height"></horizontal-barchart>
	 * */

	function HorizontalBarchart (StatisticsService) {
		return {
			restrict: 'E',
			link: function (scope, element, attrs) {
				var statData = [];
				scope.$watch(function(){
					return StatisticsService.data;
				}, function(newData){
					element.empty();

					statData = newData;
					if(statData){
						renderBarchart();
					}
				});

				function renderBarchart(){
					var container = attrs.id;
					var width = +attrs.width;
					var height = +attrs.height;

					var m = [15, 10, 10, 10],
						w = width - m[1] - m[3],
						h = height - m[0] - m[2];

					var x = d3.scale.linear().range([0, w]),
						y = d3.scale.ordinal().rangeRoundBands([0, h], 0.18);

					var xAxis = d3.svg.axis().scale(x).orient('top').tickSize(-h).ticks(Math.abs(x.range()[1] - x.range()[0]) / 50),
						yAxis = d3.svg.axis().scale(y).orient('left').tickSize(0);

					var tip = d3.tip()
						.attr('class', 'd3-tip')
						.offset([-10, 0])
						.html(function(d) {
							return '<strong>Occurences:</strong> <span style="color:yellow">' + d.occurrences + '</span>';
						});

					var svg = d3.select('#'+container).append('svg')
						.attr('width', w + m[1] + m[3])
						.attr('height', h + m[0] + m[2])
						.append('g')
						.attr('transform', 'translate(' + m[3] + ',' + m[0] + ')');

					svg.call(tip);

					// Parse numbers, and sort by value.
					statData.forEach(function(d) { d.occurrences = +d.occurrences; });
					//statData.sort(function(a, b) { return b.occurrences - a.occurrences; });

					// Set the scale domain.
					x.domain([0, d3.max(statData, function(d) { return d.occurrences; })]);
					y.domain(statData.map(function(d) { return d.data; }));

					var bar = svg.selectAll('g.bar')
						.data(statData)
						.enter().append('g')
						.attr('class', 'bar')
						.attr('transform', function(d) { return 'translate(0,' + y(d.data) + ')'; });

					bar.append('rect')
						.attr('width', x(0))
						.attr('height', y.rangeBand())
						.transition().delay(function (d,i){ return i * 30;})
						.attr('width', function(d) { return x(d.occurrences); })
						.attr('height', y.rangeBand());

					svg.append('g')
						.attr('class', 'x axis')
						.call(xAxis);

					svg.append('g')
						.attr('class', 'y axis')
						.call(yAxis);

					bar.append('text')
						.attr('class', 'value')
						.attr('x', 10)
						.attr('y', y.rangeBand() / 2)
						.attr('dx', 0)
						.attr('dy', '.35em')
						.attr('text-anchor', 'start')
						.transition().delay(function (d,i){ return i * 30;})
						.text(function(d) { return d.data?d.data:'(EMPTY)';/* + '  ' + d.occurrences;*/});

					/************btgrect*********/
					var bgBar = svg.selectAll('g.bg-rect')
						.data(statData)
						.enter().append('g')
						.attr('transform', function(d) { return 'translate(0,' + (y(d.data)-2) + ')'; });

					bgBar.append('rect')
						.attr('width', w+100)
						.attr('height', y.rangeBand()+4)
						.attr('class','bg-rect')
						.style('opacity',0)
						.attr('z-index',100)
						.on('mouseenter',function(d){
							d3.select(this).style('opacity',0.4);
							tip.show(d);
						})
						.on('mouseleave',function(d){
							d3.select(this).style('opacity',0);
							tip.hide(d);
						})
						.on('click',function(d){
							StatisticsService.addFilter(d.data);
						});
				}
			}
		};
	}

	angular.module('data-prep.horizontalBarchart')
		.directive('horizontalBarchart', HorizontalBarchart);
})();