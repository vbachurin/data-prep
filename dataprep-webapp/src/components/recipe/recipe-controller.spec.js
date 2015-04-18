describe('Recipe controller', function() {
    'use strict';

    var createController, scope;
    var previousStep = {};

    beforeEach(module('data-prep.recipe'));

    beforeEach(inject(function($rootScope, $controller, RecipeService, PlaygroundService) {
        scope = $rootScope.$new();

        createController = function() {
            var ctrl =  $controller('RecipeCtrl', {
                $scope: scope
            });
            return ctrl;
        };

        spyOn(RecipeService, 'getPreviousStep').and.returnValue(previousStep);
        spyOn(PlaygroundService, 'loadStep').and.returnValue(previousStep);
    }));

    afterEach(inject(function(RecipeService) {
        var recipe = RecipeService.getRecipe();
        recipe.splice(0, recipe.length);
    }));

    it('should bind recipe getter with RecipeService', inject(function(RecipeService) {
        //given
        var ctrl = createController();
        expect(ctrl.recipe).toEqual([]);

        var column = {id: 'colId'};
        var transformation = {
            name: 'split',
            category: 'split',
            parameters: [],
            items: []
        };

        //when
        RecipeService.getRecipe().push({
            column: column,
            transformation: transformation
        });

        //then
        expect(ctrl.recipe.length).toBe(1);
        expect(ctrl.recipe[0].column).toBe(column);
        expect(ctrl.recipe[0].transformation).toEqual(transformation);
    }));

    it('should highlight active steps after the targeted one (included)', inject(function(RecipeService) {
        //given
        var ctrl = createController();

        var recipe = RecipeService.getRecipe();
        recipe.push(
            {},
            {},
            {},
            {}
        );

        //when
        ctrl.stepHoverStart(1);

        //then
        expect(recipe[0].highlight).toBeFalsy();
        expect(recipe[1].highlight).toBeTruthy();
        expect(recipe[2].highlight).toBeTruthy();
        expect(recipe[3].highlight).toBeTruthy();
    }));

    it('should highlight inactive steps before the targeted one (included)', inject(function(RecipeService) {
        //given
        var ctrl = createController();

        var recipe = RecipeService.getRecipe();
        recipe.push(
            {},
            {inactive: true},
            {inactive: true},
            {inactive: true}
        );

        //when
        ctrl.stepHoverStart(2);

        //then
        expect(recipe[0].highlight).toBeFalsy();
        expect(recipe[1].highlight).toBeTruthy();
        expect(recipe[2].highlight).toBeTruthy();
        expect(recipe[3].highlight).toBeFalsy();
    }));

    it('should remove highlight on mouse hover end', inject(function(RecipeService) {
        //given
        var ctrl = createController();

        var recipe = RecipeService.getRecipe();
        recipe.push(
            {},
            {highlight: true},
            {highlight: true},
            {highlight: true}
        );

        //when
        ctrl.stepHoverEnd();

        //then
        expect(recipe[0].highlight).toBeFalsy();
        expect(recipe[1].highlight).toBeFalsy();
        expect(recipe[2].highlight).toBeFalsy();
        expect(recipe[3].highlight).toBeFalsy();
    }));

    it('should load current step content if the step is first inactive', inject(function(PlaygroundService) {
        //given
        var ctrl = createController();
        var step = {inactive: true};

        //when
        ctrl.toggleStep(step);

        //then
        expect(PlaygroundService.loadStep).toHaveBeenCalledWith(step);
    }));

    it('should load previous step content if the step is first active', inject(function(PlaygroundService) {
        //given
        var ctrl = createController();
        var step = {inactive: false};

        //when
        ctrl.toggleStep(step);

        //then
        expect(PlaygroundService.loadStep).toHaveBeenCalledWith(previousStep);
    }));
});
