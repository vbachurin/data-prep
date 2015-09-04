(function () {
    'use strict';
    /**
     * @ngdoc service
     * @name data-prep.services.statistics.service:StatisticsService
     * @description Extracts/structures the data to be visualized in charts
     * @requires data-prep.services.playground.service:DatagridService
     * @requires data-prep.services.filter.service:FilterService
     * @requires data-prep.services.utils.service:ConverterService
     * @requires data-prep.services.utils.service:TextFormatService
     * @requires data-prep.services.statistics.service:StatisticsRestService
     */
    function StatisticsService($q, $timeout, $filter, $cacheFactory, DatagridService, FilterService, ConverterService, TextFormatService, StatisticsRestService) {

        var aggregationCache = $cacheFactory('aggregationStatistics', {capacity: 5});

        var service = {
            boxPlot: null,
            histogram: null,
            stateDistribution: null,
            statistics: null,

            //filters
            addFilter: addFilter,
            addRangeFilter: addRangeFilter,

            //statistics entry points
            processData: processData,
            processAggregation: processAggregation,
            getAggregationColumns: getAggregationColumns,
            resetCharts: resetCharts,

            //TODO temporary method to be replaced with new geo chart
            getGeoDistribution: getGeoDistribution

        };

        return service;

        //
        // BELOW ARE ALL THE STATISTICS TABS FUNCTIONS FOR (1-VIZ, 2-VALUES, 3-PATTERN, 4-OTHERS)
        //

        //--------------------------------------------------------------------------------------------------------------
        //-----------------------------------------------1- Visualization-----------------------------------------------
        //--------------------------------------------------------------------------------------------------------------
        /**
         * TEMPORARY : Calculate column value distribution
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
            keyName = keyName || 'colValue';
            valueName = valueName || 'frequency';

            var records = DatagridService.data.records;

            return _.chain(records)
                .groupBy(function (item) {
                    return item[columnId];
                })
                .map(function (val, index) {
                    var item = {};
                    item[keyName] = keyTransformer ? keyTransformer(index) : index;
                    item[valueName] = val.length;
                    return item;
                })
                .sortBy(valueName)
                .reverse()
                .value();
        }

        /**
         * TEMPORARY : Calculate geo distribution, and targeted map
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

        /**
         * @ngdoc method
         * @name initRangeHistogram
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @param {Array} histoData Array of data
         * @description Adapt the numeric range data to fit histogram format
         */
        function initRangeHistogram(histoData) {
            var rangeData = _.map(histoData, function (histDatum) {
                return {
                    'data': histDatum.range.min + ' ... ' + histDatum.range.max,
                    'occurrences': histDatum.occurrences
                };
            });

            initClassicHistogram('occurrences', 'Occurrences', rangeData);
        }

        /**
         * @ngdoc method
         * @name initClassicHistogram
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @param {string} key The value key
         * @param {string} label The value label
         * @param {Array} dataTable The table to display
         * @description Set the frequency table that fit the histogram format
         */
        function initClassicHistogram(key, label, dataTable) {
            service.histogram = {
                data: _.map(dataTable, function (rec) {
                    rec.formattedValue = TextFormatService.computeHTMLForLeadingOrTrailingHiddenChars(rec.data);
                    return rec;
                }),
                key: key,
                label: label,
                column: service.selectedColumn
            };
        }

        /**
         * @ngdoc method
         * @name initRangeLimits
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @description Set the range slider limits
         */
        function initRangeLimits() {
            var column = service.selectedColumn;
            var currentRangeFilter = _.find(FilterService.filters, function(filter){
                return filter.colId === column.id && filter.type === 'inside_range';
            });

            if(currentRangeFilter){
                if(currentRangeFilter.args.interval[0] < column.statistics.min){
                    var maxBrush;
                    if(currentRangeFilter.args.interval[1] <= column.statistics.min){
                        maxBrush = column.statistics.min;
                    }
                    else if(currentRangeFilter.args.interval[1] >= column.statistics.max){
                        maxBrush = column.statistics.max;
                    }
                    else if(currentRangeFilter.args.interval[1] < column.statistics.max && currentRangeFilter.args.interval[1] > column.statistics.min){
                        maxBrush = currentRangeFilter.args.interval[1];
                    }
                    service.rangeLimits = {
                        min : column.statistics.min,
                        max : column.statistics.max,
                        minBrush : column.statistics.min,
                        maxBrush : maxBrush,
                        minFilterVal : currentRangeFilter.args.interval[0],
                        maxFilterVal : currentRangeFilter.args.interval[1]
                    };
                }
                else if(currentRangeFilter.args.interval[1] > column.statistics.max){

                    var minBrush;
                    if(currentRangeFilter.args.interval[0] >= column.statistics.max){
                        minBrush = column.statistics.max;
                    }
                    else if(currentRangeFilter.args.interval[0] <= column.statistics.max && currentRangeFilter.args.interval[0] >= column.statistics.min){
                        minBrush = currentRangeFilter.args.interval[0];
                    }

                    service.rangeLimits = {
                        min : column.statistics.min,
                        max : column.statistics.max,
                        minBrush : minBrush,
                        maxBrush : column.statistics.max,
                        minFilterVal : currentRangeFilter.args.interval[0],
                        maxFilterVal : currentRangeFilter.args.interval[1]
                    };
                }
                else {
                    service.rangeLimits = {
                        min : column.statistics.min,
                        max : column.statistics.max,
                        minBrush : currentRangeFilter.args.interval[0],
                        maxBrush : currentRangeFilter.args.interval[1]
                    };
                }
            }
            else {
                service.rangeLimits = {
                    min : column.statistics.min,
                    max : column.statistics.max,
                    minBrush : undefined,
                    maxBrush : undefined
                };
            }

        }

        /**
         * @ngdoc method
         * @name getAggregationData
         * @methodOf data-prep.services.statistics.service:StatisticsCacheService
         * @param {object} aggregationParameters The aggregation parameters
         * @description Get aggregations from cache if present, from REST call otherwise.
         */
        function getAggregationData(aggregationParameters) {
            var cacheKey = JSON.stringify(aggregationParameters);
            var aggregationData = aggregationCache.get(cacheKey);

            if(aggregationData) {
                return $q.when(aggregationData);
            }

            return StatisticsRestService.getAggregations(aggregationParameters)
                .then(function(response) {
                    aggregationCache.put(cacheKey, response);
                    return response;
                });
        }

        //--------------------------------------------------------------------------------------------------------------
        //-------------------------------------------------2- Values----------------------------------------------------
        //--------------------------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name clean
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @param {number} value Value to clean the float
         * @description Cleans the value to have 2 decimals (5.2568845842587425588 -> 5.25)
         * @returns {number} The value in the clean format
         */
        function clean(value) {
            return value === parseInt(value, 10) ? value : +value.toFixed(2);
        }

        /**
         * @ngdoc method
         * @name initStatisticsValues
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @param {object} column The target column
         * @description Initialize the statistics to display in the values TAB of the stats part
         */
        function initStatisticsValues(column) {
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
                    specificStats.AVG_LENGTH = clean(stats.textLengthSummary.averageLengthIgnoreBlank);
                    specificStats.AVG_LENGTH_WITH_BLANK = clean(stats.textLengthSummary.averageLength);
                    specificStats.MIN_LENGTH = stats.textLengthSummary.minimalLengthIgnoreBlank;
                    specificStats.MIN_LENGTH_WITH_BLANK = stats.textLengthSummary.minimalLength;
                    specificStats.MAX_LENGTH = stats.textLengthSummary.maximalLength;
                    break;
            }

            service.statistics = {
                common: commonStats,
                specific: specificStats
            };
        }

        //--------------------------------------------------------------------------------------------------------------
        //-------------------------------------------------3- Pattern---------------------------------------------------
        //--------------------------------------------------------------------------------------------------------------

        //Currently there are no stats to be brought on the pattern data

        //--------------------------------------------------------------------------------------------------------------
        //-------------------------------------------------4- Others----------------------------------------------------
        //--------------------------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name updateBoxplotData
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @description Gathers the boxPlot data from the specific stats of the columns having a 'number' type
         */
        function updateBoxplotData() {
            var specStats = service.statistics.specific;

            //waiting for DQ to process negative values
            if (specStats.LOWER_QUANTILE) {
                service.boxPlot = {
                    min: specStats.MIN,
                    max: specStats.MAX,
                    q1: specStats.LOWER_QUANTILE,
                    q2: specStats.UPPER_QUANTILE,
                    median: specStats.MEDIAN,
                    mean: specStats.MEAN,
                    variance: specStats.VARIANCE
                };
            }
            else {
                service.boxPlot = null;
            }
        }

        //--------------------------------------------------------------------------------------------------------------
        //-------------------------------------------------FILTER-------------------------------------------------------
        //--------------------------------------------------------------------------------------------------------------

        /**
         * @ngdoc method
         * @name addRangeFilter
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @param {Array} interval of the filter
         * @description Adds a rangefilter in the angular context
         */
        function addRangeFilter(interval) {
            var removeFilterFn = function removeFilterFn(filter) {
                if (service.selectedColumn && filter.colId === service.selectedColumn.id) {
                    initRangeLimits();
                }
            };

            service.rangeLimits.minBrush = interval[0];
            service.rangeLimits.maxBrush = interval[1];
            //empty the minFilterVal and the maxFilterVal because otherwise it will show them once we get back to the VIZ tab
            service.rangeLimits.minFilterVal = undefined;
            service.rangeLimits.maxFilterVal = undefined;

            var column = service.selectedColumn;
            var filterFn = FilterService.addFilter.bind(null, 'inside_range', column.id, column.name, {interval: interval}, removeFilterFn);
            $timeout(filterFn);
        }

        //--------------------------------------------------------------------------------------------------------------
        //-------------------------------------------------COMMON-------------------------------------------------------
        //--------------------------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name processMapData
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @param {object} column The column to visualize
         * @description Remove the previous charts data and set the map chart
         */
        function processMapData(column) {
            service.stateDistribution = column;
        }

        /**
         * @ngdoc method
         * @name processNonMapData
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @param {object} column The column to visualize
         * @description Reset the map chart and calculate the needed data for visualization
         */
        function processNonMapData(column) {
            switch (ConverterService.simplifyType(column.type)) {
                case 'number':
                    initRangeHistogram(column.statistics.histogram);
                    updateBoxplotData();
                    initRangeLimits();
                    break;
                case 'text':
                case 'boolean':
                    initClassicHistogram('occurrences', 'Occurrences', column.statistics.frequencyTable);
                    break;
                default :
                    console.log('nor a number neither a boolean neither a string');
            }
        }

        /**
         * @ngdoc method
         * @name processData
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @param {object} column The column to visualize
         * @description Processes the statistics data for visualization on the provided column
         */
        function processData(column) {
            service.selectedColumn = column;
            resetCharsWithoutCache();
            initStatisticsValues(column);

            //TODO replace with new geo chart
            if (column.domain.indexOf('STATE_CODE') !== -1) {
                processMapData(column);
            }
            //TODO Coming soon after the integration of the globe map : reset charts and init localization chart data
            // else if (column.domain === 'LOCALIZATION') {
            //    processLocalizationMapData(column);
            //}
            else {
                processNonMapData(column);
            }
        }

        /**
         * @ngdoc method
         * @name processAggregation
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @param {string} datasetId The column to visualize
         * @param {string} preparationId The column to visualize
         * @param {string} stepId The column to visualize
         * @param {number} sampleSize The sample size
         * @param {object} column The column to visualize
         * @param {object} aggregation The column to visualize
         * @description Processes the statistics aggregation for visualization
         */
        function processAggregation(datasetId, preparationId, stepId, sampleSize, column, aggregation) {
            if(!aggregation) {
                return processData(service.selectedColumn);
            }

            service.boxPlot = null;
            service.histogram = null;
            service.stateDistribution = null;

            var aggregationParameters = {
                datasetId: datasetId,
                preparationId: preparationId,
                stepId: stepId,
                sampleSize: sampleSize,
                operations: [{
                    operator: aggregation,
                    columnId: column.id
                }],
                groupBy: [service.selectedColumn.id]
            };

            getAggregationData(aggregationParameters)
                .then(function(response) {
                    initClassicHistogram(aggregation, $filter('translate')(aggregation), response);
                    service.histogram.aggregationColumn = column;
                    service.histogram.aggregation = aggregation;
                });
        }

        //--------------------------------------------------------------------------------------------------------------
        //-------------------------------------------------UTILS--------------------------------------------------------
        //--------------------------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name addFilter
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @param {string} value The phrase to filter
         * @description Add a filter in the angular context
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
         * @name resetCharts
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @description Removes all the data to disable all visualization (including cache)
         */
        function resetCharts() {
            //charts data
            resetCharsWithoutCache();

            //caches
            aggregationCache.removeAll();
        }

        /**
         * @ngdoc method
         * @name resetCharts
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @description Removes all the data to disable all visualization
         */
        function resetCharsWithoutCache() {
            service.boxPlot = null;
            service.histogram = null;
            service.rangeLimits = null;
            service.stateDistribution = null;
            service.statistics = null;
        }

        /**
         * @ngdoc method
         * @name resetCharts
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @description Removes all the data to disable all visualization
         */
        function getAggregationColumns() {
            var column = service.selectedColumn;
            //TODO JSO : put a cache again that is invalidated when one of the columns change
            return DatagridService.getNumericColumns(column);
        }
    }

    angular.module('data-prep.services.statistics')
        .service('StatisticsService', StatisticsService);
})();