describe('Playground Service', function () {
    'use strict';

    var content = {column: [], records: []};

    beforeEach(module('data-prep.services.playground'));

    beforeEach(inject(function ($injector, $q, DatasetService, FilterService, RecipeService, DatagridService, PreparationService) {
        spyOn(DatasetService, 'getContent').and.returnValue($q.when(content));
        spyOn(FilterService, 'removeAllFilters').and.callFake(function() {});
        spyOn(RecipeService, 'reset').and.callFake(function() {});
        spyOn(DatagridService, 'setDataset').and.callFake(function() {});
        spyOn(PreparationService, 'create').and.returnValue($q.when(true));
        spyOn(PreparationService, 'setName').and.returnValue($q.when(true));
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
        var assertNewPreparationInitialization;

        beforeEach(inject(function(PlaygroundService, DatasetService, FilterService, RecipeService, DatagridService) {
            assertNewPreparationInitialization = function() {
                expect(PlaygroundService.currentMetadata).toEqual(dataset);
                expect(PlaygroundService.currentData).toEqual(content);
                expect(FilterService.removeAllFilters).toHaveBeenCalled();
                expect(RecipeService.reset).toHaveBeenCalled();
                expect(DatagridService.setDataset).toHaveBeenCalledWith(dataset, content);
            };
        }));

        it('should init a new preparation and show playground when there is no loaded data yet', inject(function($rootScope, PlaygroundService, PreparationService) {
            //given
            expect(PlaygroundService.visible).toBe(false);
            expect(PlaygroundService.currentMetadata).toBeFalsy();
            expect(PlaygroundService.currentData).toBeFalsy();
            expect(PreparationService.currentPreparationId).toBeFalsy();
            expect(PreparationService.preparationName).toBeFalsy();
            expect(PreparationService.originalPreparationName).toBeFalsy();

            //when
            PlaygroundService.initPlayground(dataset);
            $rootScope.$digest();

            //then
            assertNewPreparationInitialization();
        }));

        it('should init a new preparation and show playground when there is already a created preparation yet', inject(function($rootScope, PlaygroundService, PreparationService) {
            //given
            PlaygroundService.currentMetadata = {id : 'e85afAa78556d5425bc2'};
            PreparationService.currentPreparationId = '12342305304543';

            expect(PlaygroundService.visible).toBe(false);
            expect(PlaygroundService.currentMetadata).toBeTruthy();
            expect(PlaygroundService.currentData).toBeFalsy();
            expect(PreparationService.currentPreparationId).toBeTruthy();

            //when
            PlaygroundService.initPlayground(dataset);
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
            expect(PreparationService.currentPreparationId).toBeFalsy();

            //when
            PlaygroundService.initPlayground(dataset);
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
            $rootScope.$digest();

            //then
            expect(PlaygroundService.preparationName).toBeFalsy();
            expect(PlaygroundService.originalPreparationName).toBeFalsy();
        }));

        it('should init playground when the wanted dataset is loaded and no preparation was created yet', inject(function($rootScope, PlaygroundService, FilterService, RecipeService, DatagridService) {
            //given
            var dataset = {id: 'e85afAa78556d5425bc2'};
            var data = [{column: [], records: []}];
            PlaygroundService.currentMetadata = dataset;
            PlaygroundService.currentData = data;

            expect(PlaygroundService.visible).toBe(false);
            expect(PlaygroundService.currentPreparationId).toBeFalsy();

            //when
            PlaygroundService.initPlayground(dataset);
            $rootScope.$digest();

            //then
            expect(PlaygroundService.currentMetadata).toBe(dataset);
            expect(PlaygroundService.currentData).toBe(data);
            expect(FilterService.removeAllFilters).not.toHaveBeenCalled();
            expect(RecipeService.reset).not.toHaveBeenCalled();
            expect(DatagridService.setDataset).not.toHaveBeenCalled();
        }));
    });

    it('should set new name to a new to the preparation', inject(function($rootScope, PlaygroundService, PreparationService) {
        //given
        var name = 'My preparation';
        var newName = 'My new preparation name';
        PreparationService.currentPreparationId = 'e85afAa78556d5425bc2';

        PlaygroundService.preparationName = name;
        PlaygroundService.originalPreparationName = name;
        PlaygroundService.currentMetadata = {id: '123d120394ab0c53'};

        //when
        PlaygroundService.preparationName = newName;
        PlaygroundService.createOrUpdatePreparation(newName);
        $rootScope.$digest();

        //then
        expect(PreparationService.create).not.toHaveBeenCalled();
        expect(PreparationService.setName).toHaveBeenCalledWith({id: '123d120394ab0c53'}, newName);
        expect(PlaygroundService.preparationName).toBe(newName);
        expect(PlaygroundService.originalPreparationName).toBe(newName);
    }));

    describe('load existing dataset', function() {
        var data = {
            records: [{id: '0', firstname: 'toto'}, {id: '1', firstname: 'tata'}, {id: '2', firstname: 'titi'}]
        };

        beforeEach(inject(function($rootScope, $q, PreparationService, RecipeService) {
            spyOn($rootScope, '$emit').and.callThrough();
            spyOn(PreparationService, 'getContent').and.returnValue($q.when({data: data}));
            spyOn(RecipeService, 'refresh').and.callFake(function() {});
            spyOn(RecipeService, 'disableStepsAfter').and.callFake(function() {});
        }));

        it('should load existing dataset', inject(function($rootScope, PlaygroundService, FilterService, RecipeService, DatagridService) {
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
            expect(DatagridService.setDataset).toHaveBeenCalledWith(preparation.dataset, data);
            expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.stop');
        }));

        it('should load preparation content at a specific spec', inject(function($rootScope, PlaygroundService, FilterService, RecipeService, DatagridService) {
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
            expect(DatagridService.setDataset).toHaveBeenCalledWith(metadata, data);
            expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.stop');
        }));

        it('should do nothing if current step (threshold between active and inactive) is already selected', inject(function($rootScope, PlaygroundService, RecipeService, PreparationService) {
            //given
            var step = {
                transformation: {stepId: 'a4353089cb0e039ac2'}
            };
            spyOn(RecipeService, 'getActiveThresholdStep').and.returnValue(step);

            //when
            PlaygroundService.loadStep(step);

            //then
            expect($rootScope.$emit).not.toHaveBeenCalledWith('talend.loading.start');
            expect(PreparationService.getContent).not.toHaveBeenCalled();
        }));
    });

    it('should do nothing when provided name is the original name', inject(function($rootScope, PlaygroundService, PreparationService) {
        //given
        var name = 'My preparation';
        var newName = name;

        PlaygroundService.originalPreparationName = name;
        PlaygroundService.preparationName = newName;

        //when
        PlaygroundService.createOrUpdatePreparation(newName);
        $rootScope.$digest();

        //then
        expect(PreparationService.create).not.toHaveBeenCalled();
        expect(PreparationService.setName).not.toHaveBeenCalled();
        expect(PlaygroundService.preparationName).toBe(name);
        expect(PlaygroundService.originalPreparationName).toBe(name);
    }));
});