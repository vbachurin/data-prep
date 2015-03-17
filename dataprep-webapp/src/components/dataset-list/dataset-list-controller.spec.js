describe('Dataset list controller', function () {
    'use strict';

    var createController, scope;
    var datasets = [
        {name: 'Customers (50 lines)'},
        {name: 'Us states'},
        {name: 'Customers (1K lines)'}
    ];
    var refreshedDatasets = [
        {name: 'Customers (50 lines)'},
        {name: 'Us states'}
    ];
    var data = [{
        columns: [
            {
                id: 'Postal',
                quality: {
                    empty: 5,
                    invalid: 10,
                    valid: 72
                },
                type: 'string'
            },
            {
                id: 'State',
                quality: {
                    empty: 5,
                    invalid: 10,
                    valid: 72
                },
                type: 'string'
            },
            {
                id: 'Capital',
                quality: {
                    empty: 5,
                    invalid: 10,
                    valid: 72
                },
                type: 'string'
            },
            {
                id: 'MostPopulousCity',
                quality: {
                    empty: 5,
                    invalid: 10,
                    valid: 72
                },
                type: 'string'
            }
        ],
        records: [
            {
                Postal: 'AL',
                State: 'Alabama',
                Capital: 'Montgomery',
                MostPopulousCity: 'Birmingham city'
            },
            {
                Postal: 'AK',
                State: 'Alaska',
                Capital: 'Juneau',
                MostPopulousCity: 'Anchorage'
            }
        ]
    }];

    beforeEach(module('data-prep.dataset-list'));

    beforeEach(inject(function ($rootScope, $controller, DatasetListService) {
        var datasetsValues = [datasets, refreshedDatasets];
        scope = $rootScope.$new();

        createController = function () {
            var ctrl = $controller('DatasetListCtrl', {
                $scope: scope
            });
            return ctrl;
        };

        spyOn(DatasetListService, 'refreshDatasets').and.callFake(function () {
            DatasetListService.datasets = datasetsValues.shift();
        });
    }));

    it('should get dataset on creation', inject(function (DatasetListService) {
        //when
        var ctrl = createController();
        scope.$digest();

        //then
        expect(DatasetListService.refreshDatasets).toHaveBeenCalled();
        expect(ctrl.datasets).toBe(datasets);
    }));

    describe('already created', function () {
        var ctrl;

        beforeEach(inject(function ($rootScope, $q, MessageService, DatasetService, DatasetGridService, FilterService) {
            ctrl = createController();
            scope.$digest();

            spyOn(DatasetService, 'deleteDataset').and.returnValue($q.when(true));
            spyOn(DatasetService, 'getDataFromId').and.returnValue($q.when(data));
            spyOn($rootScope, '$emit').and.callThrough();
            spyOn(FilterService, 'removeAllFilters').and.callFake(function() {});
            spyOn(DatasetGridService, 'setDataset').and.callFake(function() {});
            spyOn(DatasetGridService, 'show').and.callThrough();
            spyOn(MessageService, 'success').and.callThrough();
        }));

        it('should delete dataset, show toast and refresh dataset list', inject(function ($q, MessageService, DatasetService, DatasetListService, TalendConfirmService) {
            //given
            var dataset = datasets[0];
            spyOn(TalendConfirmService, 'confirm').and.returnValue($q.when(true));

            //when
            ctrl.delete(dataset);
            scope.$digest();

            //then
            expect(TalendConfirmService.confirm).toHaveBeenCalledWith({disableEnter: true}, [ 'DELETE_PERMANENTLY', 'NO_UNDONE_CONFIRM' ], { dataset: 'Customers (50 lines)' });
            expect(DatasetService.deleteDataset).toHaveBeenCalledWith(dataset);
            expect(MessageService.success).toHaveBeenCalledWith('DATASET_REMOVE_SUCCESS_TITLE', 'DATASET_REMOVE_SUCCESS', {dataset: 'Customers (50 lines)'});
            expect(DatasetListService.refreshDatasets).toHaveBeenCalled();
        }));

        it('should reset selected infos when the dataset is deleted', inject(function ($q, DatasetService, DatasetListService, TalendConfirmService) {
            //given
            var dataset = datasets[0];
            ctrl.lastSelectedMetadata = dataset;
            ctrl.lastSelectedData = {};
            spyOn(TalendConfirmService, 'confirm').and.returnValue($q.when(true));

            //when
            ctrl.delete(dataset);
            scope.$digest();

            //then
            expect(ctrl.lastSelectedMetadata).toBeFalsy();
            expect(ctrl.lastSelectedData).toBeFalsy();
        }));

        it('should get selected dataset data and open datagrid modal', inject(function ($rootScope, DatasetService, DatasetGridService, FilterService) {
            //given
            var dataset = {name: 'Customers (50 lines)', id: 'aA2bc348e933bc2'};

            //when
            ctrl.open(dataset);
            $rootScope.$apply();

            //then
            expect(DatasetService.getDataFromId).toHaveBeenCalledWith(dataset.id, false);
            expect(ctrl.lastSelectedMetadata).toBe(dataset);
            expect(ctrl.lastSelectedData).toBe(data);
            expect(FilterService.removeAllFilters).toHaveBeenCalled();
            expect(DatasetGridService.setDataset).toHaveBeenCalledWith(dataset, data);
            expect(DatasetGridService.show).toHaveBeenCalled();
        }));

        it('should open datagrid modal with existing data when dataset is the same as previous', inject(function ($rootScope, DatasetService, DatasetGridService, FilterService) {
            //given
            var dataset = {name: 'Customers (50 lines)', id: 'aA2bc348e933bc2'};
            ctrl.lastSelectedMetadata = dataset;
            ctrl.lastSelectedData = data;

            //when
            ctrl.open(dataset);
            $rootScope.$apply();

            //then
            expect(FilterService.removeAllFilters).not.toHaveBeenCalled();
            expect(DatasetService.getDataFromId).not.toHaveBeenCalled();
            expect(DatasetGridService.setDataset).toHaveBeenCalledWith(dataset, data);
            expect(DatasetGridService.show).toHaveBeenCalled();
        }));

        it('should bind datasets getter to DatasetListService.datasets', inject(function (DatasetListService) {
            //given
            expect(ctrl.datasets).toBe(datasets);

            //when
            DatasetListService.datasets = refreshedDatasets;

            //then
            expect(ctrl.datasets).toBe(refreshedDatasets);
        }));

        it('should bind datasets setter to DatasetListService.datasets', inject(function (DatasetListService) {
            //given
            expect(DatasetListService.datasets).toBe(datasets);

            //when
            ctrl.datasets = refreshedDatasets;

            //then
            expect(DatasetListService.datasets).toBe(refreshedDatasets);
        }));
    });
});
