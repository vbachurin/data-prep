(function () {
    'use strict';

    var lookupState = {
        dataView: new Slick.Data.DataView({inlineFilters: false}),
        addedToLookup: [],
        lookupColumnsToAdd: [],
        datasets: []
    };

    /**
     * @ngdoc service
     * @name data-prep.services.state.service:GridStateService
     * @description Grid state service. Manage the lookup grid part state
     */
    function LookupStateService() {
        return {
            reset: reset,

            setColumnFocus: setColumnFocus,
            setData: setData,
            setGridSelection: setGridSelection,
            setDataset: setDataset,
            setLookupColumnsToAdd: setLookupColumnsToAdd,
            setPotentialDatasets: setPotentialDatasets
        };

        /**
         * @ngdoc method
         * @name setLookupColumnsToAdd
         * @methodOf data-prep.services.state.service:GridStateService
         * @description sets the lookupColumnsToAdd array of the columns to add to the
         */
        function setLookupColumnsToAdd() {
            function getRidIsAddedFlag(obj) {
                return _.omit(obj, 'isAdded');
            }

            lookupState.lookupColumnsToAdd = _.chain(lookupState.addedToLookup)
                .filter(function (col) {
                    return col.isAdded === true;
                })
                .filter(function (col) {
                    return col.id !== lookupState.selectedColumn.id;
                })
                .map(getRidIsAddedFlag)
                .value();
        }

        /**
         * @ngdoc method
         * @name setColumnFocus
         * @methodOf data-prep.services.state.service:GridStateService
         * @param {string} columnId The column id to focus on
         * @description Set the column id to focus on
         */
        function setColumnFocus(columnId) {
            lookupState.columnFocus = columnId;
        }

        /**
         * @ngdoc method
         * @name setPotentialDatasets
         * @methodOf data-prep.services.state.service:GridStateService
         * @param {Array} datasets the datasets with which a lookup is possible
         * @description sets the datasets with which a lookup is possible
         */
        function setPotentialDatasets(datasets) {
            lookupState.datasets = datasets;
        }

        /**
         * @ngdoc method
         * @name setData
         * @methodOf data-prep.services.state.service:GridStateService
         * @param {object} data The data
         * @description Set new data in the grid and resets the isAdded label to false for the new columns
         */
        function setData(data) {
            lookupState.dataView.beginUpdate();
            lookupState.dataView.setItems(data.records, 'tdpId');
            lookupState.dataView.endUpdate();

            updateSelectedColumn(data);
            lookupState.addedToLookup = [];
            lookupState.lookupColumnsToAdd = [];//in order to disable the Confirm button & to synch with the state
            _.each(data.columns, function (col) {
                lookupState.addedToLookup.push({
                    isAdded: false,
                    name: col.name,
                    id: col.id
                });
            });
        }


        /**
         * @ngdoc method
         * @name setDataset
         * @methodOf data-prep.services.state.service:GridStateService
         * @param {object} dataset The dataset
         * @description Sets new dataset
         */
        function setDataset(dataset) {
            lookupState.dataset = dataset;
        }

        /**
         * @ngdoc method
         * @name updateSelectedColumn
         * @methodOf data-prep.services.state.service:GridStateService
         * @param {object} data The data
         * @description Determine the selected column from the new data
         */
        function updateSelectedColumn(data) {
            //if there is already a selected column, we update the column metadata to reference one of the new columns
            if (lookupState.selectedColumn && data.columns) {
                lookupState.selectedColumn = _.find(data.columns, {id: lookupState.selectedColumn.id}) || data.columns[0];
            }
            //the first column is selected by default
            else {
                lookupState.selectedColumn = data.columns[0];
            }
        }

        /**
         * @ngdoc method
         * @name setGridSelection
         * @methodOf data-prep.services.state.service:GridStateService
         * @param {object} column The column metadata
         * @description Set the actual selected column and line
         */
        function setGridSelection(column) {
            lookupState.selectedColumn = column;
            if (column) {
                setLookupColumnsToAdd();
            }
        }

        /**
         * @ngdoc method
         * @name reset
         * @methodOf data-prep.services.state.service:GridStateService
         * @description Reset the grid internal values
         */
        function reset() {
            lookupState.columnFocus = null;
            lookupState.selectedColumn = null;
            lookupState.selectedLine = null;
            lookupState.lookupColumnsToAdd = [];
            lookupState.addedToLookup = [];
            lookupState.dataset = null;
            lookupState.datasets = [];
        }
    }

    angular.module('data-prep.services.state')
        .service('LookupStateService', LookupStateService)
        .constant('lookupState', lookupState);
})();