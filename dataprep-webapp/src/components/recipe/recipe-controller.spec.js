/*jshint camelcase: false */

describe('Recipe controller', function() {
    'use strict';

    var createController, scope;
    var previousStep = {};
    var lastActiveStep = {inactive: false};

    beforeEach(module('data-prep.recipe'));

    beforeEach(inject(function($rootScope, $controller, $q, RecipeService, PlaygroundService, PreparationService, PreviewService) {
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
        spyOn(RecipeService, 'refresh').and.callFake(function() {
            RecipeService.reset();
            RecipeService.getRecipe().push(lastActiveStep);
        });
        spyOn(PreparationService, 'updateStep').and.returnValue($q.when(true));
        spyOn(PlaygroundService, 'loadStep').and.returnValue($q.when(true));
        spyOn(PreviewService, 'getPreviewDiffRecords').and.returnValue($q.when(true));
        spyOn(PreviewService, 'getPreviewUpdateRecords').and.returnValue($q.when(true));
        spyOn(PreviewService, 'cancelPreview').and.returnValue(null);
    }));

    afterEach(inject(function(RecipeService) {
        RecipeService.reset();
        //var recipe = RecipeService.getRecipe();
        //recipe.splice(0, recipe.length);
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

    it('should trigger append preview on inactive step hover', inject(function(RecipeService, PreviewService) {
        //given
        var ctrl = createController();

        var recipe = RecipeService.getRecipe();
        recipe.push(
            {id: '1'},
            {id: '2'},
            {id: '3'},
            {id: '4'}
        );
        RecipeService.disableStepsAfter(recipe[0]);

        //when
        ctrl.stepHoverStart(2);

        //then
        expect(PreviewService.getPreviewDiffRecords).toHaveBeenCalledWith(recipe[0], recipe[2]);
    }));

    it('should trigger disable preview on active step hover', inject(function(RecipeService, PreviewService) {
        //given
        var ctrl = createController();

        var recipe = RecipeService.getRecipe();
        recipe.push(
            {id: '1'},
            {id: '2'},
            {id: '3'},
            {id: '4'}
        );

        //when
        ctrl.stepHoverStart(2);

        //then
        expect(PreviewService.getPreviewDiffRecords).toHaveBeenCalledWith(recipe[3], recipe[1]);
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

    it('should cancel current preview on mouse hover end', inject(function(PreviewService) {
        //given
        var ctrl = createController();

        //when
        ctrl.stepHoverEnd();

        //then
        expect(PreviewService.cancelPreview).toHaveBeenCalled();
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

    it('should cancel current preview on toggle', inject(function(PreviewService) {
        //given
        var ctrl = createController();
        var step = {inactive: true};

        //when
        ctrl.toggleStep(step);

        //then
        expect(PreviewService.cancelPreview).toHaveBeenCalled();
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
        expect(PreparationService.updateStep).toHaveBeenCalledWith(step, parameters);
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
        expect(PreparationService.updateStep).toHaveBeenCalledWith(step, parameters);
        expect(RecipeService.refresh).toHaveBeenCalled();
        expect(PlaygroundService.loadStep).toHaveBeenCalledWith(lastActiveStep);
        expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.stop');
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

    it('should do nothing on update preview if the step is inactive', inject(function($rootScope, PreviewService) {
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
            },
            inactive: true
        };
        var parameters = {pattern: '--'};
        var closure = ctrl.previewUpdateClosure(step);

        //when
        closure(parameters);
        $rootScope.$digest();

        //then
        expect(PreviewService.getPreviewUpdateRecords).not.toHaveBeenCalled();
    }));

    it('should do nothing on update preview if the params have not changed', inject(function($rootScope, PreviewService) {
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
        var closure = ctrl.previewUpdateClosure(step);

        //when
        closure(parameters);
        $rootScope.$digest();

        //then
        expect(PreviewService.getPreviewUpdateRecords).not.toHaveBeenCalled();
    }));

    it('should call update preview', inject(function($rootScope, PreviewService, RecipeService) {
        //given
        RecipeService.refresh(); //set last active step for the test : see mock
        $rootScope.$digest();

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
        var parameters = {pattern: '--'};
        var closure = ctrl.previewUpdateClosure(step);

        //when
        closure(parameters);
        $rootScope.$digest();

        //then
        expect(PreviewService.getPreviewUpdateRecords).toHaveBeenCalledWith(lastActiveStep, step, {pattern: '--', column_name: 'state'});
    }));
});
