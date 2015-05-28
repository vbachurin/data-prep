describe('Dataset column header directive', function() {
    'use strict';
    var scope, createElement, element;
    
    beforeEach(module('data-prep.datagrid-header'));
    beforeEach(module('htmlTemplates'));

    afterEach(function() {
        scope.$destroy();
        element.remove();
    });

    beforeEach(inject(function($rootScope, $compile, $timeout) {
        scope = $rootScope.$new(true);
        createElement = function(directiveScope) {
            element = angular.element('<datagrid-header column="column"></datagrid-header>');
            $compile(element)(directiveScope);
            directiveScope.$digest();
            $timeout.flush();
        };
    }));
    
    it('should calculate column quality', function() {
        //given
        scope.column = {
            'id': 'MostPopulousCity',
            'quality': {
                'empty': 5,
                'invalid': 10,
                'valid': 72
            },
            'type': 'string'
        };

        //when
        createElement(scope);

        //then
        expect(scope.column.total).toBe(87);
        expect(scope.column.quality.emptyPercent).toBe(6);
        expect(scope.column.quality.emptyPercentWidth).toBe(10);
        expect(scope.column.quality.invalidPercent).toBe(12);
        expect(scope.column.quality.invalidPercentWidth).toBe(12);
        expect(scope.column.quality.validPercent).toBe(82);
        expect(scope.column.quality.validPercentWidth).toBe(78);
    });

    it('should calculate column quality with 0 values', function() {
        //given
        scope.column = {
            'id': 'MostPopulousCity',
            'quality': {
                'empty': 0,
                'invalid': 0,
                'valid': 100
            },
            'type': 'string'
        };

        //when
        createElement(scope);

        //then
        expect(scope.column.total).toBe(100);
        expect(scope.column.quality.emptyPercent).toBe(0);
        expect(scope.column.quality.emptyPercentWidth).toBe(0);
        expect(scope.column.quality.invalidPercent).toBe(0);
        expect(scope.column.quality.invalidPercentWidth).toBe(0);
        expect(scope.column.quality.validPercent).toBe(100);
        expect(scope.column.quality.validPercentWidth).toBe(100);
    });

    it('should display colum title, type and set quality bars width', function() {
        //given
        scope.column = {
            'id': '0',
            'name': 'MostPopulousCity',
            'quality': {
                'empty': 5,
                'invalid': 10,
                'valid': 72
            },
            'type': 'string'
        };

        //when
        createElement(scope);

        //then
        expect(element.find('.grid-header-title').text()).toBe('MostPopulousCity');
        expect(element.find('.grid-header-type').text()).toBe('string');
        expect(element.find('.record-ok').css('width')).toBe('78%');
        expect(element.find('.record-empty').css('width')).toBe('10%');
        expect(element.find('.record-nok').css('width')).toBe('12%');
    });

    it('should display column title, type and set quality bars width', function() {
        //given
        scope.column = {
            'id': '0',
            'name': 'MostPopulousCity',
            'quality': {
                'empty': 5,
                'invalid': 10,
                'valid': 72
            },
            'type': 'string'
        };

        //when
        createElement(scope);

        //then
        expect(element.find('.grid-header-title').text()).toBe('MostPopulousCity');
        expect(element.find('.grid-header-type').text()).toBe('string');
        expect(element.find('.record-ok').css('width')).toBe('78%');
        expect(element.find('.record-empty').css('width')).toBe('10%');
        expect(element.find('.record-nok').css('width')).toBe('12%');
    });

    it('should close dropdown on get transform list error', function() {
        //given
        scope.column = {
            'id': 'MostPopulousCity',
            'quality': {
                'empty': 5,
                'invalid': 10,
                'valid': 72
            },
            'type': 'string'
        };

        createElement(scope);
        var menu = element.find('.grid-header-menu').eq(0);
        menu.addClass('show-menu');

        //when
        element.controller('datagridHeader').transformationsRetrieveError = true;
        scope.$apply();

        //then
        expect(menu.hasClass('show-menu')).toBe(false);
    });
});