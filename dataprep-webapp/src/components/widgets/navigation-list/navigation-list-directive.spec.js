describe('navigationList directive', function() {
	'use strict';

	var scope, createElement, element;

	var list = [
		{
			'category': 'data_blending',
			'name': 'lookup',
			'parameters': [
				{
					'name': 'column_id',
					'type': 'string',
					'default': ''
				},
				{
					'name': 'filter',
					'type': 'filter',
					'default': ''
				},
				{
					'name': 'lookup_ds_name',
					'type': 'string',
					'default': 'us-customers-500'
				}
			],
			'label': 'Lookup',
			'actionScope': [],
			'description': 'Blends columns from another dataset into this one',
			'dynamic': false
		},
		{
			'category': 'data_blending',
			'name': 'lookup',
			'parameters': [
				{
					'name': 'column_id',
					'type': 'string',
					'implicit': true,
					'canBeBlank': true,
					'label': 'Column',
					'description': 'The column on which apply this action to',
					'default': ''
				},
				{
					'name': 'filter',
					'type': 'filter',
					'default': ''
				},
				{
					'name': 'lookup_ds_name',
					'type': 'string',
					'default': 'dates'
				}
			],
			'label': 'Lookup',
			'actionScope': [],
			'description': 'Blends columns from another dataset into this one',
			'dynamic': false
		},
		{
			'category': 'data_blending',
			'name': 'lookup',
			'parameters': [
				{
					'name': 'column_id',
					'type': 'string',
					'default': ''
				},
				{
					'name': 'filter',
					'type': 'filter',
					'default': ''
				},
				{
					'name': 'lookup_ds_name',
					'type': 'string',
					'default': 'exponnetial'
				}
			],
			'label': 'Lookup',
			'actionScope': [],
			'description': 'Blends columns from another dataset into this one',
			'dynamic': false
		}
	];

	beforeEach(module('talend.widget'));
	beforeEach(module('htmlTemplates'));

	beforeEach(inject(function($rootScope, $compile) {
		scope = $rootScope.$new();
		createElement = function() {
			element = angular.element('<navigation-list ' +
										'list="list"' +
										'on-click="trigger(item)"' +
										'selected-item="item"' +
										'get-label="getLabelCb(item)"' +
										'nbre-labels-to-show="2"></navigation-list>'
									);
			scope.list = list;
			scope.item = list[0];
			scope.clickCallbackCalled = false;
			scope.getLabelCb = function(item){
				return item.parameters[2].default;
			};

			$compile(element)(scope);
			scope.$digest();
		};
	}));

	afterEach(function() {
		scope.$destroy();
		element.remove();
	});

	it('should render items list', function() {
		//when
		createElement();

		//then
		expect(element.find('.navigation-list').length).toBe(1);
		expect(element.find('button').length).toBe(2);
		expect(element.find('button').eq(0).attr('disabled')).toBe('disabled');
		expect(element.find('button').eq(1).attr('disabled')).toBe(undefined);
		expect(element.find('.items-list').length).toBe(1);
		expect(element.find('.item-label').length).toBe(2);
		expect(element.find('.item-label').eq(0).text().trim()).toBe('us-customers-500');
		expect(element.find('.item-label').eq(1).text().trim()).toBe('dates');
		expect(element.find('.selected-item-label').length).toBe(1);
		expect(element.find('.selected-item-label').eq(0).text().trim()).toBe('us-customers-500');
	});

	it('should trigger item selection callback', function() {
		//given
		scope.clickCbCalled = false;
		scope.trigger = function(){
			scope.clickCbCalled = true;
		};
		createElement();

		//when
		element.find('.item-label').eq(1).click();

		//then
		expect(scope.clickCbCalled).toBeTruthy();
	});

	it('should trigger item selection callback', function() {
		//given
		scope.clickCbCalled = false;
		scope.trigger = function(){
			scope.clickCbCalled = true;
		};
		createElement();

		//when
		element.find('.item-label').eq(1).click();

		//then
		expect(scope.clickCbCalled).toBeTruthy();
	});

	it('should show 2nd and 3rd items of the list on forth button click', function() {
		//given
		createElement();
		expect(element.find('.item-label').eq(0).text().trim()).toBe('us-customers-500');
		expect(element.find('.item-label').eq(1).text().trim()).toBe('dates');

		//when
		element.find('button').eq(1).click();
		scope.$digest();

		//then
		expect(element.find('.item-label').eq(0).text().trim()).toBe('dates');
		expect(element.find('.item-label').eq(1).text().trim()).toBe('exponnetial');
	});

	it('should show 1st and 2nd items of the list on back button click', function() {
		//given
		createElement();

		element.find('button').eq(1).click();
		scope.$digest();
		expect(element.find('.item-label').eq(0).text().trim()).toBe('dates');
		expect(element.find('.item-label').eq(1).text().trim()).toBe('exponnetial');
		expect(element.find('button').eq(0).attr('disabled')).toBe(undefined);

		//when
		element.find('button').eq(0).click();
		scope.$digest();

		//then
		expect(element.find('.item-label').eq(0).text().trim()).toBe('us-customers-500');
		expect(element.find('.item-label').eq(1).text().trim()).toBe('dates');
		expect(element.find('button').eq(0).attr('disabled')).toBe('disabled');
	});

	it('should deactivate forth btn after forth button has been clicked as the last item is shown', function() {
		//given
		createElement();
		expect(element.find('button').eq(1).attr('disabled')).toBe(undefined);

		//when
		element.find('button').eq(1).click();
		scope.$digest();

		//then
		expect(element.find('button').eq(1).attr('disabled')).toBe('disabled');
	});
});