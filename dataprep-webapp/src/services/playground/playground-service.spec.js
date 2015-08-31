/*jshint camelcase: false */
describe('Playground Service', function () {
    'use strict';

    var datasetContent = {column: [], records: []};

    beforeEach(module('data-prep.services.playground'));

    beforeEach(inject(function ($injector, $q, DatasetService, FilterService, RecipeService, DatagridService,
                                PreparationService, TransformationCacheService, SuggestionService,
                                HistoryService, StatisticsService, PreviewService) {
        spyOn(DatasetService, 'getContent').and.returnValue($q.when(datasetContent));
        spyOn(FilterService, 'removeAllFilters').and.returnValue();
        spyOn(RecipeService, 'refresh').and.returnValue($q.when(true));
        spyOn(DatagridService, 'setDataset').and.returnValue();
        spyOn(PreparationService, 'create').and.returnValue($q.when(true));
        spyOn(PreparationService, 'setName').and.returnValue($q.when(true));
        spyOn(TransformationCacheService, 'invalidateCache').and.returnValue();
        spyOn(SuggestionService, 'reset').and.returnValue();
        spyOn(HistoryService, 'clear').and.returnValue();
        spyOn(PreviewService, 'reset').and.returnValue();
        spyOn(StatisticsService, 'resetCharts').and.returnValue();
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

        beforeEach(inject(function(PlaygroundService, DatasetService, FilterService, RecipeService, DatagridService, TransformationCacheService, SuggestionService, HistoryService, StatisticsService, PreviewService) {
            assertNewPreparationInitialization = function() {
                expect(PlaygroundService.currentMetadata).toEqual(dataset);
                expect(FilterService.removeAllFilters).toHaveBeenCalled();
                expect(RecipeService.refresh).toHaveBeenCalled();
                expect(DatagridService.setDataset).toHaveBeenCalledWith(dataset, datasetContent);
                expect(TransformationCacheService.invalidateCache).toHaveBeenCalled();
                expect(SuggestionService.reset).toHaveBeenCalled();
                expect(HistoryService.clear).toHaveBeenCalled();
                expect(StatisticsService.resetCharts).toHaveBeenCalled();
                expect(PreviewService.reset).toHaveBeenCalledWith(false);
            };
            assertNewPreparationNotInitialized = function() {
                expect(FilterService.removeAllFilters).not.toHaveBeenCalled();
                expect(RecipeService.refresh).not.toHaveBeenCalled();
                expect(DatagridService.setDataset).not.toHaveBeenCalled();
                expect(TransformationCacheService.invalidateCache).not.toHaveBeenCalled();
                expect(SuggestionService.reset).not.toHaveBeenCalled();
                expect(HistoryService.clear).not.toHaveBeenCalled();
                expect(StatisticsService.resetCharts).not.toHaveBeenCalled();
                expect(PreviewService.reset).not.toHaveBeenCalled();
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

    describe('load existing preparation', function() {
        var data = {
            records: [{id: '0', firstname: 'toto'}, {id: '1', firstname: 'tata'}, {id: '2', firstname: 'titi'}]
        };
        var assertDatasetLoadInitialized, assertDatasetLoadNotInitialized;

        beforeEach(inject(function($rootScope, $q, PreparationService, RecipeService, PlaygroundService, FilterService, DatagridService, TransformationCacheService, SuggestionService, HistoryService, StatisticsService, PreviewService) {
            spyOn($rootScope, '$emit').and.callThrough();
            spyOn(PreparationService, 'getContent').and.returnValue($q.when({data: data}));
            spyOn(RecipeService, 'disableStepsAfter').and.callFake(function() {});

            assertDatasetLoadInitialized = function(metadata, data) {
                expect(PlaygroundService.currentMetadata).toEqual(metadata);
                expect(FilterService.removeAllFilters).toHaveBeenCalled();
                expect(RecipeService.refresh).toHaveBeenCalled();
                expect(DatagridService.setDataset).toHaveBeenCalledWith(metadata, data);
                expect(TransformationCacheService.invalidateCache).toHaveBeenCalled();
                expect(SuggestionService.reset).toHaveBeenCalled();
                expect(HistoryService.clear).toHaveBeenCalled();
                expect(StatisticsService.resetCharts).toHaveBeenCalled();
                expect(PreviewService.reset).toHaveBeenCalledWith(false);
            };

            assertDatasetLoadNotInitialized = function() {
                expect(FilterService.removeAllFilters).not.toHaveBeenCalled();
                expect(RecipeService.refresh).not.toHaveBeenCalled();
                expect(DatagridService.setDataset).not.toHaveBeenCalled();
                expect(TransformationCacheService.invalidateCache).not.toHaveBeenCalled();
                expect(SuggestionService.reset).not.toHaveBeenCalled();
                expect(HistoryService.clear).not.toHaveBeenCalled();
                expect(StatisticsService.resetCharts).not.toHaveBeenCalled();
                expect(PreviewService.reset).not.toHaveBeenCalled();
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

        it('should load preparation content at a specific step', inject(function($rootScope, PlaygroundService, FilterService, RecipeService, DatagridService, PreviewService) {
            //given
            var step = {
                column: {id: '0000'},
                transformation: {stepId: 'a4353089cb0e039ac2'}
            };
            var metadata = {id: '1', name: 'my dataset'};
            PlaygroundService.currentMetadata = metadata;

            //when
            PlaygroundService.loadStep(step, '0001');
            expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.start');
            $rootScope.$apply();

            //then
            expect(PlaygroundService.currentMetadata).toBe(metadata);
            expect(FilterService.removeAllFilters).not.toHaveBeenCalled();
            expect(RecipeService.refresh).not.toHaveBeenCalled();
            expect(RecipeService.disableStepsAfter).toHaveBeenCalledWith(step);
            expect(PreviewService.reset).toHaveBeenCalledWith(false);
            expect(DatagridService.setDataset).toHaveBeenCalledWith(metadata, data);
            expect(DatagridService.focusedColumn).toBe('0001');
            expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.stop');
        }));

        it('should do nothing if current step (threshold between active and inactive) is already selected', inject(function($rootScope, PlaygroundService, RecipeService, PreparationService) {
            //given
            var step = {
                column: {id: '0000'},
                transformation: {stepId: 'a4353089cb0e039ac2'}
            };
            spyOn(RecipeService, 'getActiveThresholdStep').and.returnValue(step);

            //when
            PlaygroundService.loadStep(step);

            //then
            expect($rootScope.$emit).not.toHaveBeenCalledWith('talend.loading.start');
            expect(PreparationService.getContent).not.toHaveBeenCalled();
        }));

        it('should load preparation sample when sample size is changed', inject(function(PlaygroundService, PreparationService, RecipeService) {
            //given
            PreparationService.currentPreparationId = '5746518486846';

            var lastActiveStep = {transformation: {stepId: '53df45d3s8425'}};
            spyOn(RecipeService, 'getActiveThresholdStepIndex').and.returnValue(2);
            spyOn(RecipeService, 'getStep').and.returnValue(lastActiveStep);

            //when
            PlaygroundService.selectedSampleSize={name: '50', value:50};
            PlaygroundService.changeSampleSize();

            //then
            expect(PreparationService.getContent).toHaveBeenCalledWith(lastActiveStep.transformation.stepId, 50);
        }));

        it('should load the full preparation sample when sample size is changed to full dataset', inject(function(PlaygroundService, PreparationService, RecipeService) {
            //given
            PreparationService.currentPreparationId = '5746518486846';

            var lastActiveStep = {transformation: {stepId: '53df45d3s8425'}};
            spyOn(RecipeService, 'getActiveThresholdStepIndex').and.returnValue(2);
            spyOn(RecipeService, 'getStep').and.returnValue(lastActiveStep);

            //when
            PlaygroundService.selectedSampleSize={name: 'full dataset', value:'full'};
            PlaygroundService.changeSampleSize();

            //then
            expect(PreparationService.getContent).toHaveBeenCalledWith(lastActiveStep.transformation.stepId, 'full');
        }));
    });

    describe('transformation steps', function() {
        var preparationHeadContent, metadata;
        var lastStepId = 'a151e543456413ef51';
        beforeEach(inject(function($rootScope, $q, PlaygroundService, PreparationService, DatagridService, RecipeService, HistoryService) {
            preparationHeadContent = {
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
            spyOn(PreparationService, 'updateStep').and.returnValue($q.when(true));
            spyOn(PreparationService, 'removeStep').and.returnValue($q.when(true));
            spyOn(PreparationService, 'getContent').and.returnValue($q.when({data: preparationHeadContent}));
            spyOn(DatagridService, 'updateData').and.returnValue();
            spyOn(RecipeService, 'getLastStep').and.returnValue({
                transformation: {stepId: lastStepId}
            });
            spyOn(HistoryService, 'addAction').and.returnValue();
        }));

        describe('append', function() {
            it('should append preparation step', inject(function ($rootScope, PlaygroundService, PreparationService) {
                //given
                var action = 'uppercase';
                var parameters = {
                    param1: 'param1Value',
                    param2: 4,
                    scope: 'column',
                    column_id: '0001',
                    column_name: 'firstname'
                };
                var actionParameters = {
                    action: action,
                    parameters: parameters
                };

                //when
                PlaygroundService.appendStep(action, parameters);

                //then
                expect(PreparationService.appendStep).toHaveBeenCalledWith(metadata, actionParameters, undefined);
            }));

            it('should show/hide loading', inject(function ($rootScope, PlaygroundService) {
                //given
                var action = 'uppercase';
                var parameters = {
                    param1: 'param1Value',
                    param2: 4,
                    scope: 'column',
                    column_id: '0001',
                    column_name: 'firstname'
                };

                //when
                PlaygroundService.appendStep(action, parameters);
                expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.start');
                $rootScope.$digest();

                //then
                expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.stop');
            }));

            it('should refresh recipe', inject(function ($rootScope, PlaygroundService, RecipeService) {
                //given
                var action = 'uppercase';
                var parameters = {
                    param1: 'param1Value',
                    param2: 4,
                    scope: 'column',
                    column_id: '0001',
                    column_name: 'firstname'
                };

                //when
                PlaygroundService.appendStep(action, parameters);
                $rootScope.$digest();

                //then
                expect(RecipeService.refresh).toHaveBeenCalled();
            }));

            it('should refresh datagrid with head content', inject(function ($rootScope, PlaygroundService, PreparationService, DatagridService, PreviewService) {
                //given
                var action = 'uppercase';
                var parameters = {
                    param1: 'param1Value',
                    param2: 4,
                    scope: 'column',
                    column_id: '0001',
                    column_name: 'firstname'
                };
                PlaygroundService.selectedSampleSize = {value: 'full'};

                //when
                PlaygroundService.appendStep(action, parameters);
                $rootScope.$digest();

                //then
                expect(PreparationService.getContent).toHaveBeenCalledWith('head', 'full');
                expect(DatagridService.focusedColumn).toBe(parameters.column_id);
                expect(DatagridService.updateData).toHaveBeenCalledWith(preparationHeadContent);
                expect(PreviewService.reset).toHaveBeenCalledWith(false);
            }));

            describe('append history', function() {
                it('should add undo/redo actions after append transformation', inject(function($rootScope, PlaygroundService, HistoryService) {
                    //given
                    var action = 'uppercase';
                    var parameters = {
                        param1: 'param1Value',
                        param2: 4,
                        scope: 'column',
                        column_id: '0001',
                        column_name: 'firstname'
                    };
                    expect(HistoryService.addAction).not.toHaveBeenCalled();

                    //when
                    PlaygroundService.appendStep(action, parameters);
                    $rootScope.$digest();

                    //then
                    expect(HistoryService.addAction).toHaveBeenCalled();
                }));

                it('should remove the transformation in cascade mode on UNDO', inject(function(DatagridService, $rootScope, PlaygroundService, HistoryService, PreparationService) {
                    //given
                    var action = 'uppercase';
                    var parameters = {
                        param1: 'param1Value',
                        param2: 4,
                        scope: 'column',
                        column_id: '0001',
                        column_name: 'firstname'
                    };
                    var singleMode = false;

                    PlaygroundService.appendStep(action, parameters);
                    $rootScope.$digest();
                    var undo = HistoryService.addAction.calls.argsFor(0)[0];

                    expect(PreparationService.removeStep).not.toHaveBeenCalled();

                    //when
                    undo();

                    //then
                    expect(PreparationService.removeStep).toHaveBeenCalledWith('a151e543456413ef51', singleMode);
                    expect(DatagridService.focusedColumn).toBe('0001');
                }));

                it('should refresh recipe on UNDO', inject(function($rootScope, DatagridService, PlaygroundService, HistoryService, RecipeService) {
                    //given
                    var action = 'uppercase';
                    var parameters = {
                        param1: 'param1Value',
                        param2: 4,
                        scope: 'column',
                        column_id: '0001',
                        column_name: 'firstname'
                    };

                    PlaygroundService.appendStep(action, parameters);
                    $rootScope.$digest();
                    var undo = HistoryService.addAction.calls.argsFor(0)[0];

                    expect(RecipeService.refresh.calls.count()).toBe(1);

                    //when
                    undo();
                    $rootScope.$digest();

                    //then
                    expect(RecipeService.refresh.calls.count()).toBe(2);
                    expect(DatagridService.focusedColumn).toBe('0001');
                }));

                it('should refresh datagrid content on UNDO', inject(function($rootScope, PlaygroundService, HistoryService, PreparationService, DatagridService) {
                    //given
                    var action = 'uppercase';
                    var parameters = {
                        param1: 'param1Value',
                        param2: 4,
                        scope: 'column',
                        column_id: '0001',
                        column_name: 'firstname'
                    };

                    PlaygroundService.appendStep(action, parameters);
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
                    expect(DatagridService.focusedColumn).toBe('0001');
                    expect(DatagridService.updateData.calls.count()).toBe(2);
                    expect(DatagridService.updateData.calls.argsFor(1)[0]).toBe(preparationHeadContent);
                }));
            });
        });

        describe('update', function() {
            var lastActiveIndex = 5;
            var lastActiveStep = {
                column:{id:'0000'},
                transformation: {stepId: '24a457bc464e645'},
                actionParameters: {
                    action: 'touppercase'
                }
            };
            var oldParameters = {value: 'toto', column_id: '0001'};
            var stepToUpdate = {
                column:{id:'0001'},
                transformation: {stepId: '98a7565e4231fc2c7'},
                actionParameters: {
                    action: 'delete_on_value',
                    parameters: oldParameters
                }
            };

            beforeEach(inject(function(RecipeService) {
                spyOn(RecipeService, 'getActiveThresholdStepIndex').and.returnValue(lastActiveIndex);
                spyOn(RecipeService, 'getStep').and.callFake(function(index) {
                    if(index === lastActiveIndex) {
                        return lastActiveStep;
                    }
                    return stepToUpdate;
                });
            }));

            it('should update preparation step', inject(function ($rootScope, PlaygroundService, PreparationService) {
                //given
                var parameters = {value: 'toto', column_id: '0001'};

                //when
                PlaygroundService.updateStep(stepToUpdate, parameters);

                //then
                expect(PreparationService.updateStep).toHaveBeenCalledWith(stepToUpdate, parameters);
            }));

            it('should show/hide loading', inject(function ($rootScope, PlaygroundService) {
                //given
                var parameters = {value: 'toto', column_id: '0001'};

                //when
                PlaygroundService.updateStep(stepToUpdate, parameters);
                expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.start');
                $rootScope.$digest();

                //then
                expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.stop');
            }));

            it('should refresh recipe', inject(function ($rootScope, PlaygroundService, RecipeService) {
                //given
                var parameters = {value: 'toto', column_id: '0001'};

                //when
                PlaygroundService.updateStep(stepToUpdate, parameters);
                $rootScope.$digest();

                //then
                expect(RecipeService.refresh).toHaveBeenCalled();
            }));

            it('should load previous last active step', inject(function ($rootScope, PlaygroundService, PreparationService, DatagridService, PreviewService) {
                //given
                var parameters = {value: 'toto', column_id: '0001'};
                PlaygroundService.selectedSampleSize = {value: 'full'};

                //when
                PlaygroundService.updateStep(stepToUpdate, parameters);
                $rootScope.$digest();

                //then
                expect(PreparationService.getContent).toHaveBeenCalledWith(lastActiveStep.transformation.stepId, 'full');
                expect(DatagridService.focusedColumn).toBe(stepToUpdate.column.id);
                expect(DatagridService.setDataset).toHaveBeenCalledWith(metadata, preparationHeadContent);
                expect(PreviewService.reset).toHaveBeenCalledWith(false);
            }));

            describe('update history', function() {
                it('should add undo/redo actions after update transformation', inject(function($rootScope, PlaygroundService, HistoryService) {
                    //given
                    var parameters = {value: 'toto', column_id: '0001'};
                    expect(HistoryService.addAction).not.toHaveBeenCalled();

                    //when
                    PlaygroundService.updateStep(stepToUpdate, parameters);
                    $rootScope.$digest();

                    //then
                    expect(HistoryService.addAction).toHaveBeenCalled();
                }));

                it('should update the transformation with old parameters on UNDO', inject(function($rootScope, PlaygroundService, HistoryService, PreparationService) {
                    //given
                    var parameters = {value: 'toto', column_id: '0001'};

                    PlaygroundService.updateStep(stepToUpdate, parameters);
                    $rootScope.$digest();
                    var undo = HistoryService.addAction.calls.argsFor(0)[0];

                    expect(PreparationService.updateStep.calls.count()).toBe(1);

                    //when
                    undo();

                    //then
                    expect(PreparationService.updateStep.calls.count()).toBe(2);
                    var callArgs = PreparationService.updateStep.calls.argsFor(1);
                    expect(callArgs[0]).toBe(stepToUpdate);
                    expect(callArgs[1]).toBe(oldParameters);
                }));

                it('should refresh recipe on UNDO', inject(function($rootScope, PlaygroundService, HistoryService, RecipeService) {
                    //given
                    var parameters = {value: 'toto', column_id: '0001'};

                    PlaygroundService.updateStep(stepToUpdate, parameters);
                    $rootScope.$digest();
                    var undo = HistoryService.addAction.calls.argsFor(0)[0];

                    expect(RecipeService.refresh.calls.count()).toBe(1);

                    //when
                    undo();
                    $rootScope.$digest();

                    //then
                    expect(RecipeService.refresh.calls.count()).toBe(2);
                }));

                it('should refresh datagrid content on UNDO', inject(function($rootScope, PlaygroundService, HistoryService, PreparationService, DatagridService, RecipeService) {
                    //given
                    var parameters = {value: 'toto', column_id: '0001'};

                    PlaygroundService.updateStep(stepToUpdate, parameters);
                    $rootScope.$digest();
                    var undo = HistoryService.addAction.calls.argsFor(0)[0];

                    expect(PreparationService.getContent.calls.count()).toBe(1);
                    expect(DatagridService.setDataset.calls.count()).toBe(1);

                    //when
                    undo();
                    spyOn(RecipeService, 'getActiveThresholdStep').and.returnValue(); //emulate last active step different to the step to load
                    $rootScope.$digest();

                    //then
                    expect(PreparationService.getContent.calls.count()).toBe(2);
                    expect(PreparationService.getContent.calls.argsFor(1)[0]).toBe(lastActiveStep.transformation.stepId);
                    expect(DatagridService.setDataset.calls.count()).toBe(2);
                    expect(DatagridService.focusedColumn).toBe(stepToUpdate.column.id);
                    expect(DatagridService.setDataset.calls.argsFor(1)[0]).toBe(metadata);
                    expect(DatagridService.setDataset.calls.argsFor(1)[1]).toBe(preparationHeadContent);
                }));
            });
        });

        describe('remove', function() {
            var stepToDeleteId = '98a7565e4231fc2c7';
            var stepToDelete = {
                column:{id:'0001'},
                transformation: {stepId: stepToDeleteId},
                actionParameters: {
                    action: 'delete_on_value',
                    parameters: {value: 'toto', column_id: '0001'}
                }
            };

            var previousStepId = '897f486516ef549cf845';
            var previousStep = {
                column:{id:'0001'},
                transformation: {stepId: previousStepId},
                actionParameters: {
                    action: 'touppercase',
                    parameters: {column_id: '0001'}
                }
            };

            var allActionsFromStepToDelete = [
                {action: 'tolowercase', parameters: {column_id: '0003'}},
                {action: 'deleteempty', parameters: {column_id: '0003'}},
                {action: 'touppercase', parameters: {column_id: '0004'}}
            ];

            beforeEach(inject(function(RecipeService) {
                spyOn(RecipeService, 'getPreviousStep').and.returnValue(previousStep);
                spyOn(RecipeService, 'getAllActionsFrom').and.returnValue(allActionsFromStepToDelete);
            }));

            it('should remove preparation step in single mode', inject(function ($rootScope, PlaygroundService, PreparationService) {
                //when
                PlaygroundService.removeStep(stepToDelete, 'single');

                //then
                expect(PreparationService.removeStep).toHaveBeenCalledWith(stepToDeleteId, true);
            }));

            it('should remove preparation step in cascade mode', inject(function ($rootScope, PlaygroundService, PreparationService) {
                //when
                PlaygroundService.removeStep(stepToDelete, 'cascade');

                //then
                expect(PreparationService.removeStep).toHaveBeenCalledWith(stepToDeleteId, false);
            }));

            it('should remove preparation step in default mode (cascade)', inject(function ($rootScope, PlaygroundService, PreparationService) {
                //when
                PlaygroundService.removeStep(stepToDelete);

                //then
                expect(PreparationService.removeStep).toHaveBeenCalledWith(stepToDeleteId, false);
            }));

            it('should show/hide loading', inject(function ($rootScope, PlaygroundService) {
                //when
                PlaygroundService.removeStep(stepToDelete, 'cascade');
                expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.start');
                $rootScope.$digest();

                //then
                expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.stop');
            }));

            it('should refresh recipe', inject(function ($rootScope, PlaygroundService, RecipeService) {
                //when
                PlaygroundService.removeStep(stepToDelete, 'cascade');
                $rootScope.$digest();

                //then
                expect(RecipeService.refresh).toHaveBeenCalled();
            }));

            it('should update datagrid', inject(function ($rootScope, PlaygroundService, PreparationService, DatagridService, PreviewService) {
                //given
                PlaygroundService.selectedSampleSize = {value: 'full'};

                //when
                PlaygroundService.removeStep(stepToDelete, 'cascade');
                $rootScope.$digest();

                //then
                expect(PreparationService.getContent).toHaveBeenCalledWith('head', 'full');
                expect(DatagridService.focusedColumn).toBeFalsy();
                expect(DatagridService.updateData).toHaveBeenCalledWith(preparationHeadContent);
                expect(PreviewService.reset).toHaveBeenCalledWith(false);
            }));

            describe('update history', function() {
                it('should add undo/redo actions after remove transformation', inject(function($rootScope, PlaygroundService, HistoryService) {
                    //given
                    expect(HistoryService.addAction).not.toHaveBeenCalled();

                    //when
                    PlaygroundService.removeStep(stepToDelete);
                    $rootScope.$digest();

                    //then
                    expect(HistoryService.addAction).toHaveBeenCalled();
                }));

                it('should add single action in the previous step insertion point on UNDO', inject(function($rootScope, PlaygroundService, HistoryService, PreparationService) {
                    //given
                    PlaygroundService.removeStep(stepToDelete, 'single');
                    $rootScope.$digest();
                    expect(PreparationService.appendStep).not.toHaveBeenCalled();

                    //when
                    var undo = HistoryService.addAction.calls.argsFor(0)[0];
                    undo();

                    //then
                    expect(PreparationService.appendStep).toHaveBeenCalledWith(metadata, stepToDelete.actionParameters, previousStepId);
                }));

                it('should add all following actions (cascade) to preparation head on UNDO', inject(function($rootScope, PlaygroundService, HistoryService, PreparationService) {
                    //given
                    PlaygroundService.removeStep(stepToDelete, 'cascade');
                    $rootScope.$digest();
                    expect(PreparationService.appendStep).not.toHaveBeenCalled();

                    //when
                    var undo = HistoryService.addAction.calls.argsFor(0)[0];
                    undo();

                    //then
                    expect(PreparationService.appendStep).toHaveBeenCalledWith(metadata, allActionsFromStepToDelete, undefined);
                }));

                it('should refresh recipe on UNDO', inject(function($rootScope, PlaygroundService, HistoryService, RecipeService) {
                    //given
                    PlaygroundService.removeStep(stepToDelete, 'cascade');
                    $rootScope.$digest();
                    expect(RecipeService.refresh.calls.count()).toBe(1);

                    //when
                    var undo = HistoryService.addAction.calls.argsFor(0)[0];
                    undo();
                    $rootScope.$digest();

                    //then
                    expect(RecipeService.refresh.calls.count()).toBe(2);
                }));

                it('should refresh datagrid content on UNDO', inject(function($rootScope, PlaygroundService, HistoryService, PreparationService, DatagridService, RecipeService, PreviewService) {
                    //given
                    PlaygroundService.selectedSampleSize = {value: 'full'};
                    PlaygroundService.removeStep(stepToDelete, 'cascade');
                    $rootScope.$digest();
                    expect(RecipeService.refresh.calls.count()).toBe(1);

                    //when
                    var undo = HistoryService.addAction.calls.argsFor(0)[0];
                    undo();
                    $rootScope.$digest();

                    //then
                    expect(PreparationService.getContent).toHaveBeenCalledWith('head', 'full');
                    expect(DatagridService.focusedColumn).toBeFalsy();
                    expect(DatagridService.updateData).toHaveBeenCalledWith(preparationHeadContent);
                    expect(PreviewService.reset).toHaveBeenCalledWith(false);
                }));
            });
        });

        describe('edit cell', function() {
            it('should append cell edition step', inject(function ($rootScope, PlaygroundService, PreparationService) {
                //given
                var rowItem = {tdpId: 58, '0000': 'McDonald', '0001': 'Ronald'};
                var column = {id: '0001', name: 'firstname'};
                var newValue = 'Donald';
                var updateAllCellWithValue = false;

                //when
                PlaygroundService.editCell(rowItem, column, newValue, updateAllCellWithValue);

                //then
                var expectedParams = {
                    scope : 'cell',
                    column_id: '0001',
                    column_name: 'firstname',
                    row_id: 58,
                    cell_value: 'Ronald',
                    replace_value: 'Donald'
                };
                expect(PreparationService.appendStep).toHaveBeenCalledWith(
                    metadata,
                    {action: 'replace_on_value', parameters: expectedParams},
                    undefined
                );
            }));

            it('should append column replace value step', inject(function ($rootScope, PlaygroundService, PreparationService) {
                //given
                var rowItem = {tdpId: 58, '0000': 'McDonald', '0001': 'Ronald'};
                var column = {id: '0001', name: 'firstname'};
                var newValue = 'Donald';
                var updateAllCellWithValue = true;

                //when
                PlaygroundService.editCell(rowItem, column, newValue, updateAllCellWithValue);

                //then
                var expectedParams = {
                    scope : 'column',
                    column_id: '0001',
                    column_name: 'firstname',
                    row_id: 58,
                    cell_value: 'Ronald',
                    replace_value: 'Donald'
                };
                expect(PreparationService.appendStep).toHaveBeenCalledWith(
                    metadata,
                    {action: 'replace_on_value', parameters: expectedParams},
                    undefined
                );
            }));

            describe('append history', function() {
                it('should add undo/redo actions after append transformation', inject(function($rootScope, PlaygroundService, HistoryService) {
                    //given
                    var rowItem = {tdpId: 58, '0000': 'McDonald', '0001': 'Ronald'};
                    var column = {id: '0001', name: 'firstname'};
                    var newValue = 'Donald';
                    var updateAllCellWithValue = true;

                    //when
                    PlaygroundService.editCell(rowItem, column, newValue, updateAllCellWithValue);
                    $rootScope.$digest();

                    //then
                    expect(HistoryService.addAction).toHaveBeenCalled();
                }));
            });
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

    describe('dataset management when no preparation is done yet', function() {

        it('should load dataset sample when sample size is changed', inject(function(PlaygroundService, PreparationService, DatasetService) {
            //given
            PreparationService.currentPreparationId = null;
            PlaygroundService.currentMetadata = {id: '123d120394ab0c53'};

            //when
            PlaygroundService.selectedSampleSize = {value:50};
            PlaygroundService.changeSampleSize();

            //then
            expect(DatasetService.getContent).toHaveBeenCalledWith(PlaygroundService.currentMetadata.id, true, 50);
        }));

        it('should load dataset sample when sample size is changed', inject(function(PlaygroundService, PreparationService, DatasetService) {
            //given
            PreparationService.currentPreparationId = null;
            PlaygroundService.currentMetadata = {id: '123d120394ab0c53'};

            //when
            PlaygroundService.selectedSampleSize = {value:'full'};
            PlaygroundService.changeSampleSize();

            //then
            expect(DatasetService.getContent).toHaveBeenCalledWith(PlaygroundService.currentMetadata.id, true, 'full');
        }));
    });

    describe('change column type', function() {

        beforeEach(inject(function($q, DatasetService, PreparationService, DatagridService) {
            spyOn(DatasetService, 'updateColumn').and.returnValue($q.when({}));
            spyOn(PreparationService, 'getContent').and.returnValue($q.when({data: {}}));
            spyOn(DatagridService, 'updateData').and.returnValue();
        }));

        it('should get preparation content', inject(function($rootScope, PlaygroundService, PreparationService, DatasetService, PreviewService) {
            //given
            PlaygroundService.currentMetadata = {id: 'gfkjqghflqsdgf'};
            PreparationService.currentPreparationId = 1324;
            PlaygroundService.selectedSampleSize = {value:500};

            var columnId = '0001';
            var type = 'date';
            var domain = '';

            //when
            PlaygroundService.updateColumn(columnId, type, domain);
            $rootScope.$digest();

            //then
            expect(DatasetService.updateColumn).toHaveBeenCalledWith(PlaygroundService.currentMetadata.id, columnId, {type: type, domain: domain});
            expect(PreparationService.getContent).toHaveBeenCalledWith('head', PlaygroundService.selectedSampleSize.value);
            expect(PreviewService.reset).toHaveBeenCalledWith(false);
        }));

        it('should get dataset content', inject(function($rootScope, PlaygroundService, PreparationService, DatasetService) {
            //given
            PlaygroundService.currentMetadata = {id: 'gfkjqghflqsdgf'};
            PreparationService.currentPreparationId = null;
            PlaygroundService.selectedSampleSize = {value:500};

            var columnId = '0001';
            var type = 'date';
            var domain = '';

            //when
            PlaygroundService.updateColumn(columnId, type, domain);
            $rootScope.$digest();

            //then
            expect(DatasetService.updateColumn).toHaveBeenCalledWith(PlaygroundService.currentMetadata.id, columnId, {type: type, domain: domain});
            expect(DatasetService.getContent).toHaveBeenCalledWith(PlaygroundService.currentMetadata.id, false, PlaygroundService.selectedSampleSize.value);
        }));
    });
});
