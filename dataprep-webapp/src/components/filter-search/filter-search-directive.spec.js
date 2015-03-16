describe('Filter search directive', function() {
    'use strict';
    
    var scope, createElement;

    beforeEach(module('data-prep.filter-search'));
    beforeEach(module('htmlTemplates'));

    beforeEach(inject(function($rootScope, $compile) {
        scope = $rootScope.$new();
        createElement = function() {
            var element = angular.element('<filter-search></filter-search>');
            $compile(element)(scope);
            scope.$digest();
            return element;
        };
    }));

    it('should render input with auto-complete', function() {
        //when
        var element = createElement();

        //then
        expect(element.find('div[mass-autocomplete]').length).toBe(1);
        expect(element.find('input[type="search"]').length).toBe(1);
    });
});