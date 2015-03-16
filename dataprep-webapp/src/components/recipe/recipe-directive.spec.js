describe('Datagrid directive', function() {
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
        //when
        RecipeService.add({id: 'col1'}, {
            name: 'split',
            category: 'split',
            parameters: [{name: 'pattern', type: 'string'}],
            items: []
        });
        RecipeService.add({id: 'col2'}, {
            name: 'uppercase',
            category: 'case',
            parameters: [],
            items: []
        });
        scope.$digest();

        //then
        expect(element.find('>ul >li').length).toBe(2);
        expect(element.find('>ul >li >a').eq(0).text().trim().replace(/\s+/g, ' ')).toBe('1. split on column col1');
        expect(element.find('>ul >li >a').eq(1).text().trim().replace(/\s+/g, ' ')).toBe('2. uppercase on column col2');
    }));

});