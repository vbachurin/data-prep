/*  ============================================================================

  Copyright (C) 2006-2016 Talend Inc. - www.talend.com

  This source code is available under agreement available at
  https://github.com/Talend/data-prep/blob/master/LICENSE

  You should have received a copy of the agreement
  along with this program; if not, write to Talend SA
  9 rue Pages 92150 Suresnes, France

  ============================================================================*/

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
