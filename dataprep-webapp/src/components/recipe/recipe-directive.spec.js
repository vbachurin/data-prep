describe('Recipe directive', function() {
    'use strict';
    var scope, element;

    beforeEach(module('data-prep.recipe'));
    beforeEach(module('htmlTemplates'));

    beforeEach(module('pascalprecht.translate', function ($translateProvider) {
        $translateProvider.translations('en', {
            'RECIPE_ITEM_ON_COL': 'on column'
        });
        $translateProvider.preferredLanguage('en');
    }));

    beforeEach(inject(function($rootScope, $compile) {
        scope = $rootScope.$new();
        element = angular.element('<recipe></recipe>');
        $compile(element)(scope);
        scope.$digest();
    }));

    afterEach(function() {
        element.remove();
    });

    it('should render recipe entries', inject(function(RecipeService) {
        //given
        RecipeService.reset();

        //when
        RecipeService.getRecipe().push({
            column: {id: 'col1'},
            transformation: {
                stepId: '13a24e8765ef4',
                name: 'split',
                label: 'Split',
                category: 'split',
                parameters: [{name: 'pattern', type: 'string'}],
                items: []
            }
        });
        RecipeService.getRecipe().push({
            column: {id: 'col2'},
            transformation: {
                stepId: '9876fb498e36543ab51',
                name: 'uppercase',
                label: 'To uppercase',
                category: 'case',
                parameters: [],
                items: []
            },
            inactive: true
        });
        scope.$digest();

        //then
        expect(element.find('>ul >li').length).toBe(2);
        expect(element.find('>ul >li >.talend-accordion-trigger').eq(0).text().trim().replace(/\s+/g, ' ')).toBe('1. Split on column col1');
        expect(element.find('>ul >li >.talend-accordion-trigger').eq(1).text().trim().replace(/\s+/g, ' ')).toBe('2. To uppercase on column col2');
        expect(element.find('>ul >li').eq(1).hasClass('inactive')).toBe(true);
    }));

});