describe('Actions suggestions-stats directive', function() {
    'use strict';

    var scope, element, createElement;

    beforeEach(module('data-prep.actions-suggestions'));
    beforeEach(module('htmlTemplates'));
    
    beforeEach(inject(function($rootScope, $compile) {
        scope = $rootScope.$new();

        createElement = function() {
            scope = $rootScope.$new();
            element = angular.element('<actions-suggestions></actions-suggestions>');
            $compile(element)(scope);
            scope.$digest();
        };
    }));

    afterEach(function() {
        scope.$destroy();
        element.remove();
    });

    it('should set "Action" in title when no column is selected', inject(function(SuggestionService) {
        //given
        SuggestionService.currentColumn = null;

        //when
        createElement();

        //then
        expect(element.find('.title').text().trim()).toBe('Actions');
    }));

    it('should set column name in title', inject(function(SuggestionService) {
        //given
        SuggestionService.currentColumn = {name: 'Col 1'};

        //when
        createElement();

        //then
        expect(element.find('.title').text().trim()).toBe('Actions: Col 1');
    }));
});
