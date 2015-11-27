(function () {
    'use strict';

    var lookupState = {
        actions: [],                                                // the lookup actions (1 action per dataset)
        columnCheckboxes: [],                                       // column checkboxes model
        columnsToAdd: [],                                           // columns that are checked
        dataset: null,                                              // loaded dataset
        dataView: new Slick.Data.DataView({inlineFilters: false}),  // grid view that hold the dataset data
        visibility: false                                           // visibility flag
    };

    /**
     * @ngdoc service
     * @name data-prep.services.state.service:GridStateService
     * @description Lookup state service.
     */
    function LookupStateService() {
        return {
            reset: reset,

            setActions: setActions,//
            setData: setData,//
            setDataset: setDataset,//
            setSelectedColumn: setSelectedColumn,//
            setVisibility: setVisibility,//
            updateColumnsToAdd: updateColumnsToAdd//
        };

        /**
         * @ngdoc method
         * @name setVisibility
         * @methodOf data-prep.services.state.service:GridStateService
         * @description Set the lookup visibility
         */
        function setVisibility(visibility) {
            lookupState.visibility = visibility;
        }

        /**
         * @ngdoc method
         * @name setData
         * @methodOf data-prep.services.state.service:GridStateService
         * @param {object} data The data
         * @description Set new data in the grid and reset the column checkboxes
         */
        function setData(data) {
            lookupState.dataView.beginUpdate();
            lookupState.dataView.setItems(data.records, 'tdpId');
            lookupState.dataView.endUpdate();

            lookupState.selectedColumn = data.columns[0];
            lookupState.columnsToAdd = [];
            lookupState.columnCheckboxes = _.map(data.columns, function(col) {
                return {
                    id: col.id,
                    name: col.name,
                    isAdded: false
                };
            });
        }

        /**
         * @ngdoc method
         * @name updateColumnsToAdd
         * @methodOf data-prep.services.state.service:GridStateService
         * @description Update the columns to add depending on the checkboxes state
         */
        function updateColumnsToAdd() {
            lookupState.columnsToAdd = _.chain(lookupState.columnCheckboxes)
                .filter('isAdded')
                .filter(function (col) {
                    return col.id !== lookupState.selectedColumn.id;
                })
                .map(function (obj) {
                    return _.omit(obj, 'isAdded');
                })
                .value();
        }

        /**
         * @ngdoc method
         * @name setActions
         * @methodOf data-prep.services.state.service:GridStateService
         * @param {Array} actions The lookup actions (1 per possible dataset)
         * @description Sets the actions
         */
        function setActions(actions) {
            lookupState.actions = actions;
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
         * @name setSelectedColumn
         * @methodOf data-prep.services.state.service:GridStateService
         * @param {object} column The column metadata
         * @description Set the actual selected column and line
         */
        function setSelectedColumn(column) {
            lookupState.selectedColumn = column;
            if (column) {
                updateColumnsToAdd();
            }
        }

        /**
         * @ngdoc method
         * @name reset
         * @methodOf data-prep.services.state.service:GridStateService
         * @description Reset the grid internal values
         */
        function reset() {
            lookupState.actions = [];
            lookupState.columnsToAdd = [];
            lookupState.columnCheckboxes = [];
            lookupState.dataset = null;
            lookupState.selectedColumn = null;
            lookupState.visibility = false;
        }
    }

    angular.module('data-prep.services.state')
        .service('LookupStateService', LookupStateService)
        .constant('lookupState', lookupState);
})();