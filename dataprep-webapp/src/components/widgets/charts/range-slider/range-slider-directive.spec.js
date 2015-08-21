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
			scope.spy = jasmine.createSpy('spy');

			element = angular.element('<range-slider id="barChart"'+
				'width="250"'+
				'height="100"'+
				'range-limits = "rangeLimits"'+
				'on-brush-end = "spy"'+
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

	it('should calculate the right positions of the brush handlers compared to the rangeSlider limits when there is a brush', function () {
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

	it('should set the new typed range manually and submit with Enter', function () {
		//given
		createElement();

		//when
		scope.rangeLimits = {
			min:0,
			max:20,
			minBrush:5,
			maxBrush:15
		};
		scope.$digest();
		jasmine.clock().tick(100);

		var ctrl = element.controller('rangeSlider');
		//when
		element.find('input').eq(1)[0].value = 10;
		element.find('input').eq(0)[0].value = 8;
		var event2 = new angular.element.Event('keyup');
		event2.keyCode = 13;
		element.find('input').eq(0).trigger(event2);
		//then
		expect(ctrl.brush.extent()).toEqual([8,10]);
	});

	it('should set the new typed range manually and submit with Blur', function () {
		//given
		createElement();

		//when
		scope.rangeLimits = {
			min:0,
			max:20,
			minBrush:5,
			maxBrush:15
		};
		scope.$digest();
		jasmine.clock().tick(100);

		var ctrl = element.controller('rangeSlider');

		//when
		element.find('input').eq(0)[0].value = 7;
		var event2 = new angular.element.Event('blur');
		element.find('input').eq(0).trigger(event2);

		//then
		expect(ctrl.brush.extent()).toEqual([7,15]);
	});

	it('should cancel the new typed range manually and submit with Esc', function () {
		//given
		createElement();

		//when
		scope.rangeLimits = {
			min:0,
			max:20,
			minBrush:5,
			maxBrush:15
		};
		scope.$digest();
		jasmine.clock().tick(100);

		var ctrl = element.controller('rangeSlider');
		//when
		element.find('input').eq(0)[0].value = 7;
		var event2 = new angular.element.Event('keyup');
		event2.keyCode = 27;
		element.find('input').eq(0).trigger(event2);
		//then
		expect(ctrl.brush.extent()).toEqual([5,15]);
		expect(element.find('input').eq(0)[0].value).toBe('5');
	});

	it('should cancel the new incorrect typed range and gets back to the initial value after Enter Hit and hide the error message', function () {
		//given
		createElement();

		//when
		scope.rangeLimits = {
			min:0,
			max:20,
			minBrush:5,
			maxBrush:15
		};
		scope.$digest();
		jasmine.clock().tick(100);

		var ctrl = element.controller('rangeSlider');
		//when
		element.find('input').eq(0)[0].value = 'kjhfkjfkl';
		var event2 = new angular.element.Event('keyup');
		event2.keyCode = 13;
		element.find('input').eq(0).trigger(event2);
		//then
		expect(ctrl.brush.extent()).toEqual([5,15]);
		expect(element.find('text.invalid-value-msg').eq(0).text()).toBe('');//'Invalid Entered Value'
		//expect(element.find('input').eq(0)[0].value).toBe('5');
	});

	it('should show error message', function () {
		//given
		createElement();

		//when
		scope.rangeLimits = {
			min:0,
			max:20,
			minBrush:5,
			maxBrush:15
		};
		scope.$digest();
		jasmine.clock().tick(100);

		var ctrl = element.controller('rangeSlider');
		//when
		element.find('input').eq(0)[0].value = 'kjhfkjfkl';
		var event2 = new angular.element.Event('keyup');
		event2.keyCode = 104;//8
		element.find('input').eq(0).trigger(event2);
		//then
		expect(ctrl.brush.extent()).toEqual([5,15]);
		expect(element.find('text.invalid-value-msg').eq(0).text()).toBe('Invalid Entered Value');
	});

	it('should check if the filter was propagated to the StatsDetailsCtrl controller', function () {
		//given
		createElement();

		//when
		scope.rangeLimits = {
			min:0,
			max:20,
			minBrush:5,
			maxBrush:15
		};
		scope.$digest();
		jasmine.clock().tick(100);

		//when
		element.find('input').eq(0)[0].value = 7;
		var event2 = new angular.element.Event('blur');
		event2.keyCode = 13;
		element.find('input').eq(0).trigger(event2);

		//then
		expect(scope.spy).toHaveBeenCalledWith([7,15]);
	});
});
