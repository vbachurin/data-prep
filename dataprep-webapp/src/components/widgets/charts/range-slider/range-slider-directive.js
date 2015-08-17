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

				function renderRangerSlider(rangeLimits){
					var minimum = rangeLimits.min;
					var maximum = rangeLimits.max;
					var minBrush = rangeLimits.minBrush || minimum;
					var maxBrush = rangeLimits.maxBrush || maximum;

					var filterFloat = function (value) {
						if(/^(\-|\+)?([0-9]+(\.[0-9]+)?|Infinity)$/
								.test(value)){
							//d3.select('.invalid-value-msg').style('display', 'none');
							return Number(value.trim());
						}
						return 'Invalid Entered Value';
					};

					var nbDecimals = 2;
					var margin = {top: 25, right: 25, bottom: 80, left: 10},
						width = w - margin.left - margin.right,
						height = h - margin.top - margin.bottom;

					var x = d3.scale.linear()
						.domain([minimum, maximum])
						.range([0, width]);

					var brush = d3.svg.brush()
						.x(x)
						.extent([minBrush, maxBrush])
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
						.call(d3.svg.axis().scale(x).orient('bottom').ticks(Math.abs(x.range()[1] - x.range()[0]) / 50));

					xAxisg.selectAll('text').transition().duration(1000).attr('y', 13);

					var brushg = svg.append('g')
						.attr('transform', 'translate(0,' + (height - 10)+ ')')
						.attr('class', 'brush')
						.call(brush);

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
						.text('Invalid Entered Value')
						.attr('fill', 'red')
						.style('display', 'none');

					brushg.selectAll('.resize').append('rect')
						.attr('transform', function(d,i){return i?'translate(-10, 0)':'translate(0,0)';})
						.attr('width',10);

					brushg.selectAll('rect')
						.attr('height', 20);

					brushg.select('.extent')
						.attr('height', 14)
						.attr('transform', 'translate(0,3)');


					function brushstart() {

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

						scope.onBrushEnd()(brush.extent().map(function(n){return +n.toFixed(nbDecimals);}));
					}

					fillInputs();

					function fillInputs(){
						d3.select('.invalid-value-msg').style('display', 'none');
						var s = brush.extent();
						document.getElementsByName('minRange')[0].value=s[0].toFixed(nbDecimals);
						document.getElementsByName('maxRange')[0].value=s[1].toFixed(nbDecimals);
						console.log(s);
						return s;
					}

					function adjustBrush(){

						var enteredMax = filterFloat(document.getElementsByName('maxRange')[0].value);
						var enteredMin = filterFloat(document.getElementsByName('minRange')[0].value);

						if(typeof enteredMax !== 'number' || typeof enteredMin !== 'number'){
							return;
						}

						nbDecimals = d3.max([decimalPlaces(enteredMin), decimalPlaces(enteredMax)]) || 2;

						var finalExtent = [];
						//will be replaced directly by the min

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

					var minCorrectness = 1;
					var maxCorrectness = 1;

					document.getElementsByName('minRange')[0].onkeyup = function(e){
						if(e.which !== 13 && e.which !== 27){
							minCorrectness = filterFloat(this.value);
							if(typeof minCorrectness !== 'number' || typeof maxCorrectness !== 'number'){
								d3.select('.invalid-value-msg').style('display', 'block');
							}
							else{
								d3.select('.invalid-value-msg').style('display', 'none');
							}
						}
						if(e.which === 13){
							if(typeof minCorrectness === 'number' && typeof maxCorrectness === 'number'){
								adjustBrush();
							}
							else{
								fillInputs();
								d3.select('.invalid-value-msg').style('display', 'none');
							}
						}
						if(e.which === 27){
							fillInputs();
						}
					};

					document.getElementsByName('maxRange')[0].onkeyup = function(e){
						if(e.which !== 13 && e.which !== 27){
							maxCorrectness = filterFloat(this.value);
							if(typeof minCorrectness !== 'number' || typeof maxCorrectness !== 'number'){
								d3.select('.invalid-value-msg').style('display', 'block');
							}
							else{
								d3.select('.invalid-value-msg').style('display', 'none');
							}
						}
						if(e.which === 13){
							if(typeof minCorrectness === 'number' && typeof maxCorrectness === 'number'){
								adjustBrush();
							}
							else{
								fillInputs();
								d3.select('.invalid-value-msg').style('display', 'none');
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
						if (rangeLimits) {
							renderRangerSlider(rangeLimits);
							//clearTimeout(renderTimeout);
							//renderTimeout = setTimeout(renderRangerSlider.bind(this, rangeLimits), 100);
						}
					});
			}
		};
	}

	angular.module('data-prep.rangeSlider')
		.directive('rangeSlider', RangeSlider);
})();