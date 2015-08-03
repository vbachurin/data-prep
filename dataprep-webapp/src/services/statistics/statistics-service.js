(function () {
    'use strict';
    /**
     * @ngdoc service
     * @name data-prep.services.statistics:StatisticsService
     * @description Extracts/structures the data to be visualized in charts
     * @requires data-prep.services.playground.service:DatagridService
     * @requires data-prep.services.filter.service:FilterService
     * @requires data-prep.services.utils.service:ConverterService
     */
    function StatisticsService(DatagridService, FilterService, ConverterService, $timeout) {
        var service = {
            selectedColumn: null,
            data: null,
            stateDistribution: null,
            boxplotData:null,
            addFilter: addFilter,
            extractNumericData: extractNumericData,
            processVisuData: processVisuData,
            resetCharts: resetCharts,
            getGeoDistribution: getGeoDistribution
        };

        return service;

    /**
     * BELOW ARE ALL THE STATISTICS TABS FUNCTIONS (1-VIZ, 2-VALUES, 3-VATTERN, 4-OTHERS)
     */
    /******************** 1- Visualization ****************************/
        /**
         * @ngdoc method
         * @name processMapData
         * @methodOf data-prep.services.statistics:StatisticsService
         * @param {object} column The clicked column
         * @description removes the previous barchart and sets the map chart
         */
        function processMapData(column) {
            service.selectedColumn = column;
            //remove the barchart
            service.data = null;
            //remove the boxplot
            service.boxplotData = null;
            //show the map
            service.stateDistribution = column;
        }

        /**
         * @ngdoc method
         * @name extractNumericData
         * @methodOf data-prep.services.statistics:StatisticsService
         * @param {Array} histoData Array of data
         * @description extracts and builds the data for numeric column, from the histogram of the statistics
         * @returns {Array} Extracted data with the specified format {"data":" 0 ... 10", "occurences":11}, {"data":" 10 ... 20", "occurences":11}
         */
        function extractNumericData(histoData) {
            var concatData = [];
            _.each(histoData, function (histDatum) {
                concatData.push({
                    'data': histDatum.range.min + ' ... ' + histDatum.range.max,
                    'occurrences': histDatum.occurrences
                });
            });
            return concatData;
        }

        /**
         * Calculate column value distribution
         * @param columnId
         * @param keyName - distribution key name (default : 'colValue');
         * @param valueName - distribution value name (default : 'frequency')
         * @param keyTransformer - transformer applied to the distribution key
         * @returns {Array} Column distribution array {colValue: string, frequency: integer}}
         * <ul>
         *     <li>colValue (or specified) : the grouped value</li>
         *     <li>frequency (or specified) : the nb of time the value appears</li>
         * </ul>
         */
        function getDistribution(columnId, keyName, valueName, keyTransformer) {
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
        }
    /******************** 2- Value ************************************/
        /**
         * @ngdoc method
         * @name clean
         * @methodOf data-prep.services.statistics:StatisticsService
         * @param {number} value Value to clean the float
         * @description Cleans the value to have 2 decimal (5.2568845842587425588 -> 5.25)
         * @returns {number} The value in the clean format
         */
        function clean(value) {
            return value === parseInt(value, 10) ? value : +value.toFixed(2);
        }

        /**
         * @ngdoc method
         * @name initStatistics
         * @methodOf data-prep.services.statistics:StatisticsService
         * @param {object} column The target column
         * @description Initialize the statistics to display in the values TAB of the stats part
         */
        function initValuesStatistics(column) {
            if (!column.statistics) {
                return;
            }

            var stats = column.statistics;
            var colType = ConverterService.simplifyType(column.type);
            var commonStats = {
                COUNT: stats.count,
                DISTINCT_COUNT: stats.distinctCount,
                DUPLICATE_COUNT: stats.duplicateCount,

                VALID: stats.valid,
                EMPTY: stats.empty,
                INVALID: stats.invalid
            };

            var specificStats = {};
            switch (colType) {
                case 'number':
                    specificStats.MIN = clean(stats.min);
                    specificStats.MAX = clean(stats.max);
                    specificStats.MEAN = clean(stats.mean);
                    specificStats.VARIANCE = clean(stats.variance);
                    if (stats.quantiles.lowerQuantile !== 'NaN') {
                        specificStats.MEDIAN = clean(stats.quantiles.median);
                        specificStats.LOWER_QUANTILE = clean(stats.quantiles.lowerQuantile);
                        specificStats.UPPER_QUANTILE = clean(stats.quantiles.upperQuantile);
                    }

                    break;
                case 'text':
                    specificStats.AVG_LENGTH = clean(stats.textLengthSummary.averageLength);
                    specificStats.AVG_LENGTH_WITH_BLANK = clean(stats.textLengthSummary.averageLengthWithBlank);
                    specificStats.MIN_LENGTH = stats.textLengthSummary.minimalLength;
                    specificStats.MIN_LENGTH_WITH_BLANK = stats.textLengthSummary.minimalLengthWithBlank;
                    specificStats.MAX_LENGTH = stats.textLengthSummary.maximalLength;
                    break;
            }

            service.statistics = {
                common: commonStats,
                specific: specificStats
            };
        }


        /**
         * @ngdoc method
         * @name processNonMapData
         * @methodOf data-prep.services.statistics:StatisticsService
         * @param {object} column The selected column
         * @description shows/hides the visualization according to the clicked column type
         */
        function processNonMapData(column) {
            var data = null;
            if(ConverterService.simplifyType(column.type) === 'number') {
                data = extractNumericData(column.statistics.histogram);
                updateBoxplotData();
            }
            else if (column.type === 'string') {
                data = column.statistics.frequencyTable;
                service.boxplotData = null;
            }
            else if (column.type === 'boolean') {
                data = column.statistics.frequencyTable;
                service.boxplotData = null;
            }
            else {
                console.log('nor a number neither a boolean neither a string');
                service.boxplotData = null;
            }

            service.stateDistribution = null; //hide the map if the previous column was a state
            service.selectedColumn = column;
            service.data = data;
        }


        /**
         * Calculate geo distribution, and targeted map
         * @param {object} column The target column
         * @returns {object} Geo distribution {map: string, data: [{}]}
         */
        function getGeoDistribution(column) {
            var keyPrefix = 'us-';
            var map = 'countries/us/us-all';

            return {
                map: map,
                data: getDistribution(column.id, 'hc-key', 'value', function (key) {
                    return keyPrefix + key.toLowerCase();
                })
            };
        }
    /******************** 3- Pattern **********************************/
        //Currently there are no stats to be brought on the pattern data

    /******************** 4- Others ***********************************/
        /**
         * @ngdoc method
         * @name updateBoxplotData
         * @methodOf data-prep.services.statistics:StatisticsService
         * @param {}
         * @description gathers the boxplotData from the specific stats of the Column having a Number Type
         */
        function updateBoxplotData(){
            var boxplotData = null;
            var specStats = service.statistics.specific;
            //waiting for DQ to process negative values
            if(specStats.LOWER_QUANTILE){
                boxplotData = {
                    min:specStats.MIN,
                    max:specStats.MAX,
                    q1:specStats.LOWER_QUANTILE,
                    q2:specStats.UPPER_QUANTILE,
                    median:specStats.MEDIAN,
                    mean:specStats.MEAN,
                    variance:specStats.VARIANCE
                };
                service.boxplotData = boxplotData;
            }
            else {
                service.boxplotData = null;
            }
        }

    /**
     * Common functions betwwen the different tabs
     * */
        /**
         * @ngdoc method
         * @name addFilter
         * @methodOf data-prep.services.statistics:StatisticsService
         * @param {string} value The phrase to filter
         * @description Add a 'contains' filter in the angular context
         */
        function addFilter(value) {
            var column = service.selectedColumn;
            var filterFn = value ?
                FilterService.addFilter.bind(null, 'contains', column.id, column.name, {phrase: value}):
                FilterService.addFilter.bind(null, 'empty_records', column.id, column.name, {});

            $timeout(filterFn);
        }



        /**
         * @ngdoc method
         * @name processVisuData
         * @methodOf data-prep.services.statistics:StatisticsService
         * @param {object} column The selected column
         * @description processes the visualization data according to the clicked column domain
         */
        function processVisuData(column) {
            //THE WHOLE CONTENT OF THE 'VALUES' TAB IN THE STATS PART
            initValuesStatistics(column);

            if (column.domain.indexOf('STATE_CODE_') !== -1) {
                processMapData(column);
            } else if (column.domain === 'LOCALIZATION') {
                resetCharts();
            } else {
                processNonMapData(column);
            }
        }

        /**
         * @ngdoc method
         * @name resetCharts
         * @methodOf data-prep.services.statistics:StatisticsService
         * @description removes the map chart/barchart, called on a new opened dataset or preparation
         */
        function resetCharts() {
            service.data = null;
            service.stateDistribution = null;
            service.boxplotData = null;
        }
    }

    angular.module('data-prep.services.statistics')
        .service('StatisticsService', StatisticsService);
})();