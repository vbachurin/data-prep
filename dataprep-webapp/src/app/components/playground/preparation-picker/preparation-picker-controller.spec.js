/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

describe('Preparation Picker controller', () => {

    const datasets = [
        {id: 'de3cc32a-b624-484e-b8e7-dab9061a009c', name: 'my dataset'},
        {id: '4d0a2718-bec6-4614-ad6c-8b3b326ff6c7', name: 'my second dataset'},
        {id: '555a2718-bec6-4614-ad6c-8b3b326ff6c7', name: 'my second dataset (1)'}
    ];

    let compatiblePreps = [
        {
            'id': 'ab136cbf0923a7f11bea713adb74ecf919e05cfa',
            'dataSetId': 'de3cc32a-b624-484e-b8e7-dab9061a009c',
            'author': 'anonymousUser',
            'creationDate': 1427447300300,
            'steps': [
                '35890aabcf9115e4309d4ce93367bf5e4e77b82a',
                '4ff5d9a6ca2e75ebe3579740a4297fbdb9b7894f',
                '8a1c49d1b64270482e8db8232357c6815615b7cf',
                '599725f0e1331d5f8aae24f22cd1ec768b10348d'
            ]
        },
        {
            'id': 'fbaa18e82e913e97e5f0e9d40f04413412be1126',
            'dataSetId': '4d0a2718-bec6-4614-ad6c-8b3b326ff6c7',
            'author': 'anonymousUser',
            'creationDate': 1427447330693,
            'steps': [
                '47e2444dd1301120b539804507fd307072294048',
                'ae1aebf4b3fa9b983c895486612c02c766305410',
                '24dcd68f2117b9f93662cb58cc31bf36d6e2867a',
                '599725f0e1331d5f8aae24f22cd1ec768b10348d'
            ]
        }
    ];

    let createController, scope, ctrl, stateMock;
    let cloneId = 'clonepreparationid';
    let updateId = 'cupdatepreparationid';

    let datasetId = 'dsid';
    let name = 'myDsName';

    beforeEach(angular.mock.module('data-prep.preparation-picker', ($provide) => {
        stateMock = {
            inventory: {
                datasets: datasets
            },
            playground: {
                preparation: null
            }
        };
        $provide.constant('state', stateMock);
    }));

    beforeEach(inject(($rootScope, $componentController) => {
        scope = $rootScope.$new();

        createController = (datasetName, datasetId) => {
            return $componentController('preparationPicker',
                {$scope: scope},
                {
                    datasetName: datasetName,
                    datasetId: datasetId
                });
        };
    }));

    beforeEach(() => {
        ctrl = createController(name, datasetId);
    });

    describe('initialization', () => {

        it('should call fetch compatible preparations callback', inject(($q, DatasetService) => {
            //given
            spyOn(DatasetService, 'getCompatiblePreparations').and.returnValue($q.when());

            //when
            ctrl.$onInit();

            //then
            expect(DatasetService.getCompatiblePreparations).toHaveBeenCalledWith(datasetId);
        }));

        it('should fetch and populate the compatible preparations starting from a dataset', inject(($q, DatasetService, StateService) => {
            //given
            spyOn(DatasetService, 'getCompatiblePreparations').and.returnValue($q.when(compatiblePreps));
            spyOn(StateService, 'setCandidatePreparations').and.returnValue();

            //when
            expect(ctrl.isFetchingPreparations).toBe(true);
            ctrl.$onInit();
            scope.$digest();

            //then
            expect(StateService.setCandidatePreparations).toHaveBeenCalledWith([
                {
                    preparation: compatiblePreps[0],
                    dataset: stateMock.inventory.datasets[0]
                },
                {
                    preparation: compatiblePreps[1],
                    dataset: stateMock.inventory.datasets[1]
                }
            ]);
            expect(ctrl.isFetchingPreparations).toBe(false);
        }));

        it('should fetch and populate the compatible preparations starting from a preparation', inject(($q, DatasetService, StateService) => {
            //given
            stateMock.playground.preparation = {id: 'fbaa18e82e913e97e5f0e9d40f04413412be1126'};
            spyOn(DatasetService, 'getCompatiblePreparations').and.returnValue($q.when(compatiblePreps));
            spyOn(StateService, 'setCandidatePreparations').and.returnValue();

            //when
            expect(ctrl.isFetchingPreparations).toBe(true);
            ctrl.$onInit();
            scope.$digest();

            //then
            expect(StateService.setCandidatePreparations).toHaveBeenCalledWith([
                {
                    preparation: compatiblePreps[0],
                    dataset: stateMock.inventory.datasets[0]
                }
            ]);
            expect(ctrl.isFetchingPreparations).toBe(false);
        }));

        it('should not populate the compatible preparations', inject(($q, DatasetService, StateService) => {
            //given
            spyOn(DatasetService, 'getCompatiblePreparations').and.returnValue($q.when([]));
            spyOn(StateService, 'setCandidatePreparations').and.returnValue();

            //when
            expect(ctrl.isFetchingPreparations).toBe(true);
            ctrl.$onInit();
            scope.$digest();

            //then
            expect(StateService.setCandidatePreparations).toHaveBeenCalledWith([]);
            expect(ctrl.isFetchingPreparations).toBe(false);
        }));
    });

    describe('success', () => {
        beforeEach(inject(($q, $rootScope, $state, PreparationService, PreparationListService) => {
            spyOn(PreparationService, 'clone').and.returnValue($q.when(cloneId));
            spyOn(PreparationService, 'update').and.returnValue($q.when(updateId));
            spyOn(PreparationListService, 'refreshPreparations').and.returnValue($q.when(true));
            spyOn($state, 'go').and.returnValue();
            spyOn($rootScope, '$emit').and.returnValue();
        }));

        describe('preparation selection', () => {

            describe('starting from a dataset', () => {
                it('should select the given preparation', () => {
                    //given
                    let chosenPreparation = {
                        preparation: compatiblePreps[1],
                        dataset: stateMock.inventory.datasets[1]
                    };

                    let candidatePreparations = [
                        {
                            preparation: compatiblePreps[0],
                            dataset: stateMock.inventory.datasets[0]
                        },
                        chosenPreparation
                    ];

                    stateMock.playground = {
                        candidatePreparations: candidatePreparations
                    };

                    //when
                    ctrl.selectPreparation(chosenPreparation);

                    //then
                    expect(ctrl.selectedPreparation).toBe(chosenPreparation);
                });
            });

            describe('starting from a preparation', () => {
                it('should delete the current preparation and replace it with selected one', inject(($q, RecipeService, PreparationService) => {
                    //given
                    spyOn(PreparationService, 'delete').and.returnValue($q.when(true));

                    let chosenPreparation = {
                        preparation: compatiblePreps[1],
                        dataset: stateMock.inventory.datasets[1]
                    };

                    let candidatePreparations = [
                        {
                            preparation: compatiblePreps[0],
                            dataset: stateMock.inventory.datasets[0]
                        },
                        chosenPreparation
                    ];

                    stateMock.playground = {
                        candidatePreparations: candidatePreparations,
                        preparation: {id: 'aaaa-bbbb'}
                    };

                    //when
                    ctrl.selectPreparation(chosenPreparation);
                    scope.$digest();

                    //then
                    expect(ctrl.selectedPreparation).toBe(chosenPreparation);
                    expect(PreparationService.delete).toHaveBeenCalledWith(stateMock.playground.preparation);
                }));
            });
        });

        describe('apply a preparation on a dataset', () => {

            let selectedPreparation;
            let datasetId = 'dsid';
            let name = 'myDsName';

            beforeEach(() => {
                selectedPreparation = {
                    preparation: compatiblePreps[1],
                    dataset: stateMock.inventory.datasets[1]
                };
            });

            it('should clone the preparation', inject(($rootScope, $q, PreparationService) => {
                //when
                ctrl.selectPreparation(selectedPreparation);
                expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.start');
                scope.$digest();

                //then
                expect(PreparationService.clone).toHaveBeenCalledWith(ctrl.selectedPreparation.preparation.id);
            }));

            it('should update the clone preparation with the new name', inject(($rootScope, PreparationService) => {
                //when
                ctrl.selectPreparation(selectedPreparation);
                expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.start');
                scope.$digest();

                //then
                expect(PreparationService.update).toHaveBeenCalledWith(cloneId, {
                    dataSetId: ctrl.datasetId,
                    name: ctrl.datasetName
                });
            }));

            it('should refresh the preparations list', inject(($rootScope, PreparationService, PreparationListService) => {
                //when
                ctrl.selectPreparation(selectedPreparation);
                expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.start');
                scope.$digest();

                //then
                expect(PreparationListService.refreshPreparations).toHaveBeenCalled();
            }));

            it('should redirect to the preparation with the dataset', inject(($rootScope, $state, PreparationService, PreparationListService, StateService) => {
                spyOn(StateService, 'updatePreparationPickerDisplay').and.returnValue();
                spyOn(StateService, 'resetPlayground').and.returnValue();

                ctrl.selectPreparation(selectedPreparation);
                expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.start');
                scope.$digest();

                //then
                expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.stop');
                expect(StateService.updatePreparationPickerDisplay).toHaveBeenCalledWith(false);
                expect(StateService.resetPlayground).toHaveBeenCalled();
                expect($state.go).toHaveBeenCalledWith(
                    'playground.preparation',
                    {prepid: updateId},
                    {reload: true}
                );
            }));
        });
    });

    describe('failure', () => {
        describe('to apply a preparation on a dataset', () => {
            it('should hide the spinner', inject(($rootScope, $q, PreparationService) => {
                //given
                spyOn(PreparationService, 'clone').and.returnValue($q.reject());
                spyOn($rootScope, '$emit').and.returnValue();

                //when
                ctrl.selectPreparation({
                    preparation: compatiblePreps[1],
                    dataset: stateMock.inventory.datasets[1]
                });
                expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.start');
                scope.$digest();

                //then
                expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.stop');
            }));
        });
    });
});