describe('Playground Service', function () {
    'use strict';

    var $httpBackend;

    beforeEach(module('data-prep.services.playground'));

    beforeEach(inject(function ($injector, $q, DatasetRestService, FilterService, RecipeService, DatasetGridService, PreparationRestService) {
        $httpBackend = $injector.get('$httpBackend');

        spyOn(DatasetRestService, 'getDataFromId').and.callThrough();
        spyOn(FilterService, 'removeAllFilters').and.callFake(function() {});
        spyOn(RecipeService, 'reset').and.callFake(function() {});
        spyOn(DatasetGridService, 'setDataset').and.callFake(function() {});
        spyOn(PreparationRestService, 'create').and.returnValue($q.when(true));
        spyOn(PreparationRestService, 'update').and.returnValue($q.when(true));
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
        var data = {column: [], records: []};
        var assertNewPreparationInitialization;

        beforeEach(inject(function(PlaygroundService, DatasetRestService, FilterService, RecipeService, DatasetGridService, RestURLs) {
            assertNewPreparationInitialization = function() {
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

        it('should init a new preparation and show playground when there is no loaded data yet', inject(function($rootScope, PlaygroundService, PreparationRestService) {
            //given
            expect(PlaygroundService.visible).toBe(false);
            expect(PlaygroundService.currentMetadata).toBeFalsy();
            expect(PlaygroundService.currentData).toBeFalsy();
            expect(PreparationRestService.currentPreparation).toBeFalsy();
            expect(PreparationRestService.preparationName).toBeFalsy();
            expect(PreparationRestService.originalPreparationName).toBeFalsy();

            //when
            PlaygroundService.initPlayground(dataset);
            $httpBackend.flush();
            $rootScope.$digest();

            //then
            assertNewPreparationInitialization();
        }));

        it('should init a new preparation and show playground when there is already a created preparation yet', inject(function($rootScope, PlaygroundService, PreparationRestService) {
            //given
            PlaygroundService.currentMetadata = {id : 'e85afAa78556d5425bc2'};
            PreparationRestService.currentPreparation = {};

            expect(PlaygroundService.visible).toBe(false);
            expect(PlaygroundService.currentMetadata).toBeTruthy();
            expect(PlaygroundService.currentData).toBeFalsy();
            expect(PreparationRestService.currentPreparation).toBeTruthy();

            //when
            PlaygroundService.initPlayground(dataset);
            $httpBackend.flush();
            $rootScope.$digest();

            //then
            assertNewPreparationInitialization();
        }));

        it('should init a new preparation and show playground when the loaded dataset is not the wanted dataset', inject(function($rootScope, PlaygroundService, PreparationRestService) {
            //given
            PlaygroundService.currentMetadata = {id : 'ab45420c09bf98d9a90'};

            expect(PlaygroundService.visible).toBe(false);
            expect(PlaygroundService.currentMetadata).toBeTruthy();
            expect(PlaygroundService.currentData).toBeFalsy();
            expect(PreparationRestService.currentPreparation).toBeFalsy();

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

        it('should init playground when the wanted dataset is loaded and no preparation was created yet', inject(function($rootScope, PlaygroundService, FilterService, RecipeService, DatasetGridService) {
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
            expect(PlaygroundService.currentMetadata).toBe(dataset);
            expect(PlaygroundService.currentData).toBe(data);
            expect(FilterService.removeAllFilters).not.toHaveBeenCalled();
            expect(RecipeService.reset).not.toHaveBeenCalled();
            expect(DatasetGridService.setDataset).not.toHaveBeenCalled();
        }));
    });

    it('should create preparation with provided name when there is no preparation yet', inject(function($rootScope, PlaygroundService, PreparationRestService) {
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
        expect(PreparationRestService.create).toHaveBeenCalledWith(dataset.id, name);
        expect(PlaygroundService.preparationName).toBe(name);
        expect(PlaygroundService.originalPreparationName).toBe(name);
    }));

    it('should update preparation with provided name when there is loaded preparation', inject(function($rootScope, PlaygroundService, PreparationRestService) {
        //given
        var name = 'My preparation';
        var newName = 'My new preparation name';
        PreparationRestService.currentPreparation = 'e85afAa78556d5425bc2';

        PlaygroundService.preparationName = name;
        PlaygroundService.originalPreparationName = name;

        //when
        PlaygroundService.preparationName = newName;
        PlaygroundService.createOrUpdatePreparation(newName);
        $rootScope.$digest();

        //then
        expect(PreparationRestService.create).not.toHaveBeenCalled();
        expect(PreparationRestService.update).toHaveBeenCalledWith(newName);
        expect(PlaygroundService.preparationName).toBe(newName);
        expect(PlaygroundService.originalPreparationName).toBe(newName);
    }));

    describe('load existing dataset', function() {
        var data = {
            records: [{id: '0', firstname: 'toto'}, {id: '1', firstname: 'tata'}, {id: '2', firstname: 'titi'}]
        };

        beforeEach(inject(function($rootScope, $q, PreparationRestService, RecipeService) {
            spyOn($rootScope, '$emit').and.callThrough();
            spyOn(PreparationRestService, 'getContent').and.returnValue($q.when({data: data}));
            spyOn(RecipeService, 'refresh').and.callFake(function() {});
            spyOn(RecipeService, 'disableStepsAfter').and.callFake(function() {});
        }));

        it('should load existing dataset', inject(function($rootScope, PlaygroundService, FilterService, RecipeService, DatasetGridService) {
            //given
            var preparation = {
                dataset: {id: '1', name: 'my dataset'}
            };

            //when
            PlaygroundService.load(preparation);
            expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.start');
            $rootScope.$apply();

            //then
            expect(PlaygroundService.currentMetadata).toBe(preparation.dataset);
            expect(PlaygroundService.currentData).toBe(data);
            expect(FilterService.removeAllFilters).toHaveBeenCalled();
            expect(RecipeService.refresh).toHaveBeenCalled();
            expect(DatasetGridService.setDataset).toHaveBeenCalledWith(preparation.dataset, data);
            expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.stop');
        }));

        it('should load preparation content at a specific spec', inject(function($rootScope, PlaygroundService, FilterService, RecipeService, DatasetGridService) {
            //given
            var step = {
                transformation: {stepId: 'a4353089cb0e039ac2'}
            };
            var metadata = {id: '1', name: 'my dataset'};
            PlaygroundService.currentMetadata = metadata;

            //when
            PlaygroundService.loadStep(step);
            expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.start');
            $rootScope.$apply();

            //then
            expect(PlaygroundService.currentMetadata).toBe(metadata);
            expect(PlaygroundService.currentData).toBe(data);
            expect(FilterService.removeAllFilters).not.toHaveBeenCalled();
            expect(RecipeService.refresh).not.toHaveBeenCalled();
            expect(RecipeService.disableStepsAfter).toHaveBeenCalledWith(step);
            expect(DatasetGridService.setDataset).toHaveBeenCalledWith(metadata, data);
            expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.stop');
        }));

        it('should do nothing if current step (threshold between active and inactive) is already selected', inject(function($rootScope, PlaygroundService, RecipeService, PreparationRestService) {
            //given
            var step = {
                transformation: {stepId: 'a4353089cb0e039ac2'}
            };
            spyOn(RecipeService, 'getActiveThresholdStep').and.returnValue(step);

            //when
            PlaygroundService.loadStep(step);

            //then
            expect($rootScope.$emit).not.toHaveBeenCalledWith('talend.loading.start');
            expect(PreparationRestService.getContent).not.toHaveBeenCalled();
        }));
    });

    it('should do nothing when provided name is the original name', inject(function($rootScope, PlaygroundService, PreparationRestService) {
        //given
        var name = 'My preparation';
        var newName = name;

        PlaygroundService.originalPreparationName = name;
        PlaygroundService.preparationName = newName;

        //when
        PlaygroundService.createOrUpdatePreparation(newName);
        $rootScope.$digest();

        //then
        expect(PreparationRestService.create).not.toHaveBeenCalled();
        expect(PreparationRestService.update).not.toHaveBeenCalled();
        expect(PlaygroundService.preparationName).toBe(name);
        expect(PlaygroundService.originalPreparationName).toBe(name);
    }));
});