/*jshint camelcase: false */

describe('Recipe service', function () {
    'use strict';

    var preparationDetails = {
        "id": "fbaa18e82e913e97e5f0e9d40f04413412be1126",
        "dataSetId": "8ec053b1-7870-4bc6-af54-523be91dc774",
        "author": "anonymousUser",
        "creationDate": 1427447330693,
        "steps": [
            "47e2444dd1301120b539804507fd307072294048",
            "ae1aebf4b3fa9b983c895486612c02c766305410",
            "24dcd68f2117b9f93662cb58cc31bf36d6e2867a",
            "599725f0e1331d5f8aae24f22cd1ec768b10348d"
        ],
        "actions": [
            {
                "action": "cut",
                "parameters": {
                    "pattern": "-",
                    "column_name": "birth"
                }
            },
            {
                "action": "fillemptywithdefault",
                "parameters": {
                    "default_value": 0,
                    "column_name": "revenue"
                }
            },
            {
                "action": "uppercase",
                "parameters": {
                    "column_name": "lastname"
                }
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

    it('should save current values (or default if not defined) and add recipe item', inject(function(RecipeService) {
        //given
        var column = {
            id: 'colId'
        };

        var transformation = {
            name: 'split',
            category: 'split',
            parameters: [
                {name: 'param1', type: 'text', default: '', value : 'myParam1'},
                {name: 'param2', type: 'integer', default: 5}
            ],
            items: [{
                name: 'mode',
                type: 'LIST',
                values: [
                    {
                        name: 'regex',
                        parameters : [
                            {name: 'regex', type: 'text', default: '', value: 'param1Value'},
                            {name: 'comment', type: 'text', default: '', value: 'my comment'}
                        ]
                    },
                    {name: 'index'}
                ]
            }]
        };
        transformation.items[0].selectedValue = transformation.items[0].values[1];

        //when
        RecipeService.add(column, transformation);
        var recipe = RecipeService.getRecipe();

        //then
        expect(recipe[0].transformation.parameters[0].initialValue).toBe('myParam1');
        expect(recipe[0].transformation.parameters[1].initialValue).toBe(5);
        expect(recipe[0].transformation.items[0].initialValue).toBe(recipe[0].transformation.items[0].values[1]);
        expect(recipe[0].transformation.items[0].values[0].parameters[0].initialValue).toBe('param1Value');
        expect(recipe[0].transformation.items[0].values[0].parameters[1].initialValue).toBe('my comment');
    }));

    it('should reset current values to initial saved values', inject(function(RecipeService) {
        //given
        var column = {
            id: 'colId'
        };

        var transformation = {
            name: 'split',
            category: 'split',
            parameters: [
                {name: 'param1', type: 'text', default: '', value : 'myParam1'},
                {name: 'param2', type: 'integer', default: 5}
            ],
            items: [{
                name: 'mode',
                type: 'LIST',
                values: [
                    {
                        name: 'regex',
                        parameters : [
                            {name: 'regex', type: 'text', default: '', value: 'param1Value'},
                            {name: 'comment', type: 'text', default: '', value: 'my comment'}
                        ]
                    },
                    {name: 'index'}
                ]
            }]
        };
        transformation.items[0].selectedValue = transformation.items[0].values[1];

        RecipeService.add(column, transformation);
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

    it('should get recipe from preparation and init recipe items', inject(function($rootScope, RecipeService) {
        //given

        //when
        RecipeService.refresh();
        $rootScope.$digest();

        //then
        var recipe = RecipeService.getRecipe();
        expect(recipe.length).toBe(3);
        expect(recipe[0].column.id).toBe('birth');
        expect(recipe[0].transformation.stepId).toBe('ae1aebf4b3fa9b983c895486612c02c766305410');
        expect(recipe[0].transformation.name).toBe('cut');
        expect(recipe[0].transformation.parameters).toEqual([{label: 'pattern', name: 'pattern', type: 'string', inputType: 'text', initialValue: '-', default: '-'}]);

        expect(recipe[1].column.id).toBe('revenue');
        expect(recipe[1].transformation.stepId).toBe('24dcd68f2117b9f93662cb58cc31bf36d6e2867a');
        expect(recipe[1].transformation.name).toBe('fillemptywithdefault');
        expect(recipe[1].transformation.parameters).toEqual([{label: 'default_value', name: 'default_value', type: 'numeric', inputType: 'number', initialValue: 0, default: 0}]);

        expect(recipe[2].column.id).toBe('lastname');
        expect(recipe[2].transformation.stepId).toBe('599725f0e1331d5f8aae24f22cd1ec768b10348d');
        expect(recipe[2].transformation.name).toBe('uppercase');
        expect(recipe[2].transformation.parameters).toBeFalsy();
    }));
});