describe('Dataset grid directive', function() {
    'use strict';
    var scope, createElement;

    beforeEach(module('data-prep-dataset'));
    beforeEach(module('htmlTemplates'));

    beforeEach(inject(function($rootScope, $compile) {
        scope = $rootScope.$new();
        createElement = function(directiveScope) {
            var element = angular.element('<dataset-grid column="column"></dataset-grid>');
            $compile(element)(directiveScope);
            directiveScope.$digest();
            return element;
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
        var element = createElement(scope);
        //var ctrl = element.controller;

        //then
        expect(scope.column.total).toBe(87);
        expect(scope.column.quality.emptyPercent).toBe(6);
        expect(scope.column.quality.emptyPercentWidth).toBe(10);
        expect(scope.column.quality.invalidPercent).toBe(12);
        expect(scope.column.quality.invalidPercentWidth).toBe(12);
        expect(scope.column.quality.validPercent).toBe(82);
        expect(scope.column.quality.validPercentWidth).toBe(78);
    });

    it('should display colum title, type and set quality bars width', function() {
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
        var element = createElement(scope);

        //then
        expect(element.find('.grid-header-title').text()).toBe('MostPopulousCity');
        expect(element.find('.grid-header-type').text()).toBe('string');
        expect(element.find('.record-ok').css('width')).toBe('78%');
        expect(element.find('.record-empty').css('width')).toBe('10%');
        expect(element.find('.record-nok').css('width')).toBe('12%');
    });
    
});