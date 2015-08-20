(function () {
	'use strict';

	/**
	 * @ngdoc directive
	 * @name data-prep.rangeSlider.directive:rangeSlider
	 * @description This directive renders the rangeSlider.
	 * @restrict E
	 * @usage
	 *     <range-slider
	 *             width="250"
	 *             height="100"
	 *             range-limits="statsDetailsCtrl.rangeLimits"
	 *             on-brush-end="statsDetailsCtrl.onBrushEndFn"
	 *             id="domId"
	 *         ></range-slider>
	 * */

	function RangeSlider() {
		return {
			restrict: 'E',
			scope: {
				rangeLimits: '=',
				onBrushEnd: '&'
			},
			controller: 'RangeSliderCtrl',
			controllerAs: 'rangeSliderCtrl',
			link: function (scope, element, attrs, ctrl) {
				var h = attrs.height;
				var w = attrs.width;
				var container = attrs.id;
				var renderTimeout;

				function renderRangerSlider(rangeLimits){
					var minimum = rangeLimits.min;
					var maximum = rangeLimits.max;
					var minBrush = typeof rangeLimits.minBrush !== 'undefined' ? rangeLimits.minBrush : minimum;
					var maxBrush = typeof rangeLimits.maxBrush !== 'undefined' ? rangeLimits.maxBrush : maximum;

					var nbDecimals = d3.max([decimalPlaces(minBrush), decimalPlaces(maxBrush)]);
					var margin = {top: 25, right: 25, bottom: 80, left: 10},
						width = w - margin.left - margin.right,
						height = h - margin.top - margin.bottom;

					var x = d3.scale.linear()
						.domain([minimum, maximum])
						.range([0, width]);

					ctrl.setCenterValue(minBrush, maxBrush);

					ctrl.initBrush(x);

					var svg = d3.select('#'+container).append('svg')
						.attr('width', width + margin.left + margin.right)
						.attr('height', height + margin.top + margin.bottom)
						.attr('class','range-slider-cls')
						.append('g')
						.attr('transform', 'translate(' + margin.left + ',' + margin.top + ')');

					var xAxisg = svg.append('g')
						.attr('class', 'x axis')
						.attr('transform', 'translate(0,' + height + ')')
						.call(d3.svg.axis().scale(x).orient('bottom').ticks(Math.abs(x.range()[1] - x.range()[0]) / 50)
								.tickFormat(function(d){
									if(d > 1e4 || d < -1e4){
										return d3.format('e')(d);
									}
									else {
										return d3.format(',')(d);
									}
								})
							);

					xAxisg.selectAll('text').attr('y', 13);

					svg.append('g').append('foreignObject')
						.attr('width', width)
						.attr('height', 40)
						.attr('transform', 'translate(0,'+(height + 30)+')')
						.append('xhtml:div')
						.html('<span><b>Min </b><input type="text" name="minRange"></span> <span style="float:right;"><b>Max </b> <input type="text" name="maxRange"/></span>');

					svg.append('g').append('text')
						.attr('class', 'invalid-value-msg')
						.attr('x', width/2)
						.attr('y', height+75)
						.attr('text-anchor', 'middle')
						.attr('fill', 'red');

					var brushg = svg.append('g')
						.attr('transform', 'translate(0,' + (height - 10)+ ')')
						.attr('class', 'brush')
						.call(ctrl.brush);

					brushg.call(ctrl.brush.event)
						.transition().duration(400)
						.call(ctrl.brush.extent([minBrush, maxBrush]));

					brushg.selectAll('.resize').append('rect')
						.attr('transform', function(d,i){return i?'translate(-10, 0)':'translate(0,0)';})
						.attr('width',10);

					brushg.selectAll('rect')
						.attr('height', 20);

					brushg.select('.extent')
						.attr('height', 14)
						.attr('transform', 'translate(0,3)');

					ctrl.brush
						.on('brushstart', brushstart)
						.on('brush', brushmove)
						.on('brushend', brushend);

					function brushstart() {
						//on brush start code goes here
					}

					function brushmove() {
						var newExtent = fillInputs();
						if (ctrl.brush.empty()) {
							var exp = '1e-' + (nbDecimals + 1);
							svg.select('.brush').call(ctrl.brush.clear().extent([newExtent[0], newExtent[1] + Number(exp)]));
						}
					}

					function brushend() {
						//trigger filter process in the datagrid
						scope.onBrushEnd()(ctrl.brush.extent().map(function(n){return +n.toFixed(nbDecimals);}));
					}

					fillInputs();

					function fillInputs(){
						ctrl.hideMsgErr(d3.select('text.invalid-value-msg'));
						var s = ctrl.brush.extent();
						document.getElementsByName('minRange')[0].value = s[0] > 1e4 || s[0] < -1e4 ? d3.format('e')(s[0].toFixed(nbDecimals)) : s[0].toFixed(nbDecimals);
						document.getElementsByName('maxRange')[0].value = s[1] > 1e4 || s[1] < -1e4 ? d3.format('e')(s[1].toFixed(nbDecimals)) : s[1].toFixed(nbDecimals);
						return s;
					}

					function adjustBrush(){

						var enteredMax = ctrl.toNumber(document.getElementsByName('maxRange')[0].value);
						var enteredMin = ctrl.toNumber(document.getElementsByName('minRange')[0].value);

						if(enteredMax === null || enteredMin === null){
							return;
						}

						nbDecimals = d3.max([decimalPlaces(enteredMin), decimalPlaces(enteredMax)]);

						var finalExtent = [];

						if(enteredMax < enteredMin){
							var _aux = enteredMin;
							enteredMin = enteredMax;
							enteredMax = _aux;
						}

						if(enteredMax > maximum || enteredMax < minimum){
							enteredMax = maximum;
						}

						if(enteredMin > maximum || enteredMin < minimum){
							enteredMin = minimum;
						}

						if(enteredMin === enteredMax){
							var exp = '1e-' + (nbDecimals + 1);
							finalExtent = [enteredMin, enteredMin + Number(exp)];
						}
						else{
							finalExtent = [enteredMin, enteredMax];
						}
						brushg.transition()
							.call(ctrl.brush.extent(finalExtent));
						//should be after brush update
						fillInputs();
						//trigger filter process in the datagrid
						scope.onBrushEnd()(ctrl.brush.extent().map(function(n){return +n.toFixed(nbDecimals);}));
					}

					function decimalPlaces(num) {
						var match = (''+num).match(/(?:\.(\d+))?(?:[eE]([+-]?\d+))?$/);
						if (!match) { return 0; }
						return Math.max(
							0,
							// Number of digits right of decimal point.
							(match[1] ? match[1].length : 0) -
								// Adjust for scientific notation.
							(match[2] ? +match[2] : 0));
					}

					var minCorrectness, maxCorrectness;

					document.getElementsByName('minRange')[0].onblur = adjustBrush;
					document.getElementsByName('maxRange')[0].onblur = adjustBrush;

					document.getElementsByName('minRange')[0].onkeydown = function(e){
						e.stopPropagation();
					};
					document.getElementsByName('maxRange')[0].onkeydown = function(e){
						e.stopPropagation();
					};

					document.getElementsByName('minRange')[0].onkeyup = function(e){
						if(e.keyCode !== 13 && e.keyCode !== 27){
							minCorrectness = ctrl.toNumber(this.value);
							if(minCorrectness === null || maxCorrectness === null){
								var minMaxStr = document.getElementsByName('minRange')[0].value + document.getElementsByName('maxRange')[0].value;
								ctrl.showMsgErr(minMaxStr);
							}
							else{
								ctrl.hideMsgErr(d3.select('text.invalid-value-msg'));
							}
						}

						if(e.keyCode === 13 || e.keyCode === 9){
							if(minCorrectness !== null && maxCorrectness !== null){
								adjustBrush();
							}
							else{
								fillInputs();
								ctrl.hideMsgErr(d3.select('text.invalid-value-msg'));
							}
						}

						if(e.keyCode === 27){
							fillInputs();
						}
					};

					document.getElementsByName('maxRange')[0].onkeyup = function(e){
						if(e.keyCode !== 13 && e.keyCode !== 27){
							maxCorrectness = ctrl.toNumber(this.value);
							if(minCorrectness === null || maxCorrectness === null){
								var minMaxStr = document.getElementsByName('minRange')[0].value + document.getElementsByName('maxRange')[0].value;
								ctrl.showMsgErr(minMaxStr);
							}
							else{
								ctrl.hideMsgErr(d3.select('text.invalid-value-msg'));
							}
						}

						if(e.keyCode === 13 || e.keyCode === 9){
							if(minCorrectness !== null && maxCorrectness !== null){
								adjustBrush();
							}
							else{
								fillInputs();
								ctrl.hideMsgErr(d3.select('text.invalid-value-msg'));
							}
						}

						if(e.keyCode === 27){
							fillInputs();
						}
					};
				}

				scope.$watch('rangeLimits',
					function (rangeLimits) {
						element.empty();
						if ((rangeLimits) && (rangeLimits.min !== rangeLimits.max)) {
							clearTimeout(renderTimeout);
							renderTimeout = setTimeout(renderRangerSlider.bind(this, rangeLimits), 100);
						}
					});
			}
		};
	}

	angular.module('data-prep.rangeSlider')
		.directive('rangeSlider', RangeSlider);
})();