describe('Playground Service', function () {
    'use strict';

    var $httpBackend;

    beforeEach(module('data-prep.services.playground'));

    beforeEach(inject(function ($injector, $q, DatasetService, FilterService, RecipeService, DatasetGridService, PreparationService) {
        $httpBackend = $injector.get('$httpBackend');

        spyOn(DatasetService, 'getDataFromId').and.callThrough();
        spyOn(FilterService, 'removeAllFilters').and.callFake(function() {});
        spyOn(RecipeService, 'reset').and.callFake(function() {});
        spyOn(DatasetGridService, 'setDataset').and.callFake(function() {});
        spyOn(PreparationService, 'create').and.returnValue($q.when(true));
    }));

    it('should init visible flag to false', inject(function(PlaygroundService) {
        //then
        expect(PlaygroundService.visible).toBe(false);
    }));

    it('should set visible flag to true', inject(function(PlaygroundService) {
        //given
        PlaygroundService.visible = false;

        //when
        PlaygroundService.show();

        //then
        expect(PlaygroundService.visible).toBe(true);
    }));

    it('should set visible flag to true', inject(function(PlaygroundService) {
        //given
        PlaygroundService.visible = true;

        //when
        PlaygroundService.hide();

        //then
        expect(PlaygroundService.visible).toBe(false);
    }));

    describe('init new preparation', function() {
        var dataset = {id: 'e85afAa78556d5425bc2'};
        var data = [{column: [], records: []}];
        var assertNewPreparationInitialization;

        beforeEach(inject(function(PlaygroundService, DatasetService, FilterService, RecipeService, DatasetGridService, RestURLs) {
            assertNewPreparationInitialization = function() {
                expect(PlaygroundService.visible).toBe(true);
                expect(PlaygroundService.currentMetadata).toEqual(dataset);
                expect(PlaygroundService.currentData).toEqual(data);
                expect(FilterService.removeAllFilters).toHaveBeenCalled();
                expect(RecipeService.reset).toHaveBeenCalled();
                expect(DatasetGridService.setDataset).toHaveBeenCalledWith(dataset, data);
            };

            $httpBackend
                .expectGET(RestURLs.datasetUrl + '/e85afAa78556d5425bc2?metadata=false')
                .respond(200, data);
        }));

        it('should init a new preparation and show playground when there is no loaded data yet', inject(function($rootScope, PlaygroundService, PreparationService) {
            //given
            expect(PlaygroundService.visible).toBe(false);
            expect(PlaygroundService.currentMetadata).toBeFalsy();
            expect(PlaygroundService.currentData).toBeFalsy();
            expect(PreparationService.currentPreparation).toBeFalsy();
            expect(PreparationService.preparationName).toBeFalsy();
            expect(PreparationService.originalPreparationName).toBeFalsy();

            //when
            PlaygroundService.initPlayground(dataset);
            $httpBackend.flush();
            $rootScope.$digest();

            //then
            assertNewPreparationInitialization();
        }));

        it('should init a new preparation and show playground when there is already a created preparation yet', inject(function($rootScope, PlaygroundService, PreparationService) {
            //given
            PlaygroundService.currentMetadata = {id : 'e85afAa78556d5425bc2'};
            PreparationService.currentPreparation = {};

            expect(PlaygroundService.visible).toBe(false);
            expect(PlaygroundService.currentMetadata).toBeTruthy();
            expect(PlaygroundService.currentData).toBeFalsy();
            expect(PreparationService.currentPreparation).toBeTruthy();

            //when
            PlaygroundService.initPlayground(dataset);
            $httpBackend.flush();
            $rootScope.$digest();

            //then
            assertNewPreparationInitialization();
        }));

        it('should init a new preparation and show playground when the loaded dataset is not the wanted dataset', inject(function($rootScope, PlaygroundService, PreparationService) {
            //given
            PlaygroundService.currentMetadata = {id : 'ab45420c09bf98d9a90'};

            expect(PlaygroundService.visible).toBe(false);
            expect(PlaygroundService.currentMetadata).toBeTruthy();
            expect(PlaygroundService.currentData).toBeFalsy();
            expect(PreparationService.currentPreparation).toBeFalsy();

            //when
            PlaygroundService.initPlayground(dataset);
            $httpBackend.flush();
            $rootScope.$digest();

            //then
            assertNewPreparationInitialization();
        }));

        it('should reset preparation name', inject(function($rootScope, PlaygroundService) {
            //given
            PlaygroundService.preparationName = 'preparation name';
            PlaygroundService.originalPreparationName = 'preparation name';

            //when
            PlaygroundService.initPlayground(dataset);
            $httpBackend.flush();
            $rootScope.$digest();

            //then
            expect(PlaygroundService.preparationName).toBeFalsy();
            expect(PlaygroundService.originalPreparationName).toBeFalsy();
        }));

        it('should show playground when the wanted dataset is loaded and no preparation was created yet', inject(function($rootScope, PlaygroundService, FilterService, RecipeService, DatasetGridService) {
            //given
            var dataset = {id: 'e85afAa78556d5425bc2'};
            var data = [{column: [], records: []}];
            PlaygroundService.currentMetadata = dataset;
            PlaygroundService.currentData = data;

            expect(PlaygroundService.visible).toBe(false);
            expect(PlaygroundService.currentPreparation).toBeFalsy();

            //when
            PlaygroundService.initPlayground(dataset);
            $rootScope.$digest();

            //then
            expect(PlaygroundService.visible).toBe(true);
            expect(PlaygroundService.currentMetadata).toBe(dataset);
            expect(PlaygroundService.currentData).toBe(data);
            expect(FilterService.removeAllFilters).not.toHaveBeenCalled();
            expect(RecipeService.reset).not.toHaveBeenCalled();
            expect(DatasetGridService.setDataset).not.toHaveBeenCalled();
        }));
    });

    it('should create preparation with provided name when there is no preparation yet', inject(function($rootScope, PlaygroundService, PreparationService) {
        //given
        var name = 'My preparation';
        var dataset = {id: 'e85afAa78556d5425bc2'};
        PlaygroundService.currentMetadata = dataset;

        expect(PlaygroundService.preparationName).toBeFalsy();
        expect(PlaygroundService.originalPreparationName).toBeFalsy();

        //when
        PlaygroundService.createOrUpdatePreparation(name);
        $rootScope.$digest();

        //then
        expect(PreparationService.create).toHaveBeenCalledWith(dataset.id, name);
        expect(PlaygroundService.preparationName).toBe(name);
        expect(PlaygroundService.originalPreparationName).toBe(name);
    }));
});