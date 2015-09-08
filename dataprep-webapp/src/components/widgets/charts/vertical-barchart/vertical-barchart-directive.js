(function () {
	'use strict';

	/**
	 * @ngdoc directive
	 * @name talend.widget.directive:horizontalBarchart???????????
	 * @description This directive renders the horizontal bar chart.???????????
	 * @restrict E???????????
	 * @usage???????????
	 *     <vertical-barchart id="barChart"???????????
	 *             width="250"???????????
	 *             height="400"???????????
	 *             on-click="columnProfileCtrl.barchartClickFn"???????????
	 *             visu-data="columnProfileCtrl.processedData"???????????
	 *             key-field="occurrences"???????????
	 *             value-field="data"
	 *         ></vertical-barchart>
	 * */

	function VerticalBarchart() {
		return {
			restrict: 'E',
			scope: {
				onClick: '&',
				visuData: '=',
				keyField: '@',
				valueField: '@',
				keyLabel:'@'
			},
			link: function (scope, element, attrs) {
				var xField = scope.keyField;//occurences
				var yField = scope.valueField;
				var renderTimeout;
				var tip;

				function renderVBarchart(statData) {
					var container = attrs.id;
					var width = +attrs.width;
					var height = +attrs.height;

					var margin = {
						top: 20,
						right: 20,
						bottom: 110,
						left: 40
						},
						w = width - margin.left - margin.right,
						h = height - margin.top - margin.bottom;

					var x = d3.scale.ordinal()
						.rangeRoundBands([0, w], .3);
					var y = d3.scale.linear()
						.range([h, 0]);

					var xAxis = d3.svg.axis()
						.scale(x)
						.orient("bottom");

					var yAxis = d3.svg.axis()
						.scale(y)
						.orient("left");

					tip = d3.tip()
						.attr('class', 'vertical-barchart-cls d3-tip')
						.offset([-10, 0])
						//.direction('n')
						.html(function(d) {
							return 	'<strong>Occurences:</strong> <span style="color:yellow">' + d[yField] + '</span>'+
								'<br/>'+
								'<br/>'+
								'<strong>Range:</strong> <span style="color:yellow">'+ d[xField] + '</span>';
						});

					var line = d3.svg.line()
						.interpolate("monotone")
						//    .interpolate("step-after")
						.x(function(d) {
							return x(d[xField]);
						})
						.y(function(d) {
							return y(d[yField]);
						});

					var svg = d3.select("#"+container).append("svg")
						.attr('class', 'vertical-barchart-cls')
						.attr("width", w + margin.left + margin.right)
						.attr("height", h + margin.top + margin.bottom)
						.append("g")
						.attr("transform", "translate(" + margin.left + "," + margin.top + ")");

					svg.call(tip);

					// Parse numbers, and sort by value.
					statData.forEach(function (d) {
						d[yField] = +d[yField];
					});

					// Set the scale domain.
					x.domain(statData.map(function(d) {
						return d[xField];
					}));
					y.domain([0, d3.max(statData, function(d) {
						return d[yField];
					})]);

					svg.append("g")
						.attr("class", "x axis")
						.attr("transform", "translate(0," + h + ")")
						.call(xAxis)
						.selectAll("text")
						.attr("y", 0)
						.attr("x", 9)
						.attr("dy", ".35em")
						.attr("transform", "rotate(90)")
						.style("text-anchor", "start");

					svg.append("g")
						.attr("class", "y axis")
						.call(yAxis)
						/*.append("text")
						 .attr("transform", "rotate(-90)")
						 .attr("y", 6)
						 .attr("dy", ".71em")
						 .style("text-anchor", "end")
						 .text("Frequency")*/;

					var buckets = svg.selectAll(".bar")
						.data(statData)
						.enter().append("rect")
						.attr("class", "bar")
						.attr("x", function(d) {
							return x(d[xField]);
						})
						.attr("width", x.rangeBand())
						.attr("y", function(d) {
							return y(d[yField]);
						})
						.attr("height", 0)
						.transition().duration(500)
						.attr("height", function(d) {
							return h - y(d[yField]);
						});

					/************btgrect*********/
					var bgBar = svg.selectAll('g.bg-rect')
						.data(statData)
						.enter().append('g')
						.attr('transform', function (d) {
							return 'translate('+ (x(d[xField]) - 2) +', 0)';
						});

					bgBar.append('rect')
						.attr('width', x.rangeBand() + 4)
						.attr('height', h)
						.attr('class', 'bg-rect')
						.style('opacity', 0)
						.attr('z-index', 100)
						.on('mouseenter', function (d) {
							d3.select(this).style('opacity', 0.4);
							tip.show(d);
						})
						.on('mouseleave', function (d) {
							d3.select(this).style('opacity', 0);
							//tip.hide(d);
						})
						.on('click', function (d) {
							scope.onClick()(d);
						});

					/**************** Connecting Line*****************/
/*					var gLine = svg.append("g")
						.attr("transform", "translate(" + x.rangeBand() / 2 + ",0)");

					gLine.append("path")
						.datum(statData)
						.attr("class", "line")
						.attr("d", line);

					gLine.selectAll(".dot")
						.data(statData)
						.enter().append("circle")
						.attr("class", "dot")
						.attr("cx", line.x())
						.attr("cy", line.y())
						.attr("r", 4.5);*/
				}

				scope.$watch('visuData',
					function (statData) {
						element.empty();
						if(tip){
							tip.hide();
						}
						if (statData) {
							clearTimeout(renderTimeout);
							renderTimeout = setTimeout(renderVBarchart.bind(this, statData), 100);
						}
					});
			}
		};
	}

	angular.module('talend.widget')
		.directive('verticalBarchart', VerticalBarchart);
})();