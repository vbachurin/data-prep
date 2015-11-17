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
			setFilter: setFilter,
			setGridSelection: setGridSelection,
			setDataset: setDataset,
			setLookupColumnsToAdd: setLookupColumnsToAdd
		};

		function setLookupColumnsToAdd(colsIds){
			lookupGridState.lookupColumnsToAdd = colsIds;
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
		 * @name setFilter
		 * @methodOf data-prep.services.state.service:GridStateService
		 * @param {array} filters The filters
		 * @param {object} data The grid data
		 * @description Update the grid filters
		 */
		function setFilter(filters, data) {
			/**
			 * @ngdoc method
			 * @name allFilterFn
			 * @param {object} item The item to test
			 * @param {object} args Object containing the filters predicates
			 * @description Filter function. It iterates over all filters and return if the provided item fit the predicates
			 * @returns {boolean} True if the item pass all the filters
			 */
			var allFilterFn = function allFilterFn(item, args) {
				//init filters with actual data
				var initializedFilters = _.map(args.filters, function (filter) {
					return filter(data);
				});
				//execute each filter on the value
				for (var i = 0; i < initializedFilters.length; i++) {
					var filter = initializedFilters[i];
					if (!filter(item)) {
						return false;
					}
				}
				return true;
			};

			lookupGridState.dataView.beginUpdate();
			lookupGridState.dataView.setFilterArgs({filters: _.map(filters, 'filterFn')});
			lookupGridState.dataView.setFilter(allFilterFn);
			lookupGridState.dataView.endUpdate();

			updateLinesCount(data);
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

			lookupGridState.addedToLookup = {};
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
		}
	}

	angular.module('data-prep.services.state')
		.service('LookupGridStateService', LookupGridStateService)
		.constant('lookupGridState', lookupGridState);
})();