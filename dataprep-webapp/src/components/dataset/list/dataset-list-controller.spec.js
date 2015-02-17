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

    beforeEach(inject(function ($rootScope, $controller, $q, DatasetService) {
        var datasetsValues = [datasets, refreshedDatasets];
        scope = $rootScope.$new();

        createController = function () {
            var ctrl = $controller('DatasetListCtrl', {
                $scope: scope
            });
            return ctrl;
        };

        spyOn(DatasetService, 'refreshDatasets').and.callFake(function () {
            return $q.when(datasetsValues.shift());
        });
    }));

    it('should get dataset on creation', inject(function (DatasetService) {
        //when
        var ctrl = createController();
        scope.$digest();

        //then
        expect(DatasetService.refreshDatasets).toHaveBeenCalled();
        expect(ctrl.datasets).toBe(datasets);
    }));

    describe('already created', function () {
        var ctrl;

        beforeEach(inject(function ($rootScope, $q, DatasetService, DatasetGridService) {
            ctrl = createController();
            scope.$digest();

            spyOn(DatasetService, 'deleteDataset').and.returnValue($q.when(true));
            spyOn(DatasetService, 'getDataFromId').and.returnValue($q.when(data));
            spyOn($rootScope, '$emit').and.callThrough();
            spyOn(DatasetGridService, 'setDataset').and.callThrough();
            spyOn(DatasetGridService, 'show').and.callThrough();
        }));

        it('should delete dataset and refresh dataset list', inject(function (DatasetService) {
            //given
            var dataset = datasets[0];

            //when
            ctrl.delete(dataset);
            scope.$digest();

            //then
            expect(DatasetService.deleteDataset).toHaveBeenCalledWith(dataset);
            expect(DatasetService.refreshDatasets).toHaveBeenCalled();
        }));

        it('should refresh dataset list on "talend.datasets.refresh" event', inject(function ($rootScope, DatasetService) {
            //given
            expect(DatasetService.refreshDatasets.calls.count()).toBe(1);
            expect(ctrl.datasets).toBe(datasets);

            //when
            $rootScope.$emit('talend.datasets.refresh');
            scope.$digest();

            //then
            expect(DatasetService.refreshDatasets.calls.count()).toBe(2);
            expect(ctrl.datasets).toBe(refreshedDatasets);
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
    });
});
