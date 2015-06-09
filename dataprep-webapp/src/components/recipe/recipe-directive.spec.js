describe('Recipe directive', function() {
    'use strict';
    var scope, element;

    var recipe = [
        {
            column: {id: '0', name: 'col1'},
            transformation: {
                stepId: '13a24e8765ef4',
                name: 'split',
                label: 'Split',
                category: 'split',
                parameters: [{name: 'pattern', type: 'string'}],
                items: []
            }
        },
        {
            column: {id: '1', name: 'col2'},
            transformation: {
                stepId: '9876fb498e36543ab51',
                name: 'uppercase',
                label: 'To uppercase',
                category: 'case',
                parameters: [],
                items: []
            },
            inactive: true
        },
        {
            column: {id: '2', name: 'col3'},
            transformation: {
                stepId: '8876fb498e3625ab53',
                name: 'textclustering',
                label: 'Cluster',
                category: 'quickfix',
                parameters: null,
                items: null,
                cluster: {
                    titles: ['', ''],
                    clusters: [
                        {
                            parameters: [
                                {
                                    name: 'Texa',
                                    type: 'boolean',
                                    description: 'parameter.Texa.desc',
                                    label: 'parameter.Texa.label',
                                    default: null
                                },
                                {
                                    name: 'Tixass',
                                    type: 'boolean',
                                    description: 'parameter.Tixass.desc',
                                    label: 'parameter.Tixass.label',
                                    default: null
                                },
                                {
                                    name: 'Tex@s',
                                    type: 'boolean',
                                    description: 'parameter.Tex@s.desc',
                                    label: 'parameter.Tex@s.label',
                                    default: null
                                }
                            ],
                            'replace': {
                                name: 'replaceValue',
                                type: 'string',
                                description: 'parameter.replaceValue.desc',
                                label: 'parameter.replaceValue.label',
                                default: 'Texas'
                            }
                        }
                    ]
                }
            },
            inactive: true
        }
    ];

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
        scope.$destroy();
        element.remove();
    });

    it('should render recipe entries', inject(function(RecipeService) {
        //given
        RecipeService.reset();

        //when
        RecipeService.getRecipe().push(recipe[0]);
        RecipeService.getRecipe().push(recipe[1]);
        scope.$digest();

        //then
        expect(element.find('>ul >li').length).toBe(2);
        expect(element.find('>ul >li >.talend-accordion-trigger').eq(0).text().trim().replace(/\s+/g, ' ')).toBe('1. Split on column col1');
        expect(element.find('>ul >li >.talend-accordion-trigger').eq(1).text().trim().replace(/\s+/g, ' ')).toBe('2. To uppercase on column col2');
        expect(element.find('>ul >li').eq(1).hasClass('inactive')).toBe(true);
    }));

    it('should render recipe params', inject(function(RecipeService) {
        //given
        RecipeService.reset();

        //when
        RecipeService.getRecipe().push(recipe[0]);
        scope.$digest();

        //then
        expect(element.find('>ul >li > ul.submenu').length).toBe(1);
        expect(element.find('>ul >li > ul.submenu').eq(0).find('.transformation-params').length).toBe(1);
    }));

    it('should render recipe cluster params', inject(function(RecipeService) {
        //given
        RecipeService.reset();
        var body = angular.element('body');

        //when
        RecipeService.getRecipe().push(recipe[2]);
        scope.$digest();

        //then
        expect(body.find('.modal-inner').length).toBe(1);
        expect(body.find('.modal-inner').find('.cluster').length).toBe(1);
    }));

});