/*jshint camelcase: false */

describe('Recipe controller', function() {
    'use strict';

    var createController, scope;
    var previousStep = {};
    var lastActiveStep = {inactive: false};

    beforeEach(module('data-prep.recipe'));

    beforeEach(inject(function($rootScope, $controller, $q, RecipeService, PlaygroundService, PreparationService, DatasetPreviewService) {
        scope = $rootScope.$new();

        createController = function() {
            var ctrl =  $controller('RecipeCtrl', {
                $scope: scope
            });
            return ctrl;
        };

        spyOn($rootScope, '$emit').and.callThrough();
        spyOn(RecipeService, 'getPreviousStep').and.returnValue(previousStep);
        spyOn(RecipeService, 'getActiveThresholdStepIndex').and.returnValue(3);
        spyOn(RecipeService, 'refresh').and.returnValue($q.when(true));
        spyOn(RecipeService, 'getStep').and.callFake(function(index, lastDefault) {
            return (index === 3 && lastDefault) ? lastActiveStep : null;
        });
        spyOn(PreparationService, 'updateStep').and.returnValue($q.when(true));
        spyOn(PlaygroundService, 'loadStep').and.returnValue($q.when(true));
        spyOn(DatasetPreviewService, 'getPreviewAppendRecords').and.returnValue($q.when(true));
        spyOn(DatasetPreviewService, 'getPreviewDisableRecords').and.returnValue($q.when(true));
        spyOn(DatasetPreviewService, 'getPreviewUpdateRecords').and.returnValue($q.when(true));
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

    it('should create a closure that update the step parameters', inject(function($rootScope, PreparationService) {
        //given
        var ctrl = createController();
        var step = {
            column: {id: 'state'},
            transformation: {
                stepId: 'a598bc83fc894578a8b823',
                name: 'cut'
            },
            actionParameters: {
                action: 'cut',
                parameters: {pattern: '.', column_name: 'state'}
            }
        };
        var parameters = {pattern: '-'};

        //when
        var updateClosure = ctrl.stepUpdateClosure(step);
        updateClosure(parameters);
        $rootScope.$digest();

        //then
        expect(PreparationService.updateStep).toHaveBeenCalledWith('a598bc83fc894578a8b823', 'cut', parameters);
    }));

    it('should update step, refresh recipe, load last active step when parameters are different', inject(function($rootScope, PreparationService, RecipeService, PlaygroundService) {
        //given
        var ctrl = createController();
        var step = {
            column: {id: 'state'},
            transformation: {
                stepId: 'a598bc83fc894578a8b823',
                name: 'cut'
            },
            actionParameters: {
                action: 'cut',
                parameters: {pattern: '.', column_name: 'state'}
            }
        };
        var parameters = {pattern: '-'};

        //when
        ctrl.updateStep(step, parameters);
        expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.start');
        $rootScope.$digest();

        //then
        expect(PreparationService.updateStep).toHaveBeenCalledWith('a598bc83fc894578a8b823', 'cut', parameters);
        expect(RecipeService.refresh).toHaveBeenCalled();
        expect(PlaygroundService.loadStep).toHaveBeenCalledWith(lastActiveStep);
        expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.stop');
    }));

    it('should init params object with column id if param is not defined', inject(function($rootScope, PreparationService) {
        //given
        var ctrl = createController();
        var step = {
            column: {id: 'state'},
            transformation: {
                stepId: 'a598bc83fc894578a8b823',
                name: 'cut'
            },
            actionParameters: {
                action: 'cut',
                parameters: {pattern: '.', column_name: 'state'}
            }
        };

        //when
        ctrl.updateStep(step);
        $rootScope.$digest();

        //then
        expect(PreparationService.updateStep).toHaveBeenCalledWith('a598bc83fc894578a8b823', 'cut', {column_name: 'state'});
    }));

    it('should do nothing if parameters are unchanged', inject(function($rootScope, PreparationService, RecipeService, PlaygroundService) {
        //given
        var ctrl = createController();
        var step = {
            column: {id: 'state'},
            transformation: {
                stepId: 'a598bc83fc894578a8b823',
                name: 'cut'
            },
            actionParameters: {
                action: 'cut',
                parameters: {pattern: '.', column_name: 'state'}
            }
        };
        var parameters = {pattern: '.'};

        //when
        ctrl.updateStep(step, parameters);
        $rootScope.$digest();

        //then
        expect($rootScope.$emit).not.toHaveBeenCalled();
        expect(PreparationService.updateStep).not.toHaveBeenCalled();
        expect(RecipeService.refresh).not.toHaveBeenCalled();
        expect(PlaygroundService.loadStep).not.toHaveBeenCalled();
    }));
});
