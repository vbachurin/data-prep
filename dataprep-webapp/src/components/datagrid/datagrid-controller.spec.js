describe('Datagrid controller', function() {
    'use strict';

    var createController, scope;

    beforeEach(module('data-prep.datagrid'));

    beforeEach(inject(function($rootScope, $controller) {
        scope = $rootScope.$new();

        createController = function() {
            var ctrl =  $controller('DatagridCtrl', {
                $scope: scope
            });
            return ctrl;
        };
    }));

    it('should bind metadata getter with DatasetGridService', inject(function(DatasetGridService) {
        //given
        var metadata = {name: 'my dataset'};
        var ctrl = createController();
        expect(ctrl.metadata).toBeFalsy();

        //when
        DatasetGridService.setDataset(metadata, {records: []});

        //then
        expect(ctrl.metadata).toBe(metadata);
    }));

    it('should bind data getter with DatasetGridService', inject(function(DatasetGridService) {
        //given
        var data = {records: [{col: 'value'}]};
        var ctrl = createController();
        expect(ctrl.data).toBeFalsy();

        //when
        DatasetGridService.setDataset(null, data);

        //then
        expect(ctrl.data).toBe(data);
    }));

    it('should bind dataView getter with DatasetGridService', inject(function(DatasetGridService) {
        //when
        var ctrl = createController();

        //then
        expect(ctrl.dataView).toBe(DatasetGridService.dataView);
    }));

    it('should bind dataView filters with DatasetGridService', inject(function(DatasetGridService) {
        //given
        var ctrl = createController();
        expect(ctrl.filters.length).toBe(0);

        //when
        DatasetGridService.addContainFilter('colId', 'searchValue');

        //then
        expect(ctrl.filters.length).toBe(1);
        var predicate = ctrl.filters[0];
        expect(predicate({colId: 'aze searchValue aze'})).toBe(true);
        expect(predicate({colId: 'aze aze'})).toBe(false);
    }));
});
