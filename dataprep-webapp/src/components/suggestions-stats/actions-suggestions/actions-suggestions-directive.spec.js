describe('Actions suggestions-stats directive', function() {
    'use strict';

    var scope, element, createElement;
    var body = angular.element('body');
    beforeEach(module('data-prep.actions-suggestions'));
    beforeEach(module('htmlTemplates'));

    beforeEach(module('pascalprecht.translate', function ($translateProvider) {
        $translateProvider.translations('en', {
            'COLON': ': '
        });
        $translateProvider.preferredLanguage('en');
    }));
    
    beforeEach(inject(function($rootScope, $compile) {
        scope = $rootScope.$new();
        createElement = function() {
            scope = $rootScope.$new();
            element = angular.element('<actions-suggestions></actions-suggestions>');
            body.append(element);
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

    it('should display all suggested actions', inject(function(ColumnSuggestionService) {
        //when
        createElement();
        ColumnSuggestionService.transformations = [
            {
                'name': 'ceil_value',
                'label': 'Ceil value'
            },
            {
                'name': 'floor_value',
                'label': 'Floor value'
            },
            {
                'name': 'round_value',
                'label': 'Round value'
            }
        ];
        scope.$digest();

        //then
        expect(element.find('.accordion').length).toBe(3);
    }));

    it('should display searched suggested actions', inject(function(ColumnSuggestionService) {
        //when
        createElement();
        ColumnSuggestionService.transformations = [
            {
                'name': 'ceil_value',
                'label': 'Ceil value'
            },
            {
                'name': 'floor_value',
                'label': 'Floor value'
            },
            {
                'name': 'round_value',
                'label': 'Round value'
            }
        ];
        scope.$digest();

        element.controller('actionsSuggestions').searchActionString ='oo';
        scope.$digest();

        //then
        expect(element.find('.accordion').length).toBe(1);
    }));

});
