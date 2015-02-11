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

        beforeEach(inject(function($q, DatasetService) {
            ctrl = createController();
            scope.$digest();
            
            spyOn(DatasetService, 'deleteDataset').and.callFake(function() {return $q.when(true);});
        }));

        it('should delete dataset and refresh dataset list', inject(function(DatasetService) {
            //given
            var dataset = datasets[0];

            //when
            ctrl.deleteDataset(dataset);
            scope.$digest();

            //then
            expect(DatasetService.deleteDataset).toHaveBeenCalledWith(dataset);
            expect(DatasetService.getDatasets).toHaveBeenCalled();
        }));
    });
});
