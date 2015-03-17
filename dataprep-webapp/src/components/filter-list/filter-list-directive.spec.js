describe('Filter search directive', function() {
    'use strict';
    
    var scope, createElement;

    beforeEach(module('data-prep.filter-list'));
    beforeEach(module('htmlTemplates'));

    beforeEach(inject(function($rootScope, $compile) {
        scope = $rootScope.$new();
        createElement = function() {
            var element = angular.element('<filter-list></filter-list>');
            $compile(element)(scope);
            scope.$digest();
            return element;
        };
    }));

    it('should render filter list badges', inject(function(FilterService) {
        //given
        FilterService.addFilter('contains', 'col1', {phrase: 'toto'});
        FilterService.addFilter('contains', 'col2', {phrase: 'tata'});
        FilterService.addFilter('contains', 'col3', {phrase: 'titi'});

        //when
        var element = createElement();

        //then
        expect(element.find('.badge-notice').length).toBe(3);
        expect(element.find('.badge-notice').eq(0).text().trim().replace(/\s+/g, ' ')).toBe('COL1: toto x');
        expect(element.find('.badge-notice').eq(1).text().trim().replace(/\s+/g, ' ')).toBe('COL2: tata x');
        expect(element.find('.badge-notice').eq(2).text().trim().replace(/\s+/g, ' ')).toBe('COL3: titi x');
    }));

    it('should remove badge on close click', inject(function(FilterService) {
        //given
        FilterService.addFilter('contains', 'col1', {phrase: 'toto'});
        FilterService.addFilter('contains', 'col2', {phrase: 'tata'});
        FilterService.addFilter('contains', 'col3', {phrase: 'titi'});

        var element = createElement();

        //when
        element.find('.badge-notice').eq(0).find('.badge-close').click();
        scope.$digest();

        //then
        expect(element.find('.badge-notice').length).toBe(2);
        expect(element.find('.badge-notice').eq(0).text().trim().replace(/\s+/g, ' ')).toBe('COL2: tata x');
        expect(element.find('.badge-notice').eq(1).text().trim().replace(/\s+/g, ' ')).toBe('COL3: titi x');
    }));
});