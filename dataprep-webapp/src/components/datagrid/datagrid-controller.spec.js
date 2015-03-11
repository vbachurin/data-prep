describe('DatasetColumnHeader controller', function() {
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

    it('should bind showDataGrid getter with DatasetGridService', inject(function(DatasetGridService) {
        //given
        var ctrl = createController();
        expect(ctrl.showDataGrid).toBe(false);

        //when
        DatasetGridService.show();

        //then
        expect(ctrl.showDataGrid).toBe(true);
    }));

    it('should bind showDataGrid setter with DatasetGridService', inject(function(DatasetGridService) {
        //given
        var ctrl = createController();
        expect(DatasetGridService.visible).toBe(false);

        //when
        ctrl.showDataGrid = true;

        //then
        expect(DatasetGridService.visible).toBe(true);
    }));

    it('should bind metadata getter with DatasetGridService', inject(function(DatasetGridService) {
        //given
        var metadata = {name: 'my dataset'};
        var ctrl = createController();
        expect(ctrl.metadata).toBeFalsy();

        //when
        DatasetGridService.setDataset(metadata);

        //then
        expect(ctrl.metadata).toBe(metadata);
    }));

    it('should bind metadata setter with DatasetGridService', inject(function(DatasetGridService) {
        //given
        var metadata = {name: 'my dataset'};
        var ctrl = createController();
        expect(DatasetGridService.metadata).toBeFalsy();

        //when
        ctrl.metadata = metadata;

        //then
        expect(DatasetGridService.metadata).toBe(metadata);
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

    it('should bind data setter with DatasetGridService', inject(function(DatasetGridService) {
        //given
        var data = {records: [{col: 'value'}]};
        var ctrl = createController();
        expect(DatasetGridService.data).toBeFalsy();

        //when
        ctrl.data = data;

        //then
        expect(DatasetGridService.data).toBe(data);
    }));
});
