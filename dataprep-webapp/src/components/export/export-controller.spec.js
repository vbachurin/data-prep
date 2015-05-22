describe('Export controller', function() {
    'use strict';

    var scope, createController;

    beforeEach(module('data-prep.export'));

    beforeEach(inject(function($rootScope, $controller) {
        scope = $rootScope.$new();

        createController = function () {
            var ctrl = $controller('ExportCtrl', {
                $scope: scope
            });
            return ctrl;
        };
    }));

    it('should init csv separator to semicolon', function() {
        //when
        var ctrl = createController();

        //then
        expect(ctrl.csvSeparator).toBe(';');
    });

    it('should init exportUrl', inject(function(RestURLs) {
        //when
        var ctrl = createController();

        //then
        expect(ctrl.exportUrl).toBe(RestURLs.exportUrl);
    }));

    it('should bind preparationId getter to PreparationService', inject(function(PreparationService) {
        //given
        var ctrl = createController();
        expect(ctrl.preparationId).toBeFalsy();

        //when
        PreparationService.currentPreparationId = '48da64513c43a548e678bc99';

        //then
        expect(ctrl.preparationId).toBe('48da64513c43a548e678bc99');
    }));

    it('should bind stepId getter to RecipeService', inject(function(RecipeService) {
        //given
        var ctrl = createController();
        expect(ctrl.stepId).toBeFalsy();

        //when
        RecipeService.getRecipe().push({
            transformation : {
                stepId: '48da64513c43a548e678bc99'
            }
        });

        //then
        expect(ctrl.stepId).toBe('48da64513c43a548e678bc99');
    }));

    it('should bind datasetId getter to PlaygroundService', inject(function(PlaygroundService) {
        //given
        var ctrl = createController();
        expect(ctrl.datasetId).toBeFalsy();

        //when
        PlaygroundService.currentMetadata = {
            id: '48da64513c43a548e678bc99'
        };

        //then
        expect(ctrl.datasetId).toBe('48da64513c43a548e678bc99');
    }));
});