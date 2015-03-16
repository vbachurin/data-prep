describe('Dataset playground controller', function() {
    'use strict';

    var createController, scope;

    beforeEach(module('data-prep.dataset-playground'));

    beforeEach(inject(function($rootScope, $controller) {
        scope = $rootScope.$new();

        createController = function() {
            var ctrl =  $controller('DatasetPlaygroundCtrl', {
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
        DatasetGridService.setDataset(metadata, {records: []});

        //then
        expect(ctrl.metadata).toBe(metadata);
    }));

});
