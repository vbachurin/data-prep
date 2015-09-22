(function() {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.datagrid.controller:DatagridCtrl
     * @description Dataset grid controller.
     * @requires data-prep.services.state.constant:state
     * @requires data-prep.datagrid.service:DatagridTooltipService
     */
    function DatagridCtrl(state, DatagridTooltipService) {
        this.datagridTooltipService = DatagridTooltipService;
        this.state = state;
    }

    /**
     * @ngdoc property
     * @name tooltip
     * @propertyOf data-prep.datagrid.controller:DatagridCtrl
     * @description The tooltip infos
     * This is bound to {@link data-prep.datagrid.service:DatagridTooltipServicee DatagridTooltipServicee}.tooltip
     */
    Object.defineProperty(DatagridCtrl.prototype,
        'tooltip', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.datagridTooltipService.tooltip;
            }
        });

    /**
     * @ngdoc property
     * @name showTooltip
     * @propertyOf data-prep.datagrid.controller:DatagridCtrl
     * @description The tooltip display flag
     * This is bound to {@link data-prep.datagrid.service:DatagridTooltipServicee DatagridTooltipServicee}.showTooltip
     */
    Object.defineProperty(DatagridCtrl.prototype,
        'showTooltip', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.datagridTooltipService.showTooltip;
            }
        });

    angular.module('data-prep.datagrid')
        .controller('DatagridCtrl', DatagridCtrl);
})();