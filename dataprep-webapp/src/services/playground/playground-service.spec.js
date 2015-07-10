describe('Playground Service', function () {
    'use strict';

    var content = {column: [], records: []};

    beforeEach(module('data-prep.services.playground'));

    beforeEach(inject(function ($injector, $q, DatasetService, FilterService, RecipeService, DatagridService, PreparationService, TransformationCacheService, ColumnSuggestionService) {
        spyOn(DatasetService, 'getContent').and.returnValue($q.when(content));
        spyOn(FilterService, 'removeAllFilters').and.returnValue();
        spyOn(RecipeService, 'refresh').and.returnValue($q.when(true));
        spyOn(DatagridService, 'setDataset').and.returnValue();
        spyOn(PreparationService, 'create').and.returnValue($q.when(true));
        spyOn(PreparationService, 'setName').and.returnValue($q.when(true));
        spyOn(TransformationCacheService, 'invalidateCache').and.returnValue();
        spyOn(ColumnSuggestionService, 'reset').and.returnValue();
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

    it('should reject when provided name is the original name', inject(function($rootScope, PlaygroundService, PreparationService) {
        //given
        var name = 'My preparation';
        var newName = name;
        var rejected = false;

        PlaygroundService.originalPreparationName = name;
        PlaygroundService.preparationName = newName;

        //when
        PlaygroundService.createOrUpdatePreparation(newName)
            .catch(function() {
                rejected = true;
            });
        $rootScope.$digest();

        //then
        expect(rejected).toBe(true);
        expect(PreparationService.create).not.toHaveBeenCalled();
        expect(PreparationService.setName).not.toHaveBeenCalled();
        expect(PlaygroundService.preparationName).toBe(name);
        expect(PlaygroundService.originalPreparationName).toBe(name);
    }));

    describe('init new preparation', function() {
        var dataset = {id: 'e85afAa78556d5425bc2'};
        var assertNewPreparationInitialization, assertNewPreparationNotInitialized;

        beforeEach(inject(function(PlaygroundService, DatasetService, FilterService, RecipeService, DatagridService, TransformationCacheService, ColumnSuggestionService) {
            assertNewPreparationInitialization = function() {
                expect(PlaygroundService.currentMetadata).toEqual(dataset);
                expect(FilterService.removeAllFilters).toHaveBeenCalled();
                expect(RecipeService.refresh).toHaveBeenCalled();
                expect(DatagridService.setDataset).toHaveBeenCalledWith(dataset, content);
                expect(TransformationCacheService.invalidateCache).toHaveBeenCalled();
                expect(ColumnSuggestionService.reset).toHaveBeenCalled();
            };
            assertNewPreparationNotInitialized = function() {
                expect(FilterService.removeAllFilters).not.toHaveBeenCalled();
                expect(RecipeService.refresh).not.toHaveBeenCalled();
                expect(DatagridService.setDataset).not.toHaveBeenCalled();
                expect(TransformationCacheService.invalidateCache).not.toHaveBeenCalled();
                expect(ColumnSuggestionService.reset).not.toHaveBeenCalled();
            };
        }));

        it('should init a new preparation and show playground when there is no loaded data yet', inject(function($rootScope, PlaygroundService, PreparationService) {
            //given
            expect(PlaygroundService.visible).toBe(false);
            expect(PlaygroundService.currentMetadata).toBeFalsy();
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

        it('should init playground when the wanted dataset is loaded and no preparation was created yet', inject(function($rootScope, PlaygroundService) {
            //given
            var dataset = {id: 'e85afAa78556d5425bc2'};
            PlaygroundService.currentMetadata = dataset;

            expect(PlaygroundService.visible).toBe(false);
            expect(PlaygroundService.currentPreparationId).toBeFalsy();

            //when
            PlaygroundService.initPlayground(dataset);
            $rootScope.$digest();

            //then
            expect(PlaygroundService.currentMetadata).toBe(dataset);
            assertNewPreparationNotInitialized();
        }));
    });

    describe('load existing dataset', function() {
        var data = {
            records: [{id: '0', firstname: 'toto'}, {id: '1', firstname: 'tata'}, {id: '2', firstname: 'titi'}]
        };
        var assertDatasetLoadInitialized, assertDatasetLoadNotInitialized;

        beforeEach(inject(function($rootScope, $q, PreparationService, RecipeService, PlaygroundService, FilterService, DatagridService, TransformationCacheService, ColumnSuggestionService) {
            spyOn($rootScope, '$emit').and.callThrough();
            spyOn(PreparationService, 'getContent').and.returnValue($q.when({data: data}));
            spyOn(RecipeService, 'disableStepsAfter').and.callFake(function() {});

            assertDatasetLoadInitialized = function(metadata, data) {
                expect(PlaygroundService.currentMetadata).toEqual(metadata);
                expect(FilterService.removeAllFilters).toHaveBeenCalled();
                expect(RecipeService.refresh).toHaveBeenCalled();
                expect(DatagridService.setDataset).toHaveBeenCalledWith(metadata, data);
                expect(TransformationCacheService.invalidateCache).toHaveBeenCalled();
                expect(ColumnSuggestionService.reset).toHaveBeenCalled();
            };

            assertDatasetLoadNotInitialized = function() {
                expect(FilterService.removeAllFilters).not.toHaveBeenCalled();
                expect(RecipeService.refresh).not.toHaveBeenCalled();
                expect(DatagridService.setDataset).not.toHaveBeenCalled();
                expect(TransformationCacheService.invalidateCache).not.toHaveBeenCalled();
                expect(ColumnSuggestionService.reset).not.toHaveBeenCalled();
            };
        }));

        it('should load existing preparation when it is not already loaded', inject(function($rootScope, PlaygroundService, PreparationService) {
            //given
            var preparation = {
                id: '6845521254541',
                dataset: {id: '1'}
            };
            PreparationService.currentPreparationId = '5746518486846';

            //when
            PlaygroundService.load(preparation);
            $rootScope.$apply();

            //then
            assertDatasetLoadInitialized(preparation.dataset, data);
        }));

        it('should manage loading spinner on preparation load', inject(function($rootScope, PlaygroundService, PreparationService) {
            //given
            var preparation = {
                id: '6845521254541',
                dataset: {id: '1'}
            };
            PreparationService.currentPreparationId = '5746518486846';

            //when
            PlaygroundService.load(preparation);
            expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.start');
            $rootScope.$apply();

            //then
            expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.stop');
        }));

        it('should load existing preparation with simulated dataset metadata when its metadata is not set yet', inject(function($rootScope, PlaygroundService, PreparationService) {
            //given
            var preparation = {
                id: '6845521254541',
                dataSetId: '1'
            };
            PreparationService.currentPreparationId = '5746518486846';

            //when
            PlaygroundService.load(preparation);
            $rootScope.$apply();

            //then
            assertDatasetLoadInitialized({id: '1'}, data);
        }));

        it('should not change playground if the preparation to load is already loaded',
            inject(function($rootScope, PlaygroundService, PreparationService) {
            //given
            var preparation = {
                id: '6845521254541',
                dataset: {id: '1', name: 'my dataset'}
            };
            var oldMetadata = {};

            PreparationService.currentPreparationId = '6845521254541';
            PlaygroundService.currentMetadata = oldMetadata;

            //when
            PlaygroundService.load(preparation);
            $rootScope.$apply();

            //then
            expect(PlaygroundService.currentMetadata).toBe(oldMetadata);
            assertDatasetLoadNotInitialized();
            expect($rootScope.$emit).not.toHaveBeenCalled();
        }));

        it('should load preparation content at a specific step', inject(function($rootScope, PlaygroundService, FilterService, RecipeService, DatagridService) {
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

    describe('transformation', function() {
        var result, metadata;
        beforeEach(inject(function($rootScope, $q, PlaygroundService, PreparationService, DatagridService, RecipeService) {
            result = {
                'records': [{
                    'firstname': 'Grover',
                    'avgAmount': '82.4',
                    'city': 'BOSTON',
                    'birth': '01-09-1973',
                    'registration': '17-02-2008',
                    'id': '1',
                    'state': 'AR',
                    'nbCommands': '41',
                    'lastname': 'Quincy'
                }, {
                    'firstname': 'Warren',
                    'avgAmount': '87.6',
                    'city': 'NASHVILLE',
                    'birth': '11-02-1960',
                    'registration': '18-08-2007',
                    'id': '2',
                    'state': 'WA',
                    'nbCommands': '17',
                    'lastname': 'Johnson'
                }]
            };

            metadata = {id : 'e85afAa78556d5425bc2'};
            PlaygroundService.currentMetadata = metadata;

            spyOn($rootScope, '$emit').and.callThrough();
            spyOn(PreparationService, 'appendStep').and.returnValue($q.when(true));
            spyOn(PreparationService, 'getContent').and.returnValue($q.when({data: result}));
            spyOn(DatagridService, 'updateData').and.returnValue();
            spyOn(RecipeService, 'getLastStep').and.returnValue({
                transformation: {stepId: 'a151e543456413ef51'}
            });
        }));

        describe('append', function() {
            it('should append preparation step', inject(function ($rootScope, PlaygroundService, PreparationService) {
                //given
                var action = 'uppercase';
                var column = {id: 'firstname'};
                var parameters = {param1: 'param1Value', param2: 4};

                //when
                PlaygroundService.appendStep(action, column, parameters);
                $rootScope.$digest();

                //then
                expect(PreparationService.appendStep).toHaveBeenCalledWith(
                    metadata,
                    action,
                    column,
                    parameters
                );
            }));

            it('should show/hide loading on append', inject(function ($rootScope, PlaygroundService) {
                //given
                var action = 'uppercase';
                var column = {id: 'firstname'};
                var parameters = {param1: 'param1Value', param2: 4};

                //when
                PlaygroundService.appendStep(action, column, parameters);
                expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.start');
                $rootScope.$digest();

                //then
                expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.stop');
            }));

            it('should refresh recipe on append', inject(function ($rootScope, PlaygroundService, RecipeService) {
                //given
                var action = 'uppercase';
                var column = {id: 'firstname'};
                var parameters = {param1: 'param1Value', param2: 4};

                //when
                PlaygroundService.appendStep(action, column, parameters);
                $rootScope.$digest();

                //then
                expect(RecipeService.refresh).toHaveBeenCalled();
            }));

            it('should refresh datagrid with head content on append', inject(function ($rootScope, PlaygroundService, PreparationService, DatagridService) {
                //given
                var action = 'uppercase';
                var column = {id: 'firstname'};
                var parameters = {param1: 'param1Value', param2: 4};

                //when
                PlaygroundService.appendStep(action, column, parameters);
                $rootScope.$digest();

                //then
                expect(PreparationService.getContent).toHaveBeenCalledWith('head');
                expect(DatagridService.updateData).toHaveBeenCalledWith(result);
            }));
        });

        describe('append history', function() {
            beforeEach(inject(function($q, PreparationService, HistoryService) {
                spyOn(PreparationService, 'removeStep').and.returnValue($q.when(true));
                spyOn(HistoryService, 'addAction').and.returnValue();
            }));

            it('should add undo/redo actions after append transformation', inject(function($rootScope, PlaygroundService, HistoryService) {
                //given
                var action = 'uppercase';
                var column = {id: 'firstname'};
                var parameters = {param1: 'param1Value', param2: 4};

                //when
                PlaygroundService.appendStep(action, column, parameters);
                $rootScope.$digest();

                //then
                expect(HistoryService.addAction).toHaveBeenCalled();
            }));

            it('should remove the transformation on UNDO', inject(function($rootScope, PlaygroundService, HistoryService, PreparationService) {
                //given
                var action = 'uppercase';
                var column = {id: 'firstname'};
                var parameters = {param1: 'param1Value', param2: 4};

                PlaygroundService.appendStep(action, column, parameters);
                $rootScope.$digest();
                var undo = HistoryService.addAction.calls.argsFor(0)[0];

                expect(PreparationService.removeStep).not.toHaveBeenCalled();

                //when
                undo();

                //then
                expect(PreparationService.removeStep).toHaveBeenCalledWith('a151e543456413ef51');
            }));

            it('should refresh recipe on UNDO', inject(function($rootScope, PlaygroundService, HistoryService, RecipeService) {
                //given
                var action = 'uppercase';
                var column = {id: 'firstname'};
                var parameters = {param1: 'param1Value', param2: 4};

                PlaygroundService.appendStep(action, column, parameters);
                $rootScope.$digest();
                var undo = HistoryService.addAction.calls.argsFor(0)[0];

                expect(RecipeService.refresh.calls.count()).toBe(1);

                //when
                undo();
                $rootScope.$digest();

                //then
                expect(RecipeService.refresh.calls.count()).toBe(2);
            }));

            it('should refresh datagrid content on UNDO', inject(function($rootScope, PlaygroundService, HistoryService, PreparationService, DatagridService) {
                //given
                var action = 'uppercase';
                var column = {id: 'firstname'};
                var parameters = {param1: 'param1Value', param2: 4};

                PlaygroundService.appendStep(action, column, parameters);
                $rootScope.$digest();
                var undo = HistoryService.addAction.calls.argsFor(0)[0];

                expect(PreparationService.getContent.calls.count()).toBe(1);
                expect(DatagridService.updateData.calls.count()).toBe(1);

                //when
                undo();
                $rootScope.$digest();

                //then
                expect(PreparationService.getContent.calls.count()).toBe(2);
                expect(PreparationService.getContent.calls.argsFor(1)[0]).toBe('head');
                expect(DatagridService.updateData.calls.count()).toBe(2);
                expect(DatagridService.updateData.calls.argsFor(1)[0]).toBe(result);
            }));
        });
    });

    describe('recipe panel display management', function() {

        beforeEach(inject(function($q, PreparationService, DatagridService, RecipeService) {
            spyOn(PreparationService, 'getContent').and.returnValue($q.when({data: {}}));
            spyOn(PreparationService, 'appendStep').and.callFake(function() {
                RecipeService.getRecipe().push({});
                return $q.when(true);
            });
            spyOn(DatagridService, 'updateData').and.returnValue();
            spyOn(RecipeService, 'getLastStep').and.returnValue({
                transformation: {stepId: 'a151e543456413ef51'}
            });
        }));

        it('should hide recipe on dataset playground init', inject(function($rootScope, PlaygroundService) {
            //given
            PlaygroundService.showRecipe = true;
            var dataset = {id: '1'};

            //when
            PlaygroundService.initPlayground(dataset);
            $rootScope.$digest();

            //then
            expect(PlaygroundService.showRecipe).toBe(false);
        }));

        it('should show recipe on preparation playground init', inject(function($rootScope, PlaygroundService) {
            //given
            PlaygroundService.showRecipe = false;
            var preparation = {
                id: '6845521254541',
                dataset: {id: '1'}
            };

            //when
            PlaygroundService.load(preparation);
            $rootScope.$digest();

            //then
            expect(PlaygroundService.showRecipe).toBe(true);
        }));

        it('should show recipe on first step append', inject(function($rootScope, PlaygroundService) {
            //given
            PlaygroundService.showRecipe = false;

            var action = 'uppercase';
            var column = {id: 'firstname'};
            var parameters = {param1: 'param1Value', param2: 4};

            //when
            PlaygroundService.appendStep(action, column, parameters);
            $rootScope.$digest();

            //then
            expect(PlaygroundService.showRecipe).toBe(true);
        }));

        it('should NOT force recipe display on second step append', inject(function($rootScope, PlaygroundService, RecipeService) {
            //given
            PlaygroundService.showRecipe = false;
            RecipeService.getRecipe().push({});

            var action = 'uppercase';
            var column = {id: 'firstname'};
            var parameters = {param1: 'param1Value', param2: 4};

            //when
            PlaygroundService.appendStep(action, column, parameters);
            $rootScope.$digest();

            //then
            expect(PlaygroundService.showRecipe).toBe(false);
        }));
    });

    describe('preparation name edition mode', function() {

        beforeEach(inject(function($q, PreparationService, DatagridService, RecipeService) {
            spyOn(PreparationService, 'getContent').and.returnValue($q.when({data: {}}));
            spyOn(PreparationService, 'appendStep').and.callFake(function() {
                RecipeService.getRecipe().push({});
                return $q.when(true);
            });
            spyOn(DatagridService, 'updateData').and.returnValue();
        }));

        it('should turn on edition mode on dataset playground init', inject(function($rootScope, PlaygroundService) {
            //given
            PlaygroundService.preparationNameEditionMode = false;
            var dataset = {id: '1'};

            //when
            PlaygroundService.initPlayground(dataset);
            $rootScope.$digest();

            //then
            expect(PlaygroundService.preparationNameEditionMode).toBe(true);
        }));

        it('should turn off edition mode playground init', inject(function($rootScope, PlaygroundService) {
            //given
            PlaygroundService.preparationNameEditionMode = true;
            var preparation = {
                id: '6845521254541',
                dataset: {id: '1'}
            };

            //when
            PlaygroundService.load(preparation);
            $rootScope.$digest();

            //then
            expect(PlaygroundService.preparationNameEditionMode).toBe(false);
        }));
    });
});