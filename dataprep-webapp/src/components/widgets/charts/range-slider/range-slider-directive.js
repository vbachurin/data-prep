(function () {
	'use strict';

	/**
	 * @ngdoc directive
	 * @name data-prep.rangeSlider.directive:rangeSlider
	 * @description This directive renders the rangeSlider.
	 * @restrict E
	 * @usage
	 *     <range-slider id="barChart"
	 *             width="250"
	 *             height="100"
	 *             range-limits="statsDetailsCtrl.rangeLimits"
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
			link: function (scope, element, attrs) {
				var h = attrs.height;
				var w = attrs.width;
				var container = attrs.id;
				var renderTimeout;

				function renderRangerSlider(rangeLimits){
					var minimum = rangeLimits.min;
					var maximum = rangeLimits.max;
					var minBrush = typeof rangeLimits.minBrush !== 'undefined' ? rangeLimits.minBrush : minimum;
					var maxBrush = typeof rangeLimits.maxBrush !== 'undefined' ? rangeLimits.maxBrush : maximum;

					var toNumber = function toNumber (value) {
						if(/^[-+]?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?$/.test(value)){
							return Number(value.trim());
						}
						return null;
					};

					var checkCommaExistence = function checkCommaExistence (){
						var minAndMax = document.getElementsByName('minRange')[0].value + document.getElementsByName('maxRange')[0].value;
						return minAndMax.indexOf(',') !== -1 ? true : false;
					};

					var showMsgErr = function showMsgErr(){
						var msgErr = 'Invalid Entered Value';
						var finalMsgErr = checkCommaExistence()? msgErr + ': Use "." instead of ","' : msgErr;
						d3.select('text.invalid-value-msg')
							.text(finalMsgErr);
					};

					var hideMsgErr = function hideMsgErr(){
						d3.select('text.invalid-value-msg')
							.text('');
					};

					var nbDecimals = d3.max([decimalPlaces(minBrush), decimalPlaces(maxBrush)]);
					var margin = {top: 25, right: 25, bottom: 80, left: 10},
						width = w - margin.left - margin.right,
						height = h - margin.top - margin.bottom;

					var x = d3.scale.linear()
						.domain([minimum, maximum])
						.range([0, width]);

					var centerValue = (minBrush + maxBrush)/2;

					var brush = d3.svg.brush()
						.x(x)
						.extent([centerValue, centerValue])
						.on('brushstart', brushstart)
						.on('brush', brushmove)
						.on('brushend', brushend);

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
						.call(brush);

					brushg.call(brush.event)
						.transition().duration(400)
						.call(brush.extent([minBrush, maxBrush]))
						.call(brush.event);

					brushg.selectAll('.resize').append('rect')
						.attr('transform', function(d,i){return i?'translate(-10, 0)':'translate(0,0)';})
						.attr('width',10);

					brushg.selectAll('rect')
						.attr('height', 20);

					brushg.select('.extent')
						.attr('height', 14)
						.attr('transform', 'translate(0,3)');


					function brushstart() {
						//on brush start code goes here
					}

					function brushmove() {
						var newExtent = fillInputs();
						if (brush.empty()) {
							var exp = '1e-' + (nbDecimals + 1);
							svg.select('.brush').call(brush.clear().extent([newExtent[0], newExtent[1] + Number(exp)]));
						}
					}

					function brushend() {
						// only transition after input
						if (!d3.event.sourceEvent) {
							return;
						}
						var extent1 = brush.extent();

						d3.select(this).transition().duration(400)
							.call(brush.extent(extent1));
						//trigger filter process in the datagrid
						scope.onBrushEnd()(brush.extent().map(function(n){return +n.toFixed(nbDecimals);}));
					}

					fillInputs();

					function fillInputs(){
						hideMsgErr();
						var s = brush.extent();
						document.getElementsByName('minRange')[0].value = s[0] > 1e4 || s[0] < -1e4 ? d3.format('e')(s[0].toFixed(nbDecimals)) : s[0].toFixed(nbDecimals);
						document.getElementsByName('maxRange')[0].value = s[1] > 1e4 || s[1] < -1e4 ? d3.format('e')(s[1].toFixed(nbDecimals)) : s[1].toFixed(nbDecimals);
						return s;
					}

					function adjustBrush(){

						var enteredMax = toNumber(document.getElementsByName('maxRange')[0].value);
						var enteredMin = toNumber(document.getElementsByName('minRange')[0].value);

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
							.call(brush.extent(finalExtent));
						//should be after brush update
						fillInputs();
						//trigger filter process in the datagrid
						scope.onBrushEnd()(brush.extent().map(function(n){return +n.toFixed(nbDecimals);}));
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
						if(e.which !== 13 && e.which !== 27){
							minCorrectness = toNumber(this.value);
							if(minCorrectness === null || maxCorrectness === null){
								showMsgErr();
							}
							else{
								hideMsgErr();
							}
						}

						if(e.which === 13 || e.which === 9){
							if(minCorrectness !== null && maxCorrectness !== null){
								adjustBrush();
							}
							else{
								fillInputs();
								hideMsgErr();
							}
						}

						if(e.which === 27){
							fillInputs();
						}
					};

					document.getElementsByName('maxRange')[0].onkeyup = function(e){
						if(e.which !== 13 && e.which !== 27){
							maxCorrectness = toNumber(this.value);
							if(minCorrectness === null || maxCorrectness === null){
								showMsgErr();
							}
							else{
								hideMsgErr();
							}
						}

						if(e.which === 13 || e.which === 9){
							if(minCorrectness !== null && maxCorrectness !== null){
								adjustBrush();
							}
							else{
								fillInputs();
								hideMsgErr();
							}
						}

						if(e.which === 27){
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