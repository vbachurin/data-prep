(function() {
	'use strict';

	var lookupGridState = {
		dataView: new Slick.Data.DataView({inlineFilters: false}),
		addedToLookup : [],
		lookupColumnsToAdd : [],
		datasets: []
	};

	/**
	 * @ngdoc service
	 * @name data-prep.services.state.service:GridStateService
	 * @description Grid state service. Manage the lookup grid part state
	 */
	function LookupGridStateService() {
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
		function setLookupColumnsToAdd(){
			function getRidIsAddedFlag(obj){
				return _.omit(obj, 'isAdded');
			}
			lookupGridState.lookupColumnsToAdd = _.chain(lookupGridState.addedToLookup)
														.filter(function(col) {
															return col.isAdded === true;
														})
														.filter(function(col) {
															return col.id !== lookupGridState.selectedColumn.id;
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
			lookupGridState.columnFocus = columnId;
		}

		/**
		 * @ngdoc method
		 * @name setPotentialDatasets
		 * @methodOf data-prep.services.state.service:GridStateService
		 * @param {Array} datasets the datasets with which a lookup is possible
		 * @description sets the datasets with which a lookup is possible
		 */
		function setPotentialDatasets(datasets) {
			lookupGridState.datasets = datasets;
		}

		/**
		 * @ngdoc method
		 * @name setData
		 * @methodOf data-prep.services.state.service:GridStateService
		 * @param {object} data The data
		 * @description Set new data in the grid and resets the isAdded label to false for the new columns
		 */
		function setData(data) {
			lookupGridState.dataView.beginUpdate();
			lookupGridState.dataView.setItems(data.records, 'tdpId');
			lookupGridState.dataView.endUpdate();

			updateSelectedColumn(data);
			lookupGridState.addedToLookup = [];
			_.each(data.columns, function(col){
				lookupGridState.addedToLookup.push({
					isAdded : false,
					name : col.name,
					id: col.id
				});
			});
			setLookupColumnsToAdd();
		}

		function setDataset(dataset) {
			lookupGridState.dataset = dataset;
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
			if(lookupGridState.selectedColumn && data.columns) {
				lookupGridState.selectedColumn = _.find(data.columns, {id: lookupGridState.selectedColumn.id}) || data.columns[0];
			}
			//the first column is selected by default
			else {
				lookupGridState.selectedColumn = data.columns[0];
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
			lookupGridState.selectedColumn = column;
			if(column){
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
			lookupGridState.columnFocus = null;
			lookupGridState.selectedColumn = null;
			lookupGridState.selectedLine = null;
			lookupGridState.lookupColumnsToAdd = [];
			lookupGridState.addedToLookup = [];
			lookupGridState.dataset = null;
			lookupGridState.datasets = [];
		}
	}

	angular.module('data-prep.services.state')
		.service('LookupGridStateService', LookupGridStateService)
		.constant('lookupGridState', lookupGridState);
})();