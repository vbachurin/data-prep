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

    beforeEach(module('data-prep-dataset'));

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

        beforeEach(inject(function ($rootScope, $q, toaster, DatasetService, DatasetGridService) {
            ctrl = createController();
            scope.$digest();

            spyOn(DatasetService, 'deleteDataset').and.returnValue($q.when(true));
            spyOn(DatasetService, 'getDataFromId').and.returnValue($q.when(data));
            spyOn($rootScope, '$emit').and.callThrough();
            spyOn(DatasetGridService, 'setDataset').and.callThrough();
            spyOn(DatasetGridService, 'show').and.callThrough();
            spyOn(toaster, 'pop').and.callThrough();
        }));

        it('should delete dataset, show toast and refresh dataset list', inject(function ($q, toaster, DatasetService, DatasetListService, TalendConfirmService) {
            //given
            var dataset = datasets[0];
            spyOn(TalendConfirmService, 'confirm').and.returnValue($q.when(true));

            //when
            ctrl.delete(dataset);
            scope.$digest();

            //then
            expect(TalendConfirmService.confirm).toHaveBeenCalledWith('You are going to permanently delete the dataset "Customers (50 lines)".', 'This operation cannot be undone. Are you sure ?');
            expect(DatasetService.deleteDataset).toHaveBeenCalledWith(dataset);
            expect(toaster.pop).toHaveBeenCalledWith('success', 'Remove dataset', 'The dataset "Customers (50 lines)" has been removed.');
            expect(DatasetListService.refreshDatasets).toHaveBeenCalled();
        }));

        it('should reset selected infos when the dataset is deleted', inject(function ($q, toaster, DatasetService, DatasetListService, TalendConfirmService) {
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

        it('should get selected dataset data and open datagrid modal', inject(function ($rootScope, DatasetService, DatasetGridService) {
            //given
            var dataset = {name: 'Customers (50 lines)', id: 'aA2bc348e933bc2'};

            //when
            ctrl.open(dataset);
            $rootScope.$apply();

            //then
            expect(DatasetService.getDataFromId).toHaveBeenCalledWith(dataset.id, false);
            expect(ctrl.lastSelectedMetadata).toBe(dataset);
            expect(ctrl.lastSelectedData).toBe(data);
            expect(DatasetGridService.setDataset).toHaveBeenCalledWith(dataset, data);
            expect(DatasetGridService.show).toHaveBeenCalled();
        }));

        it('should open datagrid modal with existing data when dataset is the same as previous', inject(function ($rootScope, DatasetService, DatasetGridService) {
            //given
            var dataset = {name: 'Customers (50 lines)', id: 'aA2bc348e933bc2'};
            ctrl.lastSelectedMetadata = dataset;
            ctrl.lastSelectedData = data;

            //when
            ctrl.open(dataset);
            $rootScope.$apply();

            //then
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
