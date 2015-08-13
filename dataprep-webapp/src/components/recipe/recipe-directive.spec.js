/*jshint camelcase: false */
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
            },
            actionParameters: {
                action: 'split',
                parameters: {
                    scope: 'column',
                    column_id: '0',
                    pattern: '/'
                }
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
            actionParameters: {
                action: 'uppercase',
                parameters: {
                    scope: 'column',
                    column_id: '1'
                }
            },
            inactive: true
        },
        {
            column: {id: '1', name: 'col2'},
            transformation: {
                stepId: '456bb784a9674e532fc446',
                name: 'replace_on_value',
                label: 'Replace value',
                category: 'quickfix',
                parameters: [
                    {name: 'cell_value', type: 'string'},
                    {name: 'replace_value', type: 'string'}
                ],
                items: []
            },
            actionParameters: {
                action: 'quickfix',
                parameters: {
                    scope: 'cell',
                    column_id: '1',
                    row_id: 56
                }
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
            actionParameters: {
                action: 'textclustering',
                parameters: {
                    scope: 'column',
                    column_id: '2',
                    Texa: 'Texas',
                    Tixass: 'Texas'
                }
            },
            inactive: true
        },
        {
            column: {id: '0', name: 'col1'},
            transformation: {
                stepId: 'preview step',
                name: 'split',
                label: 'Split',
                category: 'split',
                parameters: [{name: 'pattern', type: 'string'}],
                items: []
            },
            actionParameters: {
                action: 'split',
                parameters: {
                    scope: 'column',
                    column_id: '0',
                    pattern: '/'
                }
            },
            preview: true
        }
    ];

    beforeEach(module('data-prep.recipe'));
    beforeEach(module('htmlTemplates'));

    beforeEach(module('pascalprecht.translate', function ($translateProvider) {
        $translateProvider.translations('en', {
            'RECIPE_ITEM_ON_COL': 'on column',
            'RECIPE_ITEM_ON_CELL': 'on cell'
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
        //when
        RecipeService.getRecipe().push(recipe[0]);
        RecipeService.getRecipe().push(recipe[1]);
        RecipeService.getRecipe().push(recipe[2]);
        scope.$digest();

        //then
        expect(element.find('>ul .accordion').length).toBe(3);
        expect(element.find('>ul .accordion .trigger').eq(0).text().trim().replace(/\s+/g, ' ')).toBe('1. Split on column col1');
        expect(element.find('>ul .accordion .trigger').eq(1).text().trim().replace(/\s+/g, ' ')).toBe('2. To uppercase on column col2');
        expect(element.find('>ul .accordion .trigger').eq(2).text().trim().replace(/\s+/g, ' ')).toBe('3. Replace value on cell');
    }));

    it('should render early preview step', inject(function(RecipeService) {
        //when
        RecipeService.getRecipe().push(recipe[0]);
        RecipeService.getRecipe().push(recipe[1]);
        RecipeService.getRecipe().push(recipe[4]); // preview step
        scope.$digest();

        //then
        expect(element.find('>ul .accordion').length).toBe(3);
        expect(element.find('>ul .accordion').eq(0).hasClass('preview')).toBe(false);
        expect(element.find('>ul .accordion').eq(1).hasClass('preview')).toBe(false);
        expect(element.find('>ul .accordion').eq(2).hasClass('preview')).toBe(true);
    }));

    it('should render recipe params', inject(function(RecipeService) {
        //when
        RecipeService.getRecipe().push(recipe[0]);
        scope.$digest();

        //then
        expect(element.find('>ul .accordion .content').length).toBe(1);
        expect(element.find('>ul .accordion .content').eq(0).find('.transformation-params').length).toBe(1);
    }));

    it('should render recipe cluster params', inject(function(RecipeService) {
        //given
        var body = angular.element('body');

        //when
        RecipeService.getRecipe().push(recipe[3]);
        scope.$digest();

        //then
        expect(body.find('.modal-inner').length).toBe(1);
        expect(body.find('.modal-inner').find('.cluster').length).toBe(1);
    }));

});