/*  ============================================================================

  Copyright (C) 2006-2016 Talend Inc. - www.talend.com

  This source code is available under agreement available at
  https://github.com/Talend/data-prep/blob/master/LICENSE

  You should have received a copy of the agreement
  along with this program; if not, write to Talend SA
  9 rue Pages 92150 Suresnes, France

  ============================================================================*/

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