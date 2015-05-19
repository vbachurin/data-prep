describe('Export directive', function() {
    'use strict';

    var scope, element, ctrl;

    beforeEach(module('data-prep.export'));
    beforeEach(module('htmlTemplates'));

    beforeEach(inject(function($rootScope, $compile) {
        scope = $rootScope.$new();
        element = angular.element('<export></export>');
        $compile(element)(scope);
        scope.$digest();

        ctrl = element.controller('export');
    }));

    afterEach(function() {
        scope.$destroy();
        element.remove();
    });

    it('should bind csvSeparator form value to controller', function() {
        //given
        var input = angular.element('body').find('#csvExport').eq(0)[0].csvSeparator;
        expect(input.value).toBe(';');

        //when
        ctrl.csvSeparator = ',';
        scope.$digest();

        //then
        expect(input.value).toBe(',');
    });

    it('should bind preparationId form value to controller', inject(function(PreparationService) {
        //given
        var input = angular.element('body').find('#csvExport').eq(0)[0].preparationId;
        expect(input.value).toBeFalsy();

        //when
        PreparationService.currentPreparationId = '48da64513c43a548e678bc99';
        scope.$digest();

        //then
        expect(input.value).toBe('48da64513c43a548e678bc99');
    }));

    it('should bind stepId form value to controller', inject(function(RecipeService) {
        //given
        var input = angular.element('body').find('#csvExport').eq(0)[0].stepId;
        expect(input.value).toBeFalsy();

        //when
        RecipeService.getRecipe().push({
            transformation : {
                stepId: '48da64513c43a548e678bc99'
            }
        });
        scope.$digest();

        //then
        expect(input.value).toBe('48da64513c43a548e678bc99');
    }));

    it('should bind datasetId form value to controller', inject(function(PlaygroundService) {
        //given
        var input = angular.element('body').find('#csvExport').eq(0)[0].datasetId;
        expect(input.value).toBeFalsy();

        //when
        PlaygroundService.currentMetadata = {
            id: '48da64513c43a548e678bc99'
        };
        scope.$digest();

        //then
        expect(input.value).toBe('48da64513c43a548e678bc99');
    }));

    it('should set actionUrl and submit form', inject(function() {
        //given
        var form = angular.element('body').find('#csvExport').eq(0)[0];
        spyOn(form, 'submit').and.returnValue(null);

        var actionUrl = 'http://toto/export';
        ctrl.exportUrl = actionUrl;

        //when
        ctrl.export();

        //then
        expect(form.action).toBe(actionUrl);
        expect(form.submit).toHaveBeenCalled();
    }));
});