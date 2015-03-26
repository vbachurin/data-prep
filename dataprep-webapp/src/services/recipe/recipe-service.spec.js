/*jshint camelcase: false */

describe('Recipe service', function () {
    'use strict';

    beforeEach(module('data-prep.services.recipe'));

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
});