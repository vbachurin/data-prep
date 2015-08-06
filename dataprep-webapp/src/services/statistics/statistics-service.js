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
    function StatisticsService(DatagridService, FilterService, ConverterService, StatisticsAggregationRestService, $timeout) {
        var service = {
            selectedColumn: null,
            data: null,
            stateDistribution: null,

            addFilter: addFilter,
            extractNumericData: extractNumericData,
            processVisuData: processVisuData,
            processVisuDataAggregation: processVisuDataAggregation,
            resetCharts: resetCharts,
            getGeoDistribution: getGeoDistribution,
            getAggregations: getAggregations
        };

        return service;

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
                FilterService.addFilter.bind(null, 'contains', column.id, column.name, {phrase: value}) :
                FilterService.addFilter.bind(null, 'empty_records', column.id, column.name, {});

            $timeout(filterFn);
        }

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
         * @ngdoc method
         * @name processBarchartData
         * @methodOf data-prep.services.statistics:StatisticsService
         * @param {object} column The selected column
         * @description shows/hides the visualization according to the clicked column type
         */
        function processBarchartData(column) {
            var data = null;
            if(ConverterService.simplifyType(column.type) === 'number') {
                data = extractNumericData(column.statistics.histogram);
            }
            else if (column.type === 'string') {
                data = column.statistics.frequencyTable;
            }
            else if (column.type === 'boolean') {
                data = column.statistics.frequencyTable;
            }
            else {
                console.log('nor a number neither a boolean neither a string');
            }

            service.stateDistribution = null; //hide the map if the previous column was a state
            service.selectedColumn = column;
            service.data = data;
        }

        /**
         * @ngdoc method
         * @name processVisuData
         * @methodOf data-prep.services.statistics:StatisticsService
         * @param {object} column The selected column
         * @description processes the visualization data according to the clicked column domain
         */
        function processVisuData(column) {
            if (column.domain.indexOf('STATE_CODE_') !== -1) {
                processMapData(column);
            } else if (column.domain === 'LOCALIZATION') {
                resetCharts();
            } else {
                processBarchartData(column);
            }
        }

        /**
         * @ngdoc method
         * @name processVisuData
         * @methodOf data-prep.services.statistics:StatisticsService
         * @param {object} currentColumn The selected column
         * @param {object} targetColumn The aggregation target column
         * @param {object} calculation The selected calculation
         * @description processes the visualization data according to the clicked column domain
         */
        function processVisuDataAggregation(currentColumn, targetColumn, calculation) {

            StatisticsCacheService.getAggregations(currentColumn, targetColumn, calculation)
                .then(function(column) {
                    //processes the visualization data
                    processVisuData(column);
                })
                .catch(function() {

                })
                .finally(function() {

                });
        }

        /**
         * @ngdoc method
         * @name getAggregations
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @param {object} stringifiedColumn The aggregation target column as string
         * @description Get aggregation from REST call, clean and adapt them
         */
        function getAggregations(stringifiedColumn) {
            return StatisticsAggregationRestService.getAggregations(stringifiedColumn)
                .then(function(response) {
                    //var menus = cleanParamsAndItems(response.data);
                    //return adaptInputTypes(menus);
                    return response.data;
                });
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
    }

    angular.module('data-prep.services.statistics')
        .service('StatisticsService', StatisticsService);
})();