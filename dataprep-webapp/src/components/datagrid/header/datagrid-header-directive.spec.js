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
    
    it('should display column title, type, domain and set quality bars width', function() {
        //given
        scope.column = {
            'id': '0',
            'name': 'MostPopulousCity',
            'quality': {
                'empty': 5,
                'invalid': 10,
                'valid': 72
            },
            'type': 'string',
            'domain': 'city'
        };

        //when
        createElement(scope);

        //then
        expect(element.find('.grid-header-title').text()).toBe('MostPopulousCity');
        expect(element.find('.grid-header-type').text()).toBe('string');
        expect(element.find('.grid-header-domain').text()).toBe('city');
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