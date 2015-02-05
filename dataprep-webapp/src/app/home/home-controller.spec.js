describe('Home controller', function() {
    'use strict';

    var createController, scope;
    var datasets = [
        {name: 'Customers (50 lines)'},
        {name: 'Us states'},
        {name: 'Customers (1K lines)'}
    ];

    beforeEach(module('data-prep'));

    beforeEach(inject(function($rootScope, $controller, $q, DatasetService) {
        scope = $rootScope.$new();

        createController = function() {
            var ctrl =  $controller('HomeCtrl', {
                $scope: scope
            });
            return ctrl;
        };

        spyOn(DatasetService, 'getDatasets').and.callFake(function() {return $q.when(datasets);});
    }));

    it('should get dataset on creation', inject(function(DatasetService) {
        //when
        var ctrl = createController();
        scope.$digest();

        //then
        expect(DatasetService.getDatasets).toHaveBeenCalled();
        expect(ctrl.datasets).toBe(datasets);
    }));

    describe('already created', function() {
        var ctrl;

        beforeEach(function() {
            ctrl = createController();
            scope.$digest();
        });

        it('should delete dataset, reset selected data, and refresh dataset list', inject(function($q, DatasetService) {
            //given
            spyOn(DatasetService, 'deleteDataset').and.callFake(function() {return $q.when(true);});

            var dataset = datasets[0];
            ctrl.selectedDataset = dataset;
            ctrl.selectedData = {};

            //when
            ctrl.deleteDataset(dataset);
            scope.$digest();

            //then
            expect(DatasetService.deleteDataset).toHaveBeenCalledWith(dataset);
            expect(ctrl.selectedDataset).toBeFalsy();
            expect(ctrl.selectedData).toBeFalsy();
            expect(DatasetService.getDatasets).toHaveBeenCalled();
        }));

        it('should delete dataset, do not reset selected data, and refresh dataset list', inject(function($q, DatasetService) {
            //given
            spyOn(DatasetService, 'deleteDataset').and.callFake(function() {return $q.when(true);});

            var dataset = datasets[0];
            ctrl.selectedDataset = datasets[1];
            ctrl.selectedData = {};

            //when
            ctrl.deleteDataset(dataset);
            scope.$digest();

            //then
            expect(DatasetService.deleteDataset).toHaveBeenCalledWith(dataset);
            expect(ctrl.selectedDataset).toBeTruthy();
            expect(ctrl.selectedData).toBeTruthy();
            expect(DatasetService.getDatasets).toHaveBeenCalled();
        }));

        it('should get dataset data', inject(function($q, DatasetService) {
            //given
            var data = [{column: [], records: []}];
            spyOn(DatasetService, 'getData').and.callFake(function() {return $q.when(data);});

            var dataset = datasets[0];
            ctrl.selectedDataset = datasets[1];
            ctrl.selectedData = {};

            //when
            ctrl.openDataset(dataset);
            scope.$digest();

            //then
            expect(DatasetService.getData).toHaveBeenCalledWith(dataset);
            expect(ctrl.selectedDataset).toBe(dataset);
            expect(ctrl.selectedData).toBeTruthy(data);
        }));
    });
});
