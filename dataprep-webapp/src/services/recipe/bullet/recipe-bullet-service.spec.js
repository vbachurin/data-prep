describe('Recipe Bullet service', function () {
    'use strict';

    var createController, scope;
    var previousStep = {column:{id:'0003'}};
    var lastActiveStep = {inactive: false};

    beforeEach(module('data-prep.services.recipe'));

    beforeEach(inject(function ($rootScope, $controller, $q, $timeout, RecipeBulletService, RecipeService, PlaygroundService, PreparationService, PreviewService) {
        scope = $rootScope.$new();

        createController = function () {
            var ctrl = $controller('RecipeCtrl', {
                $scope: scope
            });
            return ctrl;
        };

        spyOn($rootScope, '$emit').and.callThrough();
        spyOn(RecipeService, 'getPreviousStep').and.returnValue(previousStep);
        spyOn(RecipeService, 'getActiveThresholdStepIndex').and.returnValue(3);
        spyOn(RecipeService, 'refresh').and.callFake(function () {
            RecipeService.reset();
            RecipeService.getRecipe().push(lastActiveStep);
        });
        spyOn(PreparationService, 'updateStep').and.returnValue($q.when(true));
        spyOn(PlaygroundService, 'loadStep').and.returnValue($q.when(true));
        spyOn(PreviewService, 'getPreviewDiffRecords').and.returnValue($q.when(true));
        spyOn(PreviewService, 'getPreviewUpdateRecords').and.returnValue($q.when(true));
        spyOn(PreviewService, 'cancelPreview').and.returnValue(null);
        spyOn($timeout, 'cancel').and.callThrough();
    }));

    it('should highlight active steps after the targeted one (included)', inject(function (RecipeService, RecipeBulletService) {
        //given
        var recipe = RecipeService.getRecipe();
        recipe.push(
            {column:{id:'0001'}},
            {column:{id:'0001'}},
            {column:{id:'0005'}},
            {column:{id:'0006'}}
        );

        //when
        RecipeBulletService.stepHoverStart(recipe[1]);

        //then
        expect(recipe[0].highlight).toBeFalsy();
        expect(recipe[1].highlight).toBeTruthy();
        expect(recipe[2].highlight).toBeTruthy();
        expect(recipe[3].highlight).toBeTruthy();
    }));

    it('should highlight inactive steps before the targeted one (included)', inject(function (RecipeService, RecipeBulletService) {
        //given
        var recipe = RecipeService.getRecipe();
        recipe.push(
            {},
            {inactive: true, column:{id:'0001'}},
            {inactive: true, column:{id:'0004'}},
            {inactive: true, column:{id:'0005'}}
        );

        //when
        RecipeBulletService.stepHoverStart(recipe[2]);

        //then
        expect(recipe[0].highlight).toBeFalsy();
        expect(recipe[1].highlight).toBeTruthy();
        expect(recipe[2].highlight).toBeTruthy();
        expect(recipe[3].highlight).toBeFalsy();
    }));

    it('should trigger append preview on inactive step hover after a delay of 100ms', inject(function ($timeout, RecipeService, PreviewService, RecipeBulletService) {
        //given
        var recipe = RecipeService.getRecipe();
        recipe.push(
            {id: '1', column:{id:'0002'}},
            {id: '2', column:{id:'0005'}},
            {id: '3', column:{id:'0004'}},
            {id: '4', column:{id:'0005'}}
        );
        RecipeService.disableStepsAfter(recipe[0]);

        //when
        RecipeBulletService.stepHoverStart(recipe[2]);
        $timeout.flush(99);
        expect(PreviewService.getPreviewDiffRecords).not.toHaveBeenCalled();
        $timeout.flush(1);

        //then
        expect(PreviewService.getPreviewDiffRecords).toHaveBeenCalledWith(recipe[0], recipe[2], '0004');
    }));

    it('should cancel pending preview action on step hover', inject(function ($timeout, RecipeService, RecipeBulletService) {
        //given
        var recipe = RecipeService.getRecipe();
        recipe.push(
            {id: '1', column:{id:'0002'}},
            {id: '2', column:{id:'0005'}},
            {id: '3', column:{id:'0004'}},
            {id: '4', column:{id:'0005'}}
        );

        //when
        RecipeBulletService.stepHoverStart(recipe[2]);

        //then
        expect($timeout.cancel).toHaveBeenCalled();
    }));

    it('should deactivate all the recipe', inject(function (RecipeService, PlaygroundService, RecipeBulletService) {
        //given
        var recipe = RecipeService.getRecipe();
        var step1 = {inactive: false, column:{id:'0005'}};
        var step2 = {inactive: false, column:{id:'0004'}};
        recipe.push(step1);
        recipe.push(step2);

        //when
        RecipeBulletService.toggleRecipe();

        //then
        expect(PlaygroundService.loadStep).toHaveBeenCalledWith(previousStep, step1);
    }));

    it('should reactivate all the recipe', inject(function (RecipeService, PlaygroundService, RecipeBulletService) {
        //given
        var recipe = RecipeService.getRecipe();
        var step1 = {inactive: true, column:{id:'0005'}};
        var step2 = {inactive: true, column:{id:'0004'}};
        recipe.push(step1);
        recipe.push(step2);

        //when
        RecipeBulletService.toggleRecipe();

        //then
        expect(PlaygroundService.loadStep).toHaveBeenCalledWith(step2);
    }));

    it('should reactivate the recipe at the last active step before deactivation action', inject(function (RecipeService, PlaygroundService, RecipeBulletService) {
        //given
        var recipe = RecipeService.getRecipe();
        var step1 = {inactive: true, column:{id:'0005'}};
        var step2 = {inactive: true, column:{id:'0004'}};
        recipe.push(step1);
        recipe.push(step2);

        RecipeBulletService.lastToggled = step1;

        //when
        RecipeBulletService.toggleRecipe();

        //then
        expect(PlaygroundService.loadStep).toHaveBeenCalledWith(step1);
    }));

    it('should trigger disable preview on active step hover', inject(function ($timeout, RecipeService, PreviewService, RecipeBulletService) {
        //given
        var recipe = RecipeService.getRecipe();
        recipe.push(
            {id: '1', column:{id:'0005'}},
            {id: '2', column:{id:'0004'}},
            {id: '3', column:{id:'0000'}},
            {id: '4', column:{id:'0001'}}
        );

        //when
        RecipeBulletService.stepHoverStart(recipe[2]);
        $timeout.flush(99);
        expect(PreviewService.getPreviewDiffRecords).not.toHaveBeenCalled();
        $timeout.flush(1);

        //then
        expect(PreviewService.getPreviewDiffRecords).toHaveBeenCalledWith(recipe[3], recipe[1], '0000');
    }));

    it('should remove highlight on mouse hover end', inject(function (RecipeService, RecipeBulletService) {
        //given
        var recipe = RecipeService.getRecipe();
        recipe.push(
            {},
            {highlight: true, column:{id:'0002'}},
            {highlight: true, column:{id:'0005'}},
            {highlight: true, column:{id:'0001'}}
        );
        var step = {column: {id: '0001'}};

        //when
        RecipeBulletService.stepHoverEnd(step);

        //then
        expect(recipe[0].highlight).toBeFalsy();
        expect(recipe[1].highlight).toBeFalsy();
        expect(recipe[2].highlight).toBeFalsy();
        expect(recipe[3].highlight).toBeFalsy();
    }));

    it('should cancel current preview on mouse hover end after a delay of 100ms', inject(function ($timeout, PreviewService, RecipeBulletService) {
        //given
        var step = {column: {id: '0001'}};

        //when
        RecipeBulletService.stepHoverEnd(step);
        $timeout.flush(99);
        expect(PreviewService.cancelPreview).not.toHaveBeenCalled();
        $timeout.flush(1);

        //then
        expect(PreviewService.cancelPreview).toHaveBeenCalled();
    }));

    it('should cancel pending preview action on mouse hover end', inject(function ($timeout, RecipeBulletService) {
        //given
        var step = {column: {id: '0001'}};

        //when
        RecipeBulletService.stepHoverEnd(step);

        //then
        expect($timeout.cancel).toHaveBeenCalled();
    }));

    it('should load current step content if the step is first inactive', inject(function (PlaygroundService, RecipeBulletService) {
        //given
        var step = {inactive: true, column:{id:'0001'}};

        //when
        RecipeBulletService.toggleStep(step);

        //then
        expect(PlaygroundService.loadStep).toHaveBeenCalledWith(step);
    }));

    it('should load previous step content if the step is first active', inject(function (PlaygroundService, RecipeBulletService) {
        //given
        var step = {inactive: false, column:{id:'0001'}};

        //when
        RecipeBulletService.toggleStep(step);

        //then
        expect(PlaygroundService.loadStep).toHaveBeenCalledWith(previousStep, step);
    }));

    it('should cancel current preview on toggle', inject(function (PreviewService, RecipeBulletService) {
        //given
        var step = {inactive: true, column:{id:'0001'}};

        //when
        RecipeBulletService.toggleStep(step);

        //then
        expect(PreviewService.cancelPreview).toHaveBeenCalled();
    }));
});
