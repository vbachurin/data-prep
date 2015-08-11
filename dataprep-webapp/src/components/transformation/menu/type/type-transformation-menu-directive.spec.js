describe('Transformation menu directive', function () {
    'use strict';
    var scope, createElement, element, ctrl;

    var types = [
        {'id': 'ANY', 'name': 'any', 'labelKey': 'ANY'},
        {'id': 'STRING', 'name': 'string', 'labelKey': 'STRING'},
        {'id': 'NUMERIC', 'name': 'numeric', 'labelKey': 'NUMERIC'},
        {'id': 'INTEGER', 'name': 'integer', 'labelKey': 'INTEGER'},
        {'id': 'DOUBLE', 'name': 'double', 'labelKey': 'DOUBLE'},
        {'id': 'FLOAT', 'name': 'float', 'labelKey': 'FLOAT'},
        {'id': 'BOOLEAN', 'name': 'boolean', 'labelKey': 'BOOLEAN'},
        {'id': 'DATE', 'name': 'date', 'labelKey': 'DATE'}
    ];

    beforeEach(module('data-prep.transformation-menu'));
    beforeEach(module('htmlTemplates'));

    beforeEach(module('pascalprecht.translate', function ($translateProvider) {
        $translateProvider.translations('en', {
            'COLUMN_TYPE_IS': 'Column type is',
            'COLUMN_TYPE_SET': 'Set as'
        });
        $translateProvider.preferredLanguage('en');
    }));

    beforeEach(inject(function ($q, $rootScope, $compile, ColumnTypesService) {
        spyOn(ColumnTypesService, 'getTypes').and.returnValue($q.when(types));

        scope = $rootScope.$new();
        scope.column = {
            id: '0001',
            domain: 'CITY',
            domainLabel: 'CITY',
            domainFrequency: 18,
            type: 'string',
            semanticDomains: [
                {id: '', label: '', frequency: 15},
                {id: 'CITY', label: 'CITY', frequency: 18},
                {id: 'REGION', label: 'REGION', frequency: 6},
                {id: 'COUNTRY', label: 'COUNTRY', frequency: 17}
            ]
        };

        createElement = function () {
            element = angular.element('<type-transform-menu column="column"></type-transform-menu>');
            $compile(element)(scope);
            scope.$digest();

            ctrl = element.controller('typeTransformMenu');
            return element;
        };
    }));

    afterEach(function () {
        scope.$destroy();
        element.remove();
    });

    it('should display domain', function () {
        //when
        createElement();

        //then
        expect(element.find('>li >a').text().trim()).toBe('Column type is CITY');
    });

    it('should display simplified type when there is no domain', function () {
        //given
        scope.column.domain = '';
        scope.column.domainLabel = '';
        scope.column.domainCount = 0;

        //when
        createElement();

        //then
        expect(element.find('>li >a').text().trim()).toBe('Column type is text');
    });

    it('should render domain items with percentages', function () {
        //when
        createElement();

        //then
        var items = element.find('ul.submenu >li');
        expect(items.length).toBe(12);

        expect(items.eq(0).text().trim()).toBe('CITY 18 %');
        expect(items.eq(1).text().trim()).toBe('COUNTRY 17 %');
        expect(items.eq(2).text().trim()).toBe('REGION 6 %');

        expect(items.eq(3).hasClass('divider')).toBe(true);
    });

    it('should render primitive types', function () {
        //when
        createElement();

        //then
        var items = element.find('ul.submenu >li');
        expect(items.length).toBe(12);

        expect(items.eq(3).hasClass('divider')).toBe(true);

        expect(items.eq(4).text().trim()).toBe('Set as ANY');
        expect(items.eq(5).text().trim()).toBe('Set as STRING');
        expect(items.eq(6).text().trim()).toBe('Set as NUMERIC');
        expect(items.eq(7).text().trim()).toBe('Set as INTEGER');
        expect(items.eq(8).text().trim()).toBe('Set as DOUBLE');
        expect(items.eq(9).text().trim()).toBe('Set as FLOAT');
        expect(items.eq(10).text().trim()).toBe('Set as BOOLEAN');
        expect(items.eq(11).text().trim()).toBe('Set as DATE');
    });
});
