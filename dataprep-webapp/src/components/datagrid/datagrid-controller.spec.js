describe('Datagrid controller', function () {
    'use strict';

    var createController, scope;

    beforeEach(module('data-prep.datagrid'));

    beforeEach(inject(function($rootScope, $controller) {
        scope = $rootScope.$new();

        createController = function () {
            var ctrl = $controller('DatagridCtrl', {
                $scope: scope
            });
            return ctrl;
        };
    }));

    it('should bind tooltip getter to DatagridTooltipService', inject(function(DatagridTooltipService) {
        //given
        var newTooltip = {colId: '0000'};

        var ctrl = createController();
        expect(ctrl.tooltip).toEqual({});

        //when
        DatagridTooltipService.tooltip = newTooltip;

        //then
        expect(ctrl.tooltip).toEqual(newTooltip);
    }));

    it('should bind showTooltip getter to DatagridTooltipService', inject(function(DatagridTooltipService) {
        //given
        var ctrl = createController();
        expect(ctrl.showTooltip).toEqual(false);

        //when
        DatagridTooltipService.showTooltip = true;

        //then
        expect(ctrl.showTooltip).toEqual(true);
    }));
});
