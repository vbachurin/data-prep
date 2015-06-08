describe('Filter search directive', function() {
    'use strict';
    
    var scope, createElement, element;

    beforeEach(module('data-prep.filter-list'));
    beforeEach(module('htmlTemplates'));

    beforeEach(inject(function($rootScope, $compile, $timeout) {
        scope = $rootScope.$new();
        createElement = function() {
            element = angular.element('<filter-list></filter-list>');
            $compile(element)(scope);
            $timeout.flush();
            scope.$digest();
        };
    }));
    
    afterEach(function() {
        scope.$destroy();
        element.remove();
    });

    it('should render filter list badges', inject(function(FilterService) {
        //given
        FilterService.addFilter('contains', '0001', 'col1', {phrase: 'toto'});
        FilterService.addFilter('contains', '0002', 'col2', {phrase: 'tata'});
        FilterService.addFilter('contains', '0003', 'col3', {phrase: 'titi'});

        //when
        createElement();

        //then
        expect(element.find('.badge-notice').length).toBe(3);
        expect(element.find('.badge-notice').eq(0).find('.text').text().trim()).toBe('col1');
        expect(element.find('.badge-notice').eq(0).find('.editable-input').val()).toBe('toto');
        expect(element.find('.badge-notice').eq(1).find('.text').text().trim()).toBe('col2');
        expect(element.find('.badge-notice').eq(1).find('.editable-input').val()).toBe('tata');
        expect(element.find('.badge-notice').eq(2).find('.text').text().trim()).toBe('col3');
        expect(element.find('.badge-notice').eq(2).find('.editable-input').val()).toBe('titi');
    }));

    it('should remove badge on close click', inject(function(FilterService) {
        //given
        FilterService.addFilter('contains', '0001', 'col1', {phrase: 'toto'});
        FilterService.addFilter('contains', '0002', 'col2', {phrase: 'tata'});
        FilterService.addFilter('contains', '0003', 'col3', {phrase: 'titi'});

        createElement();

        //when
        element.find('.badge-notice').eq(0).find('.badge-close').click();
        scope.$digest();

        //then
        expect(element.find('.badge-notice').length).toBe(2);
        expect(element.find('.badge-notice').eq(0).find('.badge-item').eq(0).text().trim()).toBe('col2');
        expect(element.find('.badge-notice').eq(1).find('.badge-item').eq(0).text().trim()).toBe('col3');
        expect(element.find('.badge-notice').eq(0).find('.editable-input').eq(0).val().trim()).toBe('tata');
        expect(element.find('.badge-notice').eq(1).find('.editable-input').eq(0).val().trim()).toBe('titi');
    }));
});