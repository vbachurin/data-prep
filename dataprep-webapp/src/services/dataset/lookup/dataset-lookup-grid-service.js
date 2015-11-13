(function() {
	'use strict';

	/**
	 * @ngdoc service
	 * @name data-prep.services.state.service:GridStateService
	 * @description Grid state service. Manage the grid part state
	 */
	function GridLookupService() {
		var service =  {
			reset: reset,

			setColumnFocus: setColumnFocus,
			setData: setData,
			setFilter: setFilter,
			setGridSelection: setGridSelection,
			setDataset: setDataset
		};

		service.lookupGrid = {dataView: new Slick.Data.DataView({inlineFilters: false})};

		return service;

		/**
		 * @ngdoc method
		 * @name updateLinesCount
		 * @methodOf data-prep.services.state.service:GridStateService
		 * @param {object} data The grid data
		 * @description Update the number of lines statistics
		 */
		function updateLinesCount(data) {
			service.lookupGrid.nbLines = service.lookupGrid.dataView.getLength();
			service.lookupGrid.nbTotalLines = data.records.length;
			service.lookupGrid.displayLinesPercentage = (service.lookupGrid.nbLines * 100 / service.lookupGrid.nbTotalLines).toFixed(0);
		}

		/**
		 * @ngdoc method
		 * @name setFilter
		 * @methodOf data-prep.services.state.service:service.lookupGridService
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

			service.lookupGrid.dataView.beginUpdate();
			service.lookupGrid.dataView.setFilterArgs({filters: _.map(filters, 'filterFn')});
			service.lookupGrid.dataView.setFilter(allFilterFn);
			service.lookupGrid.dataView.endUpdate();

			updateLinesCount(data);
		}

		/**
		 * @ngdoc method
		 * @name setColumnFocus
		 * @methodOf data-prep.services.state.service:service.lookupGridService
		 * @param {string} columnId The column id to focus on
		 * @description Set the column id to focus on
		 */
		function setColumnFocus(columnId) {
			service.lookupGrid.columnFocus = columnId;
		}

		/**
		 * @ngdoc method
		 * @name setData
		 * @methodOf data-prep.services.state.service:service.lookupGridService
		 * @param {object} data The data
		 * @description Set new data in the grid
		 */
		function setData(data) {
			service.lookupGrid.dataView.beginUpdate();
			service.lookupGrid.dataView.setItems(data.records, 'tdpId');
			service.lookupGrid.dataView.endUpdate();

			updateLinesCount(data);
			updateSelectedColumn(data);
		}

		function setDataset(metadata) {
			service.dataset = metadata;
		}

		/**
		 * @ngdoc method
		 * @name updateSelectedColumn
		 * @methodOf data-prep.services.state.service:service.lookupGridService
		 * @param {object} data The data
		 * @description Determine the selected column from the new data
		 */
		function updateSelectedColumn(data) {
			//in preview we do not change anything
			if(data.preview) {
				return;
			}

			//if there is already a selected column, we update the column metadata to reference one of the new columns
			if(service.lookupGrid.selectedColumn && data.columns) {
				service.lookupGrid.selectedColumn = _.find(data.columns, {id: service.lookupGrid.selectedColumn.id}) || data.columns[0];
			}
			//the first column is selected by default
			else {
				service.lookupGrid.selectedColumn = data.columns[0];
			}
		}

		/**
		 * @ngdoc method
		 * @name setGridSelection
		 * @methodOf data-prep.services.state.service:service.lookupGridService
		 * @param {object} column The column metadata
		 * @param {number} line The line number
		 * @description Set the actual selected column and line
		 */
		function setGridSelection(column, line) {
			service.lookupGrid.selectedColumn = column;
			service.lookupGrid.selectedLine = line;
		}

		/**
		 * @ngdoc method
		 * @name reset
		 * @methodOf data-prep.services.state.service:service.lookupGridService
		 * @description Reset the grid internal values
		 */
		function reset() {
			service.lookupGrid.columnFocus = null;
			service.lookupGrid.selectedColumn = null;
			service.lookupGrid.selectedLine = null;
		}
	}

	angular.module('data-prep.services.dataset')
		.service('GridLookupService', GridLookupService);
})();