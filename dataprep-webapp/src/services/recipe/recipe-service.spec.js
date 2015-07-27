/*jshint camelcase: false */

describe('Recipe service', function () {
    'use strict';

    var preparationDetails = function() {
        return {
            'id': '627766216e4b3c99ee5c8621f32ac42f4f87f1b4',
            'dataSetId': 'db6c4ad8-77da-4a30-b29f-ca552706b058',
            'author': 'anonymousUser',
            'name': 'JSO prep',
            'creationDate': 1427980028390,
            'lastModificationDate': 1427980216038,
            'steps': [
                'f6e172c33bdacbc69bca9d32b2bd78174712a171',
                '329ccf0cce42db4dc0ffa9f389c05ff7d75c1748',
                'ec87e2acda2b181fc7eb7c22d91e128c6d0434fc',
                '0c58ee3034114eb620b8e598e02c74172a43e96a',
                '1e1f41dd6d4554705abebd8d1896022acdbad217',
                'add60ff0f6de4c703fa75725ada38fb37af065e6',
                '2aba0e60054728f046d35315830bce9abc3c5249'
            ],
            'actions': [
                {
                    'action': 'uppercase',
                    'parameters': {
                        'column_name': 'country'
                    }
                },
                {
                    'action': 'fillemptywithdefault',
                    'parameters': {
                        'default_value': 'M',
                        'column_name': 'gender'
                    }
                },
                {
                    'action': 'negate',
                    'parameters': {
                        'column_name': 'campain'
                    }
                },
                {
                    'action': 'cut',
                    'parameters': {
                        'pattern': '.',
                        'column_name': 'first_item'
                    }
                },
                {
                    'action': 'textclustering',
                    'parameters': {
                        'Texa': 'Texas',
                        'Tixass': 'Texas',
                        'Tex@s': 'Texas',
                        'Massachusetts': 'Massachussets',
                        'Masachusetts': 'Massachussets',
                        'Massachussetts': 'Massachussets',
                        'Massachusets': 'Massachussets',
                        'Masachussets': 'Massachussets',
                        'column_name': 'uglystate',
                        'column_id': '1'
                    }
                },
                {
                    'action': 'fillemptywithdefaultboolean',
                    'parameters': {
                        'default_value': 'True',
                        'column_name': 'campain'
                    }
                }
            ],
            'metadata': [
                {
                    'compatibleColumnTypes': [
                        'STRING'
                    ],
                    'category': 'case',
                    'items': [],
                    'name': 'uppercase',
                    'parameters': [
                        {
                            'name': 'column_name',
                            'type': 'string',
                            'description': 'parameter.column_name.desc',
                            'label': 'parameter.column_name.label',
                            'default': '',
                            implicit: true
                        }
                    ],
                    'description': 'action.uppercase.desc',
                    'label': 'action.uppercase.label'
                },
                {
                    'compatibleColumnTypes': [
                        'STRING'
                    ],
                    'items': [],
                    'name': 'fillemptywithdefault',
                    'parameters': [
                        {
                            'name': 'column_name',
                            'type': 'string',
                            'description': 'parameter.column_name.desc',
                            'label': 'parameter.column_name.label',
                            'default': '',
                            implicit: true
                        },
                        {
                            'name': 'default_value',
                            'type': 'string',
                            'description': 'parameter.default_value.desc',
                            'label': 'parameter.default_value.label',
                            'default': ''
                        }
                    ],
                    'category': 'repair',
                    'description': 'action.fillemptywithdefault.desc',
                    'label': 'action.fillemptywithdefault.label'
                },
                {
                    'compatibleColumnTypes': [
                        'BOOLEAN'
                    ],
                    'category': 'boolean',
                    'items': [],
                    'name': 'negate',
                    'parameters': [
                        {
                            'name': 'column_name',
                            'type': 'string',
                            'description': 'parameter.column_name.desc',
                            'label': 'parameter.column_name.label',
                            'default': '',
                            implicit: true
                        }
                    ],
                    'description': 'action.negate.desc',
                    'label': 'action.negate.label'
                },
                {
                    'compatibleColumnTypes': [
                        'STRING'
                    ],
                    'category': 'repair',
                    'items': [],
                    'name': 'cut',
                    'parameters': [
                        {
                            'name': 'column_name',
                            'type': 'string',
                            'description': 'parameter.column_name.desc',
                            'label': 'parameter.column_name.label',
                            'default': '',
                            implicit: true
                        },
                        {
                            'name': 'pattern',
                            'type': 'string',
                            'description': 'parameter.pattern.desc',
                            'label': 'parameter.pattern.label',
                            'default': ''
                        }
                    ],
                    'description': 'action.cut.desc',
                    'label': 'action.cut.label'
                },
                {
                    'compatibleColumnTypes': [
                        'STRING'
                    ],
                    'category': 'quickfix',
                    'name': 'textclustering',
                    'items': [],
                    'dynamic': true,
                    'parameters': [
                        {
                            'name': 'column_name',
                            'type': 'string',
                            'description': 'The column on which apply this action to',
                            'label': 'Column',
                            'default': '',
                            implicit: true
                        }
                    ],
                    'description': 'Replace all similar values with the right one',
                    'label': 'Cluster'
                },
                {
                    'compatibleColumnTypes': [
                        'BOOLEAN'
                    ],
                    'items': [
                        {
                            'name': 'default_value',
                            'category': 'categ',
                            'values': [
                                {
                                    'name': 'True',
                                    'parameters': [],
                                    'default': true
                                },
                                {
                                    'name': 'False',
                                    'parameters': [],
                                    'default': false
                                }
                            ],
                            'description': 'parameter.default_value.desc',
                            'label': 'parameter.default_value.label'
                        }
                    ],
                    'name': 'fillemptywithdefaultboolean',
                    'parameters': [
                        {
                            'name': 'column_name',
                            'type': 'string',
                            'description': 'parameter.column_name.desc',
                            'label': 'parameter.column_name.label',
                            'default': '',
                            implicit: true
                        }
                    ],
                    'category': 'repair',
                    'description': 'action.fillemptywithdefaultboolean.desc',
                    'label': 'action.fillemptywithdefaultboolean.label'
                }
            ]
        };
    };

    var initialCluster = function() {
        return {
            'titles': [
                'We found these values',
                'And we\'ll keep this value'
            ],
            'clusters': [
                {
                    'parameters': [
                        {
                            'name': 'Texa',
                            'type': 'boolean',
                            'description': 'parameter.Texa.desc',
                            'label': 'parameter.Texa.label',
                            'default': null
                        },
                        {
                            'name': 'Tixass',
                            'type': 'boolean',
                            'description': 'parameter.Tixass.desc',
                            'label': 'parameter.Tixass.label',
                            'default': null
                        },
                        {
                            'name': 'Tex@s',
                            'type': 'boolean',
                            'description': 'parameter.Tex@s.desc',
                            'label': 'parameter.Tex@s.label',
                            'default': null
                        }
                    ],
                    'replace': {
                        'name': 'replaceValue',
                        'type': 'string',
                        'description': 'parameter.replaceValue.desc',
                        'label': 'parameter.replaceValue.label',
                        'default': 'Texas'
                    }
                },
                {
                    'parameters': [
                        {
                            'name': 'Massachusetts',
                            'type': 'boolean',
                            'description': 'parameter.Massachusetts.desc',
                            'label': 'parameter.Massachusetts.label',
                            'default': null
                        },
                        {
                            'name': 'Masachusetts',
                            'type': 'boolean',
                            'description': 'parameter.Masachusetts.desc',
                            'label': 'parameter.Masachusetts.label',
                            'default': null
                        },
                        {
                            'name': 'Massachussetts',
                            'type': 'boolean',
                            'description': 'parameter.Massachussetts.desc',
                            'label': 'parameter.Massachussetts.label',
                            'default': null
                        },
                        {
                            'name': 'Massachusets',
                            'type': 'boolean',
                            'description': 'parameter.Massachusets.desc',
                            'label': 'parameter.Massachusets.label',
                            'default': null
                        },
                        {
                            'name': 'Masachussets',
                            'type': 'boolean',
                            'description': 'parameter.Masachussets.desc',
                            'label': 'parameter.Masachussets.label',
                            'default': null
                        }
                    ],
                    'replace': {
                        'name': 'replaceValue',
                        'type': 'string',
                        'description': 'parameter.replaceValue.desc',
                        'label': 'parameter.replaceValue.label',
                        'default': 'Massachussets'
                    }
                }
            ]
        };
    };

    var expectedInitializedCluster = {
        'titles':[
            'We found these values',
            'And we\'ll keep this value'
        ],
        'clusters':[
            {
                'parameters':[
                    {
                        'name':'Texa',
                        'type':'boolean',
                        'description':'parameter.Texa.desc',
                        'label':'parameter.Texa.label',
                        'default':null,
                        'value':true,
                        'initialValue':true
                    },
                    {
                        'name':'Tixass',
                        'type':'boolean',
                        'description':'parameter.Tixass.desc',
                        'label':'parameter.Tixass.label',
                        'default':null,
                        'value':true,
                        'initialValue':true
                    },
                    {
                        'name':'Tex@s',
                        'type':'boolean',
                        'description':'parameter.Tex@s.desc',
                        'label':'parameter.Tex@s.label',
                        'default':null,
                        'value':true,
                        'initialValue':true
                    }
                ],
                'replace':{
                    'name':'replaceValue',
                    'type':'string',
                    'description':'parameter.replaceValue.desc',
                    'label':'parameter.replaceValue.label',
                    'default':'Texas',
                    'value':'Texas',
                    'initialValue':'Texas',
                    'inputType':'text'
                },
                'initialActive':true
            },
            {
                'parameters':[
                    {
                        'name':'Massachusetts',
                        'type':'boolean',
                        'description':'parameter.Massachusetts.desc',
                        'label':'parameter.Massachusetts.label',
                        'default':null,
                        'value':true,
                        'initialValue':true
                    },
                    {
                        'name':'Masachusetts',
                        'type':'boolean',
                        'description':'parameter.Masachusetts.desc',
                        'label':'parameter.Masachusetts.label',
                        'default':null,
                        'value':true,
                        'initialValue':true
                    },
                    {
                        'name':'Massachussetts',
                        'type':'boolean',
                        'description':'parameter.Massachussetts.desc',
                        'label':'parameter.Massachussetts.label',
                        'default':null,
                        'value':true,
                        'initialValue':true
                    },
                    {
                        'name':'Massachusets',
                        'type':'boolean',
                        'description':'parameter.Massachusets.desc',
                        'label':'parameter.Massachusets.label',
                        'default':null,
                        'value':true,
                        'initialValue':true
                    },
                    {
                        'name':'Masachussets',
                        'type':'boolean',
                        'description':'parameter.Masachussets.desc',
                        'label':'parameter.Masachussets.label',
                        'default':null,
                        'value':true,
                        'initialValue':true
                    }
                ],
                'replace':{
                    'name':'replaceValue',
                    'type':'string',
                    'description':'parameter.replaceValue.desc',
                    'label':'parameter.replaceValue.label',
                    'default':'Massachussets',
                    'value':'Massachussets',
                    'initialValue':'Massachussets',
                    'inputType':'text'
                },
                'initialActive':true
            }
        ]
    };

    beforeEach(module('data-prep.services.recipe'));
    beforeEach(inject(function($q, PreparationService, TransformationService) {
        spyOn(PreparationService, 'getDetails').and.returnValue($q.when({
            data: preparationDetails()
        }));
        spyOn(TransformationService, 'resetParamValue').and.returnValue();
        spyOn(TransformationService, 'initDynamicParameters').and.callFake(function (transformation) {
            transformation.cluster = initialCluster();
            return $q.when(transformation);
        });
        spyOn(TransformationService, 'initParamsValues').and.callThrough();
    }));

    it('should reset recipe item list when no preparation is loaded', inject(function(RecipeService, PreparationService) {
        //given
        PreparationService.currentPreparationId = null;
        RecipeService.getRecipe()[0] = {};
        expect(RecipeService.getRecipe().length).toBeTruthy();

        //when
        RecipeService.refresh();

        //then
        expect(RecipeService.getRecipe().length).toBe(0);
    }));

    it('should get recipe with no params when a preparation is loaded', inject(function($rootScope, RecipeService, PreparationService) {
        //given
        PreparationService.currentPreparationId = '627766216e4b3c99ee5c8621f32ac42f4f87f1b4';

        //when
        RecipeService.refresh();
        $rootScope.$digest();

        //then
        var recipe = RecipeService.getRecipe();
        expect(recipe.length).toBe(6);
        expect(recipe[0].column.name).toBe('country');
        expect(recipe[0].transformation.stepId).toBe('329ccf0cce42db4dc0ffa9f389c05ff7d75c1748');
        expect(recipe[0].transformation.name).toBe('uppercase');
        expect(recipe[0].transformation.parameters).toEqual([]);
        expect(recipe[0].transformation.items).toEqual([]);

        expect(recipe[2].column.name).toBe('campain');
        expect(recipe[2].transformation.stepId).toBe('0c58ee3034114eb620b8e598e02c74172a43e96a');
        expect(recipe[2].transformation.name).toBe('negate');
        expect(recipe[2].transformation.parameters).toEqual([]);
        expect(recipe[2].transformation.items).toEqual([]);
    }));

    it('should get recipe from preparation and init recipe simple params', inject(function($rootScope, RecipeService, PreparationService) {
        //given
        PreparationService.currentPreparationId = '627766216e4b3c99ee5c8621f32ac42f4f87f1b4';

        //when
        RecipeService.refresh();
        $rootScope.$digest();

        //then
        var recipe = RecipeService.getRecipe();
        expect(recipe.length).toBe(6);

        expect(recipe[1].column.name).toBe('gender');
        expect(recipe[1].transformation.stepId).toBe('ec87e2acda2b181fc7eb7c22d91e128c6d0434fc');
        expect(recipe[1].transformation.name).toBe('fillemptywithdefault');
        expect(recipe[1].transformation.items).toEqual([]);
        expect(recipe[1].transformation.parameters).toEqual([
            {
                name: 'default_value',
                type: 'string',
                description: 'parameter.default_value.desc',
                label: 'parameter.default_value.label',
                default: '',
                initialValue: 'M',
                value: 'M',
                inputType: 'text'
            }]);

        expect(recipe[3].column.name).toBe('first_item');
        expect(recipe[3].transformation.stepId).toBe('1e1f41dd6d4554705abebd8d1896022acdbad217');
        expect(recipe[3].transformation.name).toBe('cut');
        expect(recipe[3].transformation.items).toEqual([]);
        expect(recipe[3].transformation.parameters).toEqual([
            {
                name: 'pattern',
                type: 'string',
                description: 'parameter.pattern.desc',
                label: 'parameter.pattern.label',
                default: '',
                initialValue: '.',
                value: '.',
                inputType: 'text'
            }]);
    }));

    it('should get recipe from preparation and init recipe choices', inject(function($rootScope, RecipeService, PreparationService) {
        //given
        PreparationService.currentPreparationId = '627766216e4b3c99ee5c8621f32ac42f4f87f1b4';

        //when
        RecipeService.refresh();
        $rootScope.$digest();

        //then
        var recipe = RecipeService.getRecipe();
        expect(recipe.length).toBe(6);

        expect(recipe[5].column.name).toBe('campain');
        expect(recipe[5].transformation.stepId).toBe('2aba0e60054728f046d35315830bce9abc3c5249');
        expect(recipe[5].transformation.name).toBe('fillemptywithdefaultboolean');
        expect(recipe[5].transformation.parameters).toEqual([]);
        expect(recipe[5].transformation.items).toEqual([
            {
                name: 'default_value',
                category: 'categ',
                values: [
                    { name: 'True', parameters: [], default: true },
                    { name: 'False', parameters: [], default: false }
                ],
                description: 'parameter.default_value.desc',
                label: 'parameter.default_value.label',
                initialValue: { name: 'True', parameters: [], default: true },
                selectedValue: { name: 'True', parameters: [], default: true }
            }]);
    }));

    it('should get recipe from preparation and init dynamic params', inject(function($rootScope, RecipeService, TransformationService, PreparationService) {
        //given
        PreparationService.currentPreparationId = '627766216e4b3c99ee5c8621f32ac42f4f87f1b4';

        //when
        RecipeService.refresh();
        $rootScope.$digest();

        //then
        var recipe = RecipeService.getRecipe();
        expect(recipe.length).toBe(6);

        expect(recipe[4].column.name).toBe('uglystate');
        expect(recipe[4].transformation.stepId).toBe('add60ff0f6de4c703fa75725ada38fb37af065e6');
        expect(recipe[4].transformation.name).toBe('textclustering');
        expect(recipe[4].transformation.parameters).toEqual([]);
        expect(recipe[4].transformation.items).toEqual([]);
        expect(recipe[4].transformation.cluster).toEqual(expectedInitializedCluster);

        expect(TransformationService.initDynamicParameters).toHaveBeenCalledWith(recipe[4].transformation, { columnId: '1', preparationId: '627766216e4b3c99ee5c8621f32ac42f4f87f1b4', stepId: '1e1f41dd6d4554705abebd8d1896022acdbad217' });
        expect(TransformationService.initParamsValues).toHaveBeenCalledWith(recipe[4].transformation, recipe[4].actionParameters.parameters);
    }));

    it('should reuse dynamic params from previous recipe if ids are the same, on refresh', inject(function($rootScope, RecipeService, TransformationService, PreparationService) {
        //given
        PreparationService.currentPreparationId = '627766216e4b3c99ee5c8621f32ac42f4f87f1b4';
        RecipeService.refresh();
        $rootScope.$digest();
        var oldRecipe = RecipeService.getRecipe();
        expect(TransformationService.initDynamicParameters.calls.count()).toBe(1);

        //when
        RecipeService.refresh();
        $rootScope.$digest();

        //then
        var recipe = RecipeService.getRecipe();
        expect(recipe[4].transformation.parameters).toBe(oldRecipe[4].transformation.parameters);
        expect(recipe[4].transformation.items).toBe(oldRecipe[4].transformation.items);
        expect(recipe[4].transformation.cluster).toBe(oldRecipe[4].transformation.cluster);

        expect(TransformationService.initDynamicParameters.calls.count()).toBe(1);
    }));

    it('should save steps actions parameters', inject(function($rootScope, RecipeService, PreparationService) {
        //given
        PreparationService.currentPreparationId = '627766216e4b3c99ee5c8621f32ac42f4f87f1b4';

        //when
        RecipeService.refresh();
        $rootScope.$digest();

        //then
        var recipe = RecipeService.getRecipe();
        expect(recipe[0].actionParameters).toEqual({ action: 'uppercase', parameters: Object({ column_name: 'country' }) });
        expect(recipe[1].actionParameters).toEqual({ action: 'fillemptywithdefault', parameters: Object({ default_value: 'M', column_name: 'gender' }) });
        expect(recipe[2].actionParameters).toEqual({ action: 'negate', parameters: Object({ column_name: 'campain' }) });
        expect(recipe[3].actionParameters).toEqual({ action: 'cut', parameters: Object({ pattern: '.', column_name: 'first_item' }) });
        expect(recipe[4].actionParameters).toEqual({ action: 'textclustering', parameters: { Texa: 'Texas', Tixass: 'Texas', 'Tex@s': 'Texas', Massachusetts: 'Massachussets', Masachusetts: 'Massachussets', Massachussetts: 'Massachussets', Massachusets: 'Massachussets', Masachussets: 'Massachussets', column_id: '1', column_name: 'uglystate' }});
        expect(recipe[5].actionParameters).toEqual({ action: 'fillemptywithdefaultboolean', parameters: Object({ default_value: 'True', column_name: 'campain' }) });
    }));

    it('should reset current values to initial saved values in param', inject(function(RecipeService, TransformationService) {
        //given
        var recipeItem = {
            column: {id: 'colId'},
            transformation: {
                stepId: '329ccf0cce42db4dc0ffa9f389c05ff7d75c1748',
                name: 'cut',
                items: [
                    {
                        name: 'mode',
                        type: 'LIST',
                        values: [
                            {name: 'regex'},
                            {name: 'index'}
                        ]
                    }
                ],
                parameters: [
                    {
                        name: 'param1',
                        type: 'string',
                        initialValue: 'myParam1',
                        inputType: 'text'
                    }
                ],
                cluster: {
                    details: {}
                }
            }
        };

        //when
        RecipeService.resetParams(recipeItem);

        //then
        expect(TransformationService.resetParamValue).toHaveBeenCalledWith(recipeItem.transformation.parameters, null);
        expect(TransformationService.resetParamValue).toHaveBeenCalledWith(recipeItem.transformation.items, 'CHOICE');
        expect(TransformationService.resetParamValue).toHaveBeenCalledWith(recipeItem.transformation.cluster, 'CLUSTER');
    }));

    it('should reset current values to initial saved values in param', inject(function(RecipeService) {
        //given
        var recipe = [{transformation: {stepId: '0'}},
            {transformation: {stepId: '1'}},
            {transformation: {stepId: '2'}},
            {transformation: {stepId: '3'}}];
        RecipeService.getRecipe().push(recipe[0], recipe[1], recipe[2], recipe[3]);

        //when
        RecipeService.disableStepsAfter(recipe[1]);

        //then
        expect(recipe[0].inactive).toBeFalsy();
        expect(recipe[1].inactive).toBeFalsy();
        expect(recipe[2].inactive).toBeTruthy();
        expect(recipe[3].inactive).toBeTruthy();
        expect(RecipeService.getActiveThresholdStep()).toBe(recipe[1]);
    }));

    it('should return the step before provided step', inject(function(RecipeService) {
        //given
        var recipe = [{transformation: {stepId: '0'}},
            {transformation: {stepId: '1'}},
            {transformation: {stepId: '2'}},
            {transformation: {stepId: '3'}}];
        RecipeService.getRecipe().push(recipe[0], recipe[1], recipe[2], recipe[3]);

        //when
        var previous = RecipeService.getPreviousStep(recipe[2]);

        //then
        expect(previous).toBe(recipe[1]);
    }));

    it('should return the initial step when provided step is the first transformation', inject(function($rootScope, RecipeService, PreparationService) {
        //given
        PreparationService.currentPreparationId = '627766216e4b3c99ee5c8621f32ac42f4f87f1b4';
        RecipeService.refresh();
        $rootScope.$digest();

        //when
        var previous = RecipeService.getPreviousStep(RecipeService.getRecipe()[0]);

        //then
        expect(previous.transformation.stepId).toBe('f6e172c33bdacbc69bca9d32b2bd78174712a171');
    }));

    it('should return the wanted step', inject(function($rootScope, RecipeService, PreparationService) {
        //given
        PreparationService.currentPreparationId = '627766216e4b3c99ee5c8621f32ac42f4f87f1b4';
        RecipeService.refresh();
        $rootScope.$digest();

        var expectedStep = RecipeService.getRecipe()[1];

        //when
        var result = RecipeService.getStep(1, false);

        //then
        expect(result).toBe(expectedStep);
    }));

    it('should return null when the index is superior to the recipe length', inject(function($rootScope, RecipeService, PreparationService) {
        //given
        PreparationService.currentPreparationId = '627766216e4b3c99ee5c8621f32ac42f4f87f1b4';
        RecipeService.refresh();
        $rootScope.$digest();

        //when
        var result = RecipeService.getStep(25, false);

        //then
        expect(result).toBe(null);
    }));

    it('should return the last step when the index is superior to the recipe length', inject(function($rootScope, RecipeService, PreparationService) {
        //given
        PreparationService.currentPreparationId = '627766216e4b3c99ee5c8621f32ac42f4f87f1b4';
        RecipeService.refresh();
        $rootScope.$digest();

        var expectedStep = RecipeService.getRecipe()[5];

        //when
        var result = RecipeService.getStep(25, true);

        //then
        expect(result).toBe(expectedStep);
    }));

    it('should return the last active step index', inject(function($rootScope, RecipeService, PreparationService) {
        //given
        PreparationService.currentPreparationId = '627766216e4b3c99ee5c8621f32ac42f4f87f1b4';
        RecipeService.refresh();
        $rootScope.$digest();

        RecipeService.disableStepsAfter(RecipeService.getRecipe()[2]);

        //when
        var index = RecipeService.getActiveThresholdStepIndex();

        //then
        expect(index).toBe(2);
    }));

    it('should return last step index when no specific active step has been set', inject(function($rootScope, RecipeService, PreparationService) {
        //given
        PreparationService.currentPreparationId = '627766216e4b3c99ee5c8621f32ac42f4f87f1b4';
        RecipeService.refresh();
        $rootScope.$digest();

        //when
        var index = RecipeService.getActiveThresholdStepIndex();

        //then
        expect(index).toBe(5);
    }));

    it('should return the initial state if the index is 0', inject(function($rootScope, RecipeService, PreparationService) {
        //given
        PreparationService.currentPreparationId = '627766216e4b3c99ee5c8621f32ac42f4f87f1b4';
        RecipeService.refresh();
        $rootScope.$digest();

        //when
        var step = RecipeService.getStepBefore(0);

        //then
        expect(step).toEqual({ transformation: {stepId: 'f6e172c33bdacbc69bca9d32b2bd78174712a171' }});
    }));

    it('should return the last step if the index is bigger than the recipe size', inject(function($rootScope, RecipeService, PreparationService) {
        //given
        PreparationService.currentPreparationId = '627766216e4b3c99ee5c8621f32ac42f4f87f1b4';
        RecipeService.refresh();
        $rootScope.$digest();

        //when
        var step = RecipeService.getStepBefore(1000);

        //then
        expect(step).toEqual(RecipeService.getRecipe()[5]);
    }));

    it('should return the step before the one identified by the index', inject(function($rootScope, RecipeService, PreparationService) {
        //given
        PreparationService.currentPreparationId = '627766216e4b3c99ee5c8621f32ac42f4f87f1b4';
        RecipeService.refresh();
        $rootScope.$digest();

        //when
        var step = RecipeService.getStepBefore(2);

        //then
        expect(step).toEqual(RecipeService.getRecipe()[1]);
    }));

    it('should return the step index', inject(function($rootScope, RecipeService, PreparationService) {
        //given
        PreparationService.currentPreparationId = '627766216e4b3c99ee5c8621f32ac42f4f87f1b4';
        RecipeService.refresh();
        $rootScope.$digest();

        //when
        var index = RecipeService.getStepIndex(RecipeService.getRecipe()[2]);

        //then
        expect(index).toBe(2);
    }));

    it('should return true when step is the first step', inject(function($rootScope, RecipeService, PreparationService) {
        //given
        PreparationService.currentPreparationId = '627766216e4b3c99ee5c8621f32ac42f4f87f1b4';
        RecipeService.refresh();
        $rootScope.$digest();

        //when
        var isFirst = RecipeService.isFirstStep(RecipeService.getRecipe()[0]);

        //then
        expect(isFirst).toBe(true);
    }));

    it('should return false when step is NOT the first step', inject(function($rootScope, RecipeService, PreparationService) {
        //given
        PreparationService.currentPreparationId = '627766216e4b3c99ee5c8621f32ac42f4f87f1b4';
        RecipeService.refresh();
        $rootScope.$digest();

        //when
        var isFirst = RecipeService.isFirstStep(RecipeService.getRecipe()[2]);

        //then
        expect(isFirst).toBe(false);
    }));

    it('should return true when step is the last step', inject(function($rootScope, RecipeService, PreparationService) {
        //given
        PreparationService.currentPreparationId = '627766216e4b3c99ee5c8621f32ac42f4f87f1b4';
        RecipeService.refresh();
        $rootScope.$digest();

        //when
        var isLast = RecipeService.isLastStep(RecipeService.getRecipe()[5]);

        //then
        expect(isLast).toBe(true);
    }));

    it('should return false when step is NOT the last step', inject(function($rootScope, RecipeService, PreparationService) {
        //given
        PreparationService.currentPreparationId = '627766216e4b3c99ee5c8621f32ac42f4f87f1b4';
        RecipeService.refresh();
        $rootScope.$digest();

        //when
        var isLast = RecipeService.isLastStep(RecipeService.getRecipe()[2]);

        //then
        expect(isLast).toBe(false);
    }));
});