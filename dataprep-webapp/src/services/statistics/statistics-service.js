(function () {
	'use strict';

	function StatisticsService (DatagridService, FilterService, $timeout) {
		var self = this;

		/**
		 * Add a 'contains' filter in the angular context
		 * @param value - the phrase
		 */
		self.addFilter = function (value) {
			$timeout(FilterService.addFilter.bind(null,'contains',self.selectedColumn.id,self.selectedColumn.name,{phrase: 			value}));
		};

		/**
		 * removes the previous barchart and sets the map chart
		 * @param column
		 */
		self.processMapData = function (column) {
			self.selectedColumn = column;
			//remove the barchart
			self.data = null;
			//show the map
			self.stateDistribution = column;
		};

		/**
		 * extracts and builds the data for numeric column, from the histogram of the statistics
		 * @param column.statistics.histogram
		 */
		self.extractNumericData = function(histoData){
			var concatData         = [];
			_.each(histoData, function (histDatum) {
				concatData.push({
					'data': histDatum.range.min + ' ... ' + histDatum.range.max,
					'occurrences': histDatum.occurrences
				});
			});
			return concatData;
		};

		/**
		 * calculates the barchart data according to the column types
		 * @param column
		 * @returns [{"data":" 0-> 10", "occurences":11}, {"data":" 10 -> 20", "occurences":11}, ...]
		 */
		self.processBarchartData = function (column) {
			self.selectedColumn = column;
			//hide the map if the previous column was a state
			self.stateDistribution = null;
			if (column.type === 'numeric' || column.type === 'integer' || column.type === 'float' || column.type === 'double') {
				self.data = self.extractNumericData(column.statistics.histogram);
			} else if (column.type === 'string') {
				self.data         = column.statistics.frequencyTable;
			} else if (column.type === 'boolean') {
				self.data            = column.statistics.frequencyTable;
			} else {
				self.data            = null;
				console.log('nor a number neither a boolean neither a string');
			}
		};

		/**
		 * checks what to draw a barchart or a map according to the column.domain field
		 * @param column
		 */
		self.processVisuData = function (column) {
			if (column.domain.indexOf('STATE_CODE_') !== -1) {
				self.processMapData(column);
			} else if(column.domain === 'LOCALIZATION'){
				self.data            = null;
				self.stateDistribution = null;
			} else {
				self.processBarchartData(column);
			}
		};

		/**
		 * empties the map chart/barchart, called on a new opened dataset or preparation
		 */
		self.resetCharts = function(){
			self.data = null;
			self.stateDistribution = null;
		};

		//------------------------------------------------------------------------------------------------------
		//-----------------------------------------------DISTRIBUTIONS------------------------------------------
		//------------------------------------------------------------------------------------------------------
		/**
		 * Calculate column value distribution
		 * @param columnId
		 * @param keyName - distribution key name (default : 'colValue');
		 * @param valueName - distribution value name (default : 'frequency')
		 * @param keyTransformer - transformer applied to the distribution key
		 * @returns [{colValue: string, frequency: integer}} - colValue (or specified) : the grouped value, frequency
		 *     (or specified) : the nb of time the value appears
		 */
		self.getDistribution = function (columnId, keyName, valueName, keyTransformer) {
			keyName   = keyName || 'colValue';
			valueName = valueName || 'frequency';

			var records = DatagridService.data.records;

			var result = _.chain(records)
				.groupBy(function (item) {
					return item[columnId];
				})
				.map(function (val, index) {
					var item        = {};
					item[keyName]   = keyTransformer ? keyTransformer(index) : index;
					item[valueName] = val.length;
					return item;
				})
				.sortBy(valueName)
				.reverse()
				.value();

			return result;
		};

		/**
		 * Calculate geo distribution, and targeted map
		 * @param column
		 * @returns {{map: string, data: [{}]}}
		 */
		self.getGeoDistribution = function (column) {
			var keyPrefix = 'us-';
			var map       = 'countries/us/us-all';

			return {
				map: map,
				data: self.getDistribution(column.id, 'hc-key', 'value', function (key) {return keyPrefix + key.toLowerCase();})
			};
		};
	}

	angular.module('data-prep.services.statistics')
		.service('StatisticsService', StatisticsService);
})();