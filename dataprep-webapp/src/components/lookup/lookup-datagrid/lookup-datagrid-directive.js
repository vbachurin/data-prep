(function () {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.lookup-datagrid.directive:Datagrid
     * @description This directive create the SlickGrid lookup-datagrid<br/>
     * Watchers :
     * <ul>
     *         <li>Grid header directive list : insert grid header in SlickGrid headers</li>
     *         <li>Grid Metadata : it means the loaded dataset has changed, the styles are reset</li>
     *         <li>Grid data : update grid headers and styles</li>
     *         <li>Filters : reset the styles</li>
     * </ul>
     *
     * @requires data-prep.state.service:state
     * @requires data-prep.lookup-datagrid.service:DatagridGridService
     * @requires data-prep.lookup-datagrid.service:DatagridColumnService
     * @requires data-prep.lookup-datagrid.service:DatagridStyleService
     * @requires data-prep.lookup-datagrid.service:DatagridSizeService
     * @requires data-prep.lookup-datagrid.service:DatagridTooltipService
     * @requires data-prep.lookup-datagrid.service:DatagridExternalService
     * @restrict E
     */
    function LookupDatagrid(state, LookupDatagridGridService, LookupDatagridColumnService, LookupDatagridStyleService,
                            LookupDatagridSizeService, LookupDatagridTooltipService) {
        return {
            restrict: 'E',
            templateUrl: 'components/lookup/lookup-datagrid/lookup-datagrid.html',
            bindToController: true,
            controllerAs: 'lookupDatagridCtrl',
            controller: 'LookupDatagridCtrl',
            link: function (scope, iElement, iAttrs, ctrl) {
                var grid;
                var columnTimeout, focusTimeout;

                //------------------------------------------------------------------------------------------------------
                //--------------------------------------------------GETTERS---------------------------------------------
                //------------------------------------------------------------------------------------------------------

                /**
                 * @ngdoc method
                 * @name getData
                 * @methodOf data-prep.lookup-datagrid.directive:Datagrid
                 * @description [PRIVATE] Get the loaded data
                 */
                var getData = function getData() {
                    return state.playground.lookupData;
                };

                //------------------------------------------------------------------------------------------------------
                //---------------------------------------------------UTILS----------------------------------------------
                //------------------------------------------------------------------------------------------------------
                /**
                 * @ngdoc method
                 * @name onMetadataChange
                 * @methodOf data-prep.lookup-datagrid.directive:Datagrid
                 * @description [PRIVATE] Reset cell styles, scroll to top and expect to recreate all columns on next update
                 */
                var onMetadataChange = function onMetadataChange() {
                    if (grid) {
                        LookupDatagridStyleService.resetCellStyles();
                        grid.scrollRowToTop(0);
                        LookupDatagridColumnService.renewAllColumns(true);
                    }
                };

                /**
                 * @ngdoc method
                 * @name onDataChange
                 * @methodOf data-prep.lookup-datagrid.directive:Datagrid
                 * @description [PRIVATE] Update and resize the columns with its headers, set grid styles
                 */
                var onDataChange = function onDataChange(data) {
                    if (data) {
                        onMetadataChange();

                        initGridIfNeeded();
                        var columns;
                        var selectedColumn;
                        var stateSelectedColumn = ctrl.state.playground.lookupGrid.selectedColumn; //column metadata
                        //var stateSelectedLine = ctrl.state.playground.lookupGrid.selectedLine; //column metadata

                        //create columns, manage style and size, set columns in grid
                        clearTimeout(columnTimeout);
                        columnTimeout = setTimeout(function () {
                            columns = LookupDatagridColumnService.createColumns(data.columns, data.preview);

                            if(!data.preview) {
                                selectedColumn = stateSelectedColumn ? _.find(columns, {id: stateSelectedColumn.id}) : null;
                            }

                            LookupDatagridStyleService.updateColumnClass(columns, selectedColumn);
                            LookupDatagridSizeService.autosizeColumns(columns); // IMPORTANT : this set columns in the grid
                            LookupDatagridColumnService.renewAllColumns(false);
                        }, 0);

                        //focus specific column
                        clearTimeout(focusTimeout);
                        focusTimeout = setTimeout(LookupDatagridGridService.navigateToFocusedColumn, 300);
                    }
                };

                //------------------------------------------------------------------------------------------------------
                //---------------------------------------------------INIT-----------------------------------------------
                //------------------------------------------------------------------------------------------------------
                /**
                 * @ngdoc method
                 * @name initGridIfNeeded
                 * @methodOf data-prep.lookup-datagrid.directive:Datagrid
                 * @description [PRIVATE] Init Slick grid and init lookup-datagrid private services.
                 */
                var initGridIfNeeded = function () {
                    if (!grid) {
                        grid = LookupDatagridGridService.initGrid('#lookup-datagrid');

                        // the tooltip ruler is used compute a cell text regardless of the font and zoom used.
                        // To do so, the text is put into an invisible span so that the span can be measured.
                        LookupDatagridTooltipService.tooltipRuler = iElement.find('#lookup-tooltip-ruler').eq(0);
                    }
                };

                //------------------------------------------------------------------------------------------------------
                //-------------------------------------------------WATCHERS---------------------------------------------
                //------------------------------------------------------------------------------------------------------
                /**
                 * Update grid columns and invalidate grid on data change
                 */
                scope.$watch(getData, onDataChange);
            }
        };
    }

    angular.module('data-prep.lookup')
        .directive('lookupDatagrid', LookupDatagrid);
})();

