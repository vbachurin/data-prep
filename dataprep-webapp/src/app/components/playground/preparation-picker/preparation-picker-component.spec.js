/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

describe('Preparation Picker Component', () => {

    let createElement, scope, element, stateMock;

    const datasets = [
        {
            'id': 'de3cc32a-b624-484e-b8e7-dab9061a009c',
            'name': 'customers_jso_light',
            'author': 'anonymousUser',
            'records': 15,
            'nbLinesHeader': 1,
            'nbLinesFooter': 0,
            'created': '03-30-2015 08:06'
        },
        {
            'id': '3b21388c-f54a-4334-9bef-748912d0806f',
            'name': 'customers_jso',
            'author': 'anonymousUser',
            'records': 1000,
            'nbLinesHeader': 1,
            'nbLinesFooter': 0,
            'created': '03-30-2015 07:35'
        },
        {
            'id': '4d0a2718-bec6-4614-ad6c-8b3b326ff6c7',
            'name': 'first_interactions',
            'author': 'anonymousUser',
            'records': 29379,
            'nbLinesHeader': 1,
            'nbLinesFooter': 0,
            'created': '03-30-2015 08:05'
        },
        {
            'id': '5e95be9e-88cd-4765-9ecc-ee48cc28b6d5',
            'name': 'first_interactions_400',
            'author': 'anonymousUser',
            'records': 400,
            'nbLinesHeader': 1,
            'nbLinesFooter': 0,
            'created': '03-30-2015 08:06'
        }
    ];

    const candidatePreparations = [
        {
            preparation: {
                'id': 'ab136cbf0923a7f11bea713adb74ecf919e05cfa',
                'dataSetId': 'de3cc32a-b624-484e-b8e7-dab9061a009c',
                'author': 'anonymousUser',
                'creationDate': 1427447300000,
                'lastModificationDate': 1427447300300
            },
            dataset: {
                'id': 'de3cc32a-b624-484e-b8e7-dab9061a009c',
                'name': 'customers_jso_light',
                'author': 'anonymousUser',
                'records': 15,
                'nbLinesHeader': 1,
                'nbLinesFooter': 0,
                'created': '03-30-2015 08:06'
            }
        },
        {
            preparation: {
                'id': 'fbaa18e82e913e97e5f0e9d40f04413412be1126',
                'dataSetId': '4d0a2718-bec6-4614-ad6c-8b3b326ff6c7',
                'author': 'anonymousUser',
                'creationDate': 1427447330000,
                'lastModificationDate': 1427447330693
            },
            dataset: {
                'id': '4d0a2718-bec6-4614-ad6c-8b3b326ff6c7',
                'name': 'customers_jso',
                'author': 'anonymousUser',
                'records': 1000,
                'nbLinesHeader': 1,
                'nbLinesFooter': 0,
                'created': '03-30-2015 07:35'
            }
        }
    ];

    const rawPreparations = [
        {
            'id': 'ab136cbf0923a7f11bea713adb74ecf919e05cfa',
            'dataSetId': 'de3cc32a-b624-484e-b8e7-dab9061a009c',
            'author': 'anonymousUser',
            'creationDate': 1427447300000,
            'lastModificationDate': 1427447300300
        },
        {
            'id': 'fbaa18e82e913e97e5f0e9d40f04413412be1126',
            'dataSetId': '4d0a2718-bec6-4614-ad6c-8b3b326ff6c7',
            'author': 'anonymousUser',
            'creationDate': 1427447330000,
            'lastModificationDate': 1427447330693
        }
    ];

    beforeEach(angular.mock.module('data-prep.preparation-picker', ($provide) => {
        stateMock = {
            playground: {
                candidatePreparations: candidatePreparations,
                preparation: null
            },
            inventory: {
                datasets: datasets
            }
        };
        $provide.constant('state', stateMock);
    }));

    beforeEach(angular.mock.module('htmlTemplates'));

    beforeEach(inject(($q, $rootScope, $compile, DatasetService) => {
        scope = $rootScope.$new();

        createElement = () => {
            element = angular.element(
                `<preparation-picker
                    dataset-name="{{datasetName}}"
                    dataset-id="{{datasetId}}">
                </preparation-picker>`);
            $compile(element)(scope);
            scope.$digest();
        };

        spyOn(DatasetService, 'getCompatiblePreparations').and.returnValue($q.when(rawPreparations));
    }));

    afterEach(() => {
        scope.$destroy();
        element.remove();
    });

    describe('display preparations list', () => {

        it('should render preparations list starting form a dataset', () => {
            //given
            scope.datasetName = 'myDsName';
            scope.datasetId = 'mydatasetId';

            //when
            createElement();

            //then
            expect(element.find('.preparation').length).toBe(candidatePreparations.length);
        });
    });

    describe('select a preparation to apply on a dataset', () => {

        beforeEach(inject(($q, $rootScope, $state, StateService, PreparationService, PreparationListService) => {
            spyOn(PreparationService, 'clone').and.returnValue($q.when('cloneId'));
            spyOn(PreparationService, 'update').and.returnValue($q.when('updateId'));
            spyOn(PreparationListService, 'refreshPreparations').and.returnValue($q.when(true));
            spyOn($state, 'go').and.returnValue();
            spyOn($rootScope, '$emit').and.returnValue();
            spyOn(StateService, 'updatePreparationPickerDisplay').and.returnValue();
            spyOn(StateService, 'resetPlayground').and.returnValue();
        }));

        it('should render preparations list starting form a dataset', inject(($q, $rootScope, $state, StateService, PreparationService, PreparationListService) => {
            //given
            scope.datasetName = 'myDsName';
            scope.datasetId = 'mydatasetId';
            createElement();
            let preparationTile = element.find('.preparation').eq(0);

            //when
            preparationTile.click();
            $rootScope.$digest();

            //then
            expect(PreparationService.clone).toHaveBeenCalledWith(candidatePreparations[0].preparation.id);
            expect(PreparationService.update).toHaveBeenCalledWith('cloneId', {
                dataSetId: 'mydatasetId',
                name: 'myDsName'
            });
            expect(PreparationListService.refreshPreparations).toHaveBeenCalled();
            expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.stop');
            expect(StateService.updatePreparationPickerDisplay).toHaveBeenCalledWith(false);
            expect(StateService.resetPlayground).toHaveBeenCalled();
            expect($state.go).toHaveBeenCalledWith(
                'playground.preparation',
                {prepid: 'updateId'},
                {reload: true}
            );
        }));
    });

});