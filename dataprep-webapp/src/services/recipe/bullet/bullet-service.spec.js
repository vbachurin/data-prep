/*jshint camelcase: false */

describe('Recipe controller', function() {
	'use strict';

	var createController, scope;
	var previousStep = {};
	var lastActiveStep = {inactive: false};

	beforeEach(module('data-prep.services.recipe'));

	beforeEach(inject(function($rootScope, $controller, $q, $timeout, BulletService, RecipeService, PlaygroundService, PreparationService, PreviewService) {
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
		spyOn($timeout, 'cancel').and.callThrough();
	}));

	afterEach(inject(function(RecipeService) {
		RecipeService.reset();
	}));

	it('should highlight active steps after the targeted one (included)', inject(function(RecipeService, BulletService) {
		//given
		var recipe = RecipeService.getRecipe();
	    recipe.push(
	        {},
	        {},
	        {},
	        {}
	    );

	    //when
		BulletService.stepHoverStart(1);

	    //then
	    expect(recipe[0].highlight).toBeFalsy();
	    expect(recipe[1].highlight).toBeTruthy();
	    expect(recipe[2].highlight).toBeTruthy();
	    expect(recipe[3].highlight).toBeTruthy();
	}));

	it('should highlight inactive steps before the targeted one (included)', inject(function(RecipeService, BulletService) {
		//given
		var recipe = RecipeService.getRecipe();
	    recipe.push(
	        {},
	        {inactive: true},
	        {inactive: true},
	        {inactive: true}
	    );

	    //when
		BulletService.stepHoverStart(2);

	    //then
	    expect(recipe[0].highlight).toBeFalsy();
	    expect(recipe[1].highlight).toBeTruthy();
	    expect(recipe[2].highlight).toBeTruthy();
	    expect(recipe[3].highlight).toBeFalsy();
	}));

	it('should trigger append preview on inactive step hover after a delay of 100ms', inject(function($timeout, RecipeService, PreviewService, BulletService) {
		//given
		var recipe = RecipeService.getRecipe();
	    recipe.push(
	        {id: '1'},
	        {id: '2'},
	        {id: '3'},
	        {id: '4'}
	    );
	    RecipeService.disableStepsAfter(recipe[0]);

	    //when
		BulletService.stepHoverStart(2);
	    $timeout.flush(99);
	    expect(PreviewService.getPreviewDiffRecords).not.toHaveBeenCalled();
	    $timeout.flush(1);

	    //then
	    expect(PreviewService.getPreviewDiffRecords).toHaveBeenCalledWith(recipe[0], recipe[2]);
	}));

	it('should cancel pending preview action on step hover', inject(function($timeout, RecipeService, BulletService) {
	    //given
	    var recipe = RecipeService.getRecipe();
	    recipe.push(
	        {id: '1'},
	        {id: '2'},
	        {id: '3'},
	        {id: '4'}
	    );

	    //when
		BulletService.stepHoverStart(2);

	    //then
	    expect($timeout.cancel).toHaveBeenCalled();
	}));


	it('should trigger disable preview on active step hover', inject(function($timeout, RecipeService, PreviewService, BulletService) {
	    //given
	    var recipe = RecipeService.getRecipe();
	    recipe.push(
	        {id: '1'},
	        {id: '2'},
	        {id: '3'},
	        {id: '4'}
	    );

	    //when
		BulletService.stepHoverStart(2);
	    $timeout.flush(99);
	    expect(PreviewService.getPreviewDiffRecords).not.toHaveBeenCalled();
	    $timeout.flush(1);

	    //then
	    expect(PreviewService.getPreviewDiffRecords).toHaveBeenCalledWith(recipe[3], recipe[1]);
	}));

	it('should remove highlight on mouse hover end', inject(function(RecipeService, BulletService) {
	    //given
	    var recipe = RecipeService.getRecipe();
	    recipe.push(
	        {},
	        {highlight: true},
	        {highlight: true},
	        {highlight: true}
	    );

	    //when
		BulletService.stepHoverEnd();

	    //then
	    expect(recipe[0].highlight).toBeFalsy();
	    expect(recipe[1].highlight).toBeFalsy();
	    expect(recipe[2].highlight).toBeFalsy();
	    expect(recipe[3].highlight).toBeFalsy();
	}));

	it('should cancel current preview on mouse hover end after a delay of 100ms', inject(function($timeout, PreviewService, BulletService) {
	    //given
	    //when
		BulletService.stepHoverEnd();
	    $timeout.flush(99);
	    expect(PreviewService.cancelPreview).not.toHaveBeenCalled();
	    $timeout.flush(1);

	    //then
	    expect(PreviewService.cancelPreview).toHaveBeenCalled();
	}));

	it('should cancel pending preview action on mouse hover end', inject(function($timeout, BulletService) {
	    //given

	    //when
		BulletService.stepHoverEnd();

	    //then
	    expect($timeout.cancel).toHaveBeenCalled();
	}));

	it('should load current step content if the step is first inactive', inject(function(PlaygroundService, BulletService) {
	    //given
	    var step = {inactive: true};

	    //when
		BulletService.toggleStep(step);

	    //then
	    expect(PlaygroundService.loadStep).toHaveBeenCalledWith(step);
	}));

	it('should load previous step content if the step is first active', inject(function(PlaygroundService, BulletService) {
	    //given
	    var step = {inactive: false};

	    //when
		BulletService.toggleStep(step);

	    //then
	    expect(PlaygroundService.loadStep).toHaveBeenCalledWith(previousStep);
	}));

	it('should cancel current preview on toggle', inject(function(PreviewService, BulletService) {
	    //given
	    var step = {inactive: true};

	    //when
		BulletService.toggleStep(step);

	    //then
	    expect(PreviewService.cancelPreview).toHaveBeenCalled();
	}));
});
