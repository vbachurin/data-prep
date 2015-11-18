(function() {
	'use strict';

	var lookupGridState = {
		dataView: new Slick.Data.DataView({inlineFilters: false}),
		addedToLookup : {},
		lookupColumnsToAdd : []
	};

	/**
	 * @ngdoc service
	 * @name data-prep.services.state.service:GridStateService
	 * @description Grid state service. Manage the grid part state
	 */
	function LookupGridStateService() {
		return {
			reset: reset,

			setColumnFocus: setColumnFocus,
			setData: setData,
			setGridSelection: setGridSelection,
			setDataset: setDataset,
			setLookupColumnsToAdd: setLookupColumnsToAdd
		};

		function setLookupColumnsToAdd(){
			var columnsToAdd = _.keys(_.pick(lookupGridState.addedToLookup, function(col){
				return col.isAdded;
			}));
			lookupGridState.lookupColumnsToAdd = _.without(columnsToAdd, lookupGridState.selectedColumn.id);
		}

		/**
		 * @ngdoc method
		 * @name updateLinesCount
		 * @methodOf data-prep.services.state.service:GridStateService
		 * @param {object} data The grid data
		 * @description Update the number of lines statistics
		 */
		function updateLinesCount(data) {
			lookupGridState.nbLines = lookupGridState.dataView.getLength();
			lookupGridState.nbTotalLines = data.records.length;
			lookupGridState.displayLinesPercentage = (lookupGridState.nbLines * 100 / lookupGridState.nbTotalLines).toFixed(0);
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
		 * @name setData
		 * @methodOf data-prep.services.state.service:GridStateService
		 * @param {object} data The data
		 * @description Set new data in the grid
		 */
		function setData(data) {
			lookupGridState.dataView.beginUpdate();
			lookupGridState.dataView.setItems(data.records, 'tdpId');
			lookupGridState.dataView.endUpdate();

			updateLinesCount(data);
			updateSelectedColumn(data);

			_.each(data.columns, function(col){
				lookupGridState.addedToLookup[col.id]  = {
					isAdded : false
				};
			});
		}

		function setDataset(metadata) {
			lookupGridState.dataset = metadata;
		}

		/**
		 * @ngdoc method
		 * @name updateSelectedColumn
		 * @methodOf data-prep.services.state.service:GridStateService
		 * @param {object} data The data
		 * @description Determine the selected column from the new data
		 */
		function updateSelectedColumn(data) {
			//in preview we do not change anything
			if(data.preview) {
				return;
			}

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
		 * @param {number} line The line number
		 * @description Set the actual selected column and line
		 */
		function setGridSelection(column, line) {
			lookupGridState.selectedColumn = column;
			lookupGridState.selectedLine = line;
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
			lookupGridState.addedToLookup = {};
		}
	}

	angular.module('data-prep.services.state')
		.service('LookupGridStateService', LookupGridStateService)
		.constant('lookupGridState', lookupGridState);
})();