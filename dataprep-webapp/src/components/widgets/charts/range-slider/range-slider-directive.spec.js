describe('rangeSlider directive', function () {
	'use strict';

	var createElement, element, scope, rangeLimits;

	beforeEach(module('data-prep.rangeSlider'));
	beforeEach(inject(function ($rootScope, $compile) {
		rangeLimits = {min:0,
			max:20,
			minBrush:5,
			maxBrush:15
		};

		createElement = function () {

			scope = $rootScope.$new();
			scope.rangeLimits = null;
			scope.onBrushEnd = function(interval){
				console.log(interval);
			};

			element = angular.element('<range-slider id="barChart"'+
				'width="250"'+
				'height="100"'+
				'range-limits = "rangeLimits"'+
				'on-brush-end = "onBrushEnd"'+
				'id="domId"'+
				'></range-slider>');

			angular.element('body').append(element);
			$compile(element)(scope);
			scope.$digest();
		};
	}));

	beforeEach(function () {
		jasmine.clock().install();
	});
	afterEach(function () {
		jasmine.clock().uninstall();

		scope.$destroy();
		element.remove();
	});

	it('should render the rangeSlider after a 100ms delay', function () {
		//given
		createElement();

		//when
		scope.rangeLimits = rangeLimits;
		scope.$digest();
		jasmine.clock().tick(100);

		//then
		expect(element.find('rect').length).toBe(6);
		expect(element.find('input').length).toBe(2);
		expect(element.find('.range-slider-cls').length).toBe(1);
		expect(element.find('.resize').length).toBe(2);
		expect(element.find('.extent').length).toBe(1);

		//given
		//distance between the rangeSlider limits and the brush position
		var x = d3.scale.linear()
			.domain([scope.rangeLimits.min, scope.rangeLimits.max])
			.range([0, 250]);

		var minPixelsDiff = x(scope.rangeLimits.minBrush) - x(scope.rangeLimits.min);
		expect(minPixelsDiff).toBe(x(scope.rangeLimits.minBrush - scope.rangeLimits.min));
	});

	it('should calculate the right positions of the brush handlers compared to the rangeSlider limits when there is NO brush', function () {
		//given
		createElement();
		var margins= {
			left: 25,
			right:10
		};

		//when
		scope.rangeLimits = {
			min:0,
			max:20,
			minBrush:undefined,
			maxBrush:undefined
		};
		scope.$digest();
		jasmine.clock().tick(100);

		var flushAllD3Transitions = function() {
			var now = Date.now;
			Date.now = function() { return Infinity; };
			d3.timer.flush();
			Date.now = now;
		};
		flushAllD3Transitions();

		var x = d3.scale.linear()
			.domain([scope.rangeLimits.min, scope.rangeLimits.max])
			.range([0, (250-(margins.left+margins.right))]);

		//then
		//width of the brush extent is all the range size
		expect(d3.select('.extent').attr('width')).toBe(''+(x(scope.rangeLimits.max) - x(scope.rangeLimits.min)));
		expect(d3.select('.extent').attr('x')).toBe(''+x(scope.rangeLimits.min));
	});

	it('should calculate the right positions of the brush handlers compared to the rangeSlider limits when there is NO brush', function () {
		//given
		createElement();
		var margins= {
			left: 25,
			right:10
		};

		//when
		scope.rangeLimits = {
			min:0,
			max:20,
			minBrush:5,
			maxBrush:15
		};
		scope.$digest();
		jasmine.clock().tick(100);

		var flushAllD3Transitions = function() {
			var now = Date.now;
			Date.now = function() { return Infinity; };
			d3.timer.flush();
			Date.now = now;
		};
		flushAllD3Transitions();

		var x = d3.scale.linear()
			.domain([scope.rangeLimits.min, scope.rangeLimits.max])
			.range([0, (250-(margins.left+margins.right))]);

		//then
		//width of the brush extent is between[5...15]
		expect(d3.select('.extent').attr('width')).toBe(''+(x(scope.rangeLimits.maxBrush) - x(scope.rangeLimits.minBrush)));
		expect(d3.select('.extent').attr('x')).toBe(''+x(scope.rangeLimits.minBrush));

		var brush = d3.svg.brush()
			.x(x)
			.extent([6, 10]);

		d3.select('.brush').call(brush.event)
			.call(brush.extent([0, 20]));
		expect(d3.select('.extent').attr('width')).toBe(''+(x(scope.rangeLimits.max) - x(scope.rangeLimits.min)));
	});

	it('should make a brush action programatically', function () {
		//given
		createElement();
		var margins= {
			left: 25,
			right:10
		};

		//when
		scope.rangeLimits = {
			min:0,
			max:20,
			minBrush:5,
			maxBrush:15
		};
		scope.$digest();
		jasmine.clock().tick(100);

		var flushAllD3Transitions = function() {
			var now = Date.now;
			Date.now = function() { return Infinity; };
			d3.timer.flush();
			Date.now = now;
		};
		flushAllD3Transitions();

		var x = d3.scale.linear()
			.domain([scope.rangeLimits.min, scope.rangeLimits.max])
			.range([0, (250-(margins.left+margins.right))]);

		var ctrl = element.controller('rangeSlider');
		//make a brush programatically and set the extent to [0, 20]
		d3.select('.brush').call(ctrl.brush.event)
			.call(ctrl.brush.extent([0, 20]))
			.call(ctrl.brush.event);

		//then
		expect(d3.select('.extent').attr('width')).toBe(''+(x(scope.rangeLimits.max) - x(scope.rangeLimits.min)));
		expect(document.getElementsByName('minRange')[0].value).toBe('0');
		expect(document.getElementsByName('maxRange')[0].value).toBe('20');
	});

	it('should set a new range manually', function () {
		//given
		createElement();
		var margins= {
			left: 25,
			right:10
		};

		//when
		scope.rangeLimits = {
			min:0,
			max:20,
			minBrush:5,
			maxBrush:15
		};
		scope.$digest();
		jasmine.clock().tick(100);

		var flushAllD3Transitions = function() {
			var now = Date.now;
			Date.now = function() { return Infinity; };
			d3.timer.flush();
			Date.now = now;
		};
		flushAllD3Transitions();

		var x = d3.scale.linear()
			.domain([scope.rangeLimits.min, scope.rangeLimits.max])
			.range([0, (250-(margins.left+margins.right))]);

		var ctrl = element.controller('rangeSlider');
		//expect(d3.select('.extent').attr('width')).toBe(''+(x(scope.rangeLimits.max) - x(scope.rangeLimits.min)));

		//then

		//var event = new angular.element.Event('keyup');
		//event.keyCode = 104;//8
		////when
		//element.find('input').eq(0).trigger(event);
		element.find('input').eq(0)[0].value = 8;
		var event2 = new angular.element.Event('keyup');
		event2.keyCode = 13;
		//when
		element.find('input').eq(0).trigger(event2);
		console.log(ctrl.brush.extent(),"+++++++++");
	});
});