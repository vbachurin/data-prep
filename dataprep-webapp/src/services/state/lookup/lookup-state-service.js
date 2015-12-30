(function () {
    'use strict';

    var lookupState = {
        actions: [],                                                // the lookup actions (1 action per dataset)
        columnCheckboxes: [],                                       // column checkboxes model
        columnsToAdd: [],                                           // columns that are checked
        dataset: null,                                              // loaded dataset
        dataView: new Slick.Data.DataView({inlineFilters: false}),  // grid view that hold the dataset data
        visibility: false,                                          // visibility flag
        step: null,                                                   // lookup step
        isUpdatingLookupStep: false
    };

    /**
     * @ngdoc service
     * @name data-prep.services.state.service:GridStateService
     * @description Lookup state service.
     */
    function LookupStateService() {
        return {
            reset: reset,
            setActions: setActions,
            setData: setData,
            setDataset: setDataset,
            setSelectedColumn: setSelectedColumn,
            setVisibility: setVisibility,
            updateColumnsToAdd: updateColumnsToAdd,
            setLookupStep: setLookupStep,
            setUpdateMode: setUpdateMode,
            setAddMode:setAddMode,
            setUpdatingLookupStep: setUpdatingLookupStep
        };

        /**
         * @ngdoc method
         * @name setVisibility
         * @methodOf data-prep.services.state.service:LookupStateService
         * @description Set the lookup visibility
         */
        function setVisibility(visibility) {
            lookupState.visibility = visibility;
        }

        /**
         * @ngdoc method
         * @name setData
         * @methodOf data-prep.services.state.service:LookupStateService
         * @param {object} data The data
         * @description Set new data in the grid and reset the column checkboxes
         */
        function setData(data) {
            lookupState.dataView.beginUpdate();
            lookupState.dataView.setItems(data.records, 'tdpId');
            lookupState.dataView.endUpdate();

            lookupState.selectedColumn = data.metadata.columns[0];
            lookupState.columnsToAdd = [];
            lookupState.columnCheckboxes = _.map(data.metadata.columns, function(col) {
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
         * @methodOf data-prep.services.state.service:LookupStateService
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
         * @methodOf data-prep.services.state.service:LookupStateService
         * @param {Array} actions The lookup actions (1 per possible dataset)
         * @description Sets the actions
         */
        function setActions(actions) {
            lookupState.actions = actions;
        }

        /**
         * @ngdoc method
         * @name setLookupStep
         * @methodOf data-prep.services.state.service:LookupStateService
         * @param {String} stepId a lookup step
         * @description Sets a lookup step
         */
        function setLookupStep(step) {
            lookupState.step = step;
        }

        /**
         * @ngdoc method
         * @name setUpdatingLookupStep
         * @methodOf data-prep.services.state.service:LookupStateService
         * @param {boolean} bool flag to check if in update mode
         * @description set flag to check if in update mode
         */
        function setUpdatingLookupStep(bool) {
            lookupState.isUpdatingLookupStep = bool;
        }


        /**
         * @ngdoc method
         * @name setUpdateMode
         * @methodOf data-prep.services.state.service:LookupStateService
         * @description Set parameters for update mode
         */
        function setUpdateMode() {
            lookupState.columnsToAdd = [];
            lookupState.columnCheckboxes = [];
            lookupState.dataset = null;
            lookupState.selectedColumn = null;
            lookupState.step = null;
            lookupState.isUpdatingLookupStep = true;
        }


        /**
         * @ngdoc method
         * @name setAddMode
         * @methodOf data-prep.services.state.service:LookupStateService
         * @description Set parameters for add mode
         */
        function setAddMode() {
            lookupState.columnsToAdd = [];
            lookupState.columnCheckboxes = [];
            lookupState.dataset = null;
            lookupState.selectedColumn = null;
            lookupState.step = null;
            lookupState.isUpdatingLookupStep = false;
            lookupState.visibility = false;
        }

        /**
         * @ngdoc method
         * @name setDataset
         * @methodOf data-prep.services.state.service:LookupStateService
         * @param {object} dataset The dataset
         * @description Sets new dataset
         */
        function setDataset(dataset) {
            lookupState.dataset = dataset;
        }

        /**
         * @ngdoc method
         * @name setSelectedColumn
         * @methodOf data-prep.services.state.service:LookupStateService
         * @param {object} column The column metadata
         * @description Set the actual selected column and line
         */
        function setSelectedColumn(column) {
            lookupState.selectedColumn = column;
        }

        /**
         * @ngdoc method
         * @name reset
         * @methodOf data-prep.services.state.service:LookupStateService
         * @description Reset the grid internal values
         */
        function reset() {
            lookupState.actions = [];
            lookupState.columnsToAdd = [];
            lookupState.columnCheckboxes = [];
            lookupState.dataset = null;
            lookupState.selectedColumn = null;
            lookupState.visibility = false;
            lookupState.step = null;
            lookupState.isUpdatingLookupStep = false;
        }
    }

    angular.module('data-prep.services.state')
        .service('LookupStateService', LookupStateService)
        .constant('lookupState', lookupState);
})();