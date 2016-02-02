/**
 * @ngdoc controller
 * @name data-prep.datagrid.controller:DatagridCtrl
 * @description Dataset grid controller.
 * @requires data-prep.services.state.constant:state
 * @requires data-prep.datagrid.service:DatagridTooltipService
 */
export default function DatagridCtrl(state, DatagridTooltipService) {
    'ngInject';

    this.datagridTooltipService = DatagridTooltipService;
    this.state = state;
}