/*jshint camelcase: false */

describe('Recipe service', function () {
    'use strict';

    var preparationDetails = {
        'id': '627766216e4b3c99ee5c8621f32ac42f4f87f1b4',
        'dataSetId': 'db6c4ad8-77da-4a30-b29f-ca552706b058',
        'author': 'anonymousUser',
        'name': 'JSO prep',
        'creationDate': 1427980028390,
        'lastModificationDate': 1427980216038,
        'steps': [
            '2aba0e60054728f046d35315830bce9abc3c5249',
            '1e1f41dd6d4554705abebd8d1896022acdbad217',
            '0c58ee3034114eb620b8e598e02c74172a43e96a',
            'ec87e2acda2b181fc7eb7c22d91e128c6d0434fc',
            '329ccf0cce42db4dc0ffa9f389c05ff7d75c1748',
            'f6e172c33bdacbc69bca9d32b2bd78174712a171'
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
                        'default': ''
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
                        'default': ''
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
                        'default': ''
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
                        'default': ''
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
                        'default': ''
                    }
                ],
                'category': 'repair',
                'description': 'action.fillemptywithdefaultboolean.desc',
                'label': 'action.fillemptywithdefaultboolean.label'
            }
        ]
    };

    beforeEach(module('data-prep.services.recipe'));
    beforeEach(inject(function($q, PreparationService) {
        spyOn(PreparationService, 'getDetails').and.returnValue($q.when({
            data: preparationDetails
        }));
    }));

    it('should reset recipe item list', inject(function(RecipeService) {
        //given
        RecipeService.getRecipe()[0] = {};
        expect(RecipeService.getRecipe().length).toBeTruthy();

        //when
        RecipeService.reset();

        //then
        expect(RecipeService.getRecipe().length).toBe(0);
    }));

    it('should get recipe from preparation and init recipe items/params', inject(function($rootScope, RecipeService) {
        //given

        //when
        RecipeService.refresh();
        $rootScope.$digest();

        //then
        var recipe = RecipeService.getRecipe();
        expect(recipe.length).toBe(5);
        expect(recipe[0].column.id).toBe('country');
        expect(recipe[0].transformation.stepId).toBe('329ccf0cce42db4dc0ffa9f389c05ff7d75c1748');
        expect(recipe[0].transformation.name).toBe('uppercase');
        expect(recipe[0].transformation.parameters).toEqual([]);
        expect(recipe[0].transformation.items).toEqual([]);

        expect(recipe[1].column.id).toBe('gender');
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

        expect(recipe[2].column.id).toBe('campain');
        expect(recipe[2].transformation.stepId).toBe('0c58ee3034114eb620b8e598e02c74172a43e96a');
        expect(recipe[2].transformation.name).toBe('negate');
        expect(recipe[2].transformation.parameters).toEqual([]);
        expect(recipe[2].transformation.items).toEqual([]);

        expect(recipe[3].column.id).toBe('first_item');
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

        expect(recipe[4].column.id).toBe('campain');
        expect(recipe[4].transformation.stepId).toBe('2aba0e60054728f046d35315830bce9abc3c5249');
        expect(recipe[4].transformation.name).toBe('fillemptywithdefaultboolean');
        expect(recipe[4].transformation.parameters).toEqual([]);
        expect(recipe[4].transformation.items).toEqual([
            {
                name: 'default_value',
                category: 'categ',
                values: [
                    { name: 'True', parameters: [], default: true },
                    { name: 'False', parameters: [], default: false }
                ],
                description: 'parameter.default_value.desc',
                label: 'parameter.default_value.label',
                type: 'LIST',
                initialValue: { name: 'True', parameters: [], default: true },
                selectedValue: { name: 'True', parameters: [], default: true }
            }]);
    }));

    it('should save steps actions parameters', inject(function($rootScope, RecipeService) {
        //given

        //when
        RecipeService.refresh();
        $rootScope.$digest();

        //then
        var recipe = RecipeService.getRecipe();
        expect(recipe[0].actionParameters).toEqual({ action: 'uppercase', parameters: Object({ column_name: 'country' }) });
        expect(recipe[1].actionParameters).toEqual({ action: 'fillemptywithdefault', parameters: Object({ default_value: 'M', column_name: 'gender' }) });
        expect(recipe[2].actionParameters).toEqual({ action: 'negate', parameters: Object({ column_name: 'campain' }) });
        expect(recipe[3].actionParameters).toEqual({ action: 'cut', parameters: Object({ pattern: '.', column_name: 'first_item' }) });
        expect(recipe[4].actionParameters).toEqual({ action: 'fillemptywithdefaultboolean', parameters: Object({ default_value: 'True', column_name: 'campain' }) });
    }));

    it('should reset current values to initial saved values in param', inject(function(RecipeService) {
        //given
        var column = {id: 'colId'};
        var transformation = {
            stepId: '329ccf0cce42db4dc0ffa9f389c05ff7d75c1748',
            name: 'cut',
            items: [
                {
                    name: 'mode',
                    type: 'LIST',
                    values: [
                        {
                            name: 'regex',
                            parameters : [
                                {name: 'regex', type: 'text', initialValue: 'param1Value'},
                                {name: 'comment', type: 'text', initialValue: 'my comment'}
                            ]
                        },
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
                },
                {
                    name: 'param2',
                    type: 'integer',
                    initialValue: 5,
                    inputType: 'number'
                }
            ]
        };
        transformation.items[0].initialValue = transformation.items[0].values[1];

        RecipeService.getRecipe().push({
            column: column,
            transformation: transformation
        });
        var recipe = RecipeService.getRecipe();
        recipe[0].transformation.parameters[0].value = 'myNewParam1';
        recipe[0].transformation.parameters[1].value = 6;
        recipe[0].transformation.items[0].selectedValue = transformation.items[0].values[0];
        recipe[0].transformation.items[0].values[0].parameters[0].value = 'newParam1Value';
        recipe[0].transformation.items[0].values[0].parameters[1].value = 'myNewcomment';

        //when
        RecipeService.resetParams(recipe[0]);

        //then
        expect(recipe[0].transformation.parameters[0].initialValue).toBe('myParam1');
        expect(recipe[0].transformation.parameters[1].initialValue).toBe(5);
        expect(recipe[0].transformation.items[0].initialValue).toBe(recipe[0].transformation.items[0].values[1]);
        expect(recipe[0].transformation.items[0].values[0].parameters[0].initialValue).toBe('param1Value');
        expect(recipe[0].transformation.items[0].values[0].parameters[1].initialValue).toBe('my comment');
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

    it('should return the initial step when provided step is the first transformation', inject(function($rootScope, RecipeService) {
        //given
        RecipeService.refresh();
        $rootScope.$digest();

        //when
        var previous = RecipeService.getPreviousStep(RecipeService.getRecipe()[0]);

        //then
        expect(previous.transformation.stepId).toBe('f6e172c33bdacbc69bca9d32b2bd78174712a171');
    }));

    it('should return the wanted step', inject(function($rootScope, RecipeService) {
        //given
        RecipeService.refresh();
        $rootScope.$digest();

        var expectedStep = RecipeService.getRecipe()[1];

        //when
        var result = RecipeService.getStep(1, false);

        //then
        expect(result).toBe(expectedStep);
    }));

    it('should return null when the index is superior to the recipe length', inject(function($rootScope, RecipeService) {
        //given
        RecipeService.refresh();
        $rootScope.$digest();

        //when
        var result = RecipeService.getStep(25, false);

        //then
        expect(result).toBe(null);
    }));

    it('should return the last step when the index is superior to the recipe length', inject(function($rootScope, RecipeService) {
        //given
        RecipeService.refresh();
        $rootScope.$digest();

        var expectedStep = RecipeService.getRecipe()[4];

        //when
        var result = RecipeService.getStep(25, true);

        //then
        expect(result).toBe(expectedStep);
    }));

    it('should return the last active step index', inject(function($rootScope, RecipeService) {
        //given
        RecipeService.refresh();
        $rootScope.$digest();

        RecipeService.disableStepsAfter(RecipeService.getRecipe()[2]);

        //when
        var index = RecipeService.getActiveThresholdStepIndex();

        //then
        expect(index).toBe(2);
    }));

    it('should return -1 when no specific active step has been set', inject(function($rootScope, RecipeService) {
        //given
        RecipeService.refresh();
        $rootScope.$digest();

        //when
        var index = RecipeService.getActiveThresholdStepIndex();

        //then
        expect(index).toBe(-1);
    }));

    it('should return the initial state if the index is 0', inject(function($rootScope, RecipeService) {
        //given
        RecipeService.refresh();
        $rootScope.$digest();

        //when
        var step = RecipeService.getStepBefore(0);

        //then
        expect(step).toEqual({ transformation: {stepId: 'f6e172c33bdacbc69bca9d32b2bd78174712a171' }});
    }));

    it('should return the last step if the index is bigger than the recipe size', inject(function($rootScope, RecipeService) {
        //given
        RecipeService.refresh();
        $rootScope.$digest();

        //when
        var step = RecipeService.getStepBefore(1000);

        //then
        expect(step).toEqual(RecipeService.getRecipe()[4]);
    }));

    it('should return the step before the one identified by the index', inject(function($rootScope, RecipeService) {
        //given
        RecipeService.refresh();
        $rootScope.$digest();

        //when
        var step = RecipeService.getStepBefore(2);

        //then
        expect(step).toEqual(RecipeService.getRecipe()[1]);
    }));
});