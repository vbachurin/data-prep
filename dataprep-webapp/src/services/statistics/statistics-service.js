(function () {
    'use strict';
    /**
     * @ngdoc service
     * @name data-prep.services.statistics.service:StatisticsService
     * @description Extracts/structures the data to be visualized in charts
     * @requires data-prep.services.playground.service:DatagridService
     * @requires data-prep.services.recipe.service:RecipeService
     * @requires data-prep.services.statistics.service:StatisticsRestService
     * @requires data-prep.services.utils.service:ConverterService
     * @requires data-prep.services.utils.service:FilterAdapterService
     * @requires data-prep.services.utils.service:TextFormatService
     * @requires data-prep.services.utils.service:StorageService
     * @requires data-prep.services.utils.service:WorkerService
     */
    function StatisticsService($filter, state,
                               DatagridService, RecipeService, StatisticsRestService,
                               ConverterService, FilterAdapterService, TextFormatService, StorageService, WorkerService) {

        var dateFilteredWorkerWrapper;
        var datePatternWorkerWrapper;

        //workerFn[0-9] are preserved words that won't be mangled during uglification
        //those should be used to pass external functions, that are directly used here, to the web worker
        var workerFn0 = TextFormatService.convertJavaDateFormatToMomentDateFormat;
        var workerFn1 = TextFormatService.convertPatternToRegexp;

        var service = {
            boxPlot: null,
            histogram: null,
            filteredHistogram: null,
            stateDistribution: null,
            statistics: null,
            patterns: null,

            //update range
            initRangeLimits: initRangeLimits,

            //filters
            getRangeFilterRemoveFn: getRangeFilterRemoveFn,

            //statistics entry points
            processData: processData,                       // basic charts
            processAggregation: processAggregation,         // aggregation charts
            getAggregationColumns: getAggregationColumns,   // possible aggregation columns
            updateStatistics: updateStatistics,             // update stats + trigger chart
            reset: reset,                                   // reset charts/statistics/cache

            //TODO temporary method to be replaced with new geo chart
            getGeoDistribution: getGeoDistribution,

            //Pattern
            valueMatchPatternFn: valueMatchPatternFn
        };

        return service;

        //
        // BELOW ARE ALL THE STATISTICS TABS FUNCTIONS FOR (1-CHART, 2-VALUES, 3-PATTERN, 4-OTHERS)
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

            var records = state.playground.data.records;

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
            var keyPrefix = 'US-';
            var map = 'countries/us/us-all';

            return {
                map: map,
                data: getDistribution(column.id, 'hc-key', 'value', function (key) {
                    return keyPrefix + key;
                })
            };
        }

        /**
         * @ngdoc method
         * @name getRangeFilteredOccurrence
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @param {number} min The range min value
         * @param {number} max The range max value
         * @description Compute The filtered records that fullfill the given predicate
         * @returns {number} The Number of records
         */
        function getRangeFilteredOccurrence(min, max) {
            return _.chain(state.playground.grid.filteredOccurences)
                .keys()
                .filter(function (value) {
                    var numberValue = Number(value);
                    return !isNaN(numberValue) &&
                        ((numberValue === min) || (numberValue > min && numberValue < max));
                })
                .map(function (key) {
                    return state.playground.grid.filteredOccurences[key];
                })
                .reduce(function (accu, value) {
                    return accu + value;
                }, 0)
                .value();
        }

        /**
         * @ngdoc method
         * @name initRangeHistogram
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @param {Array} histoData Array of data
         * @description Adapt the numeric range data to fit histogram format
         */
        function initRangeHistogram(histoData) {
            if (!histoData) {
                return;
            }

            var rangeData = [];
            var filteredRangeData = [];
            _.forEach(histoData.items, function (histDatum) {
                var min = histDatum.range.min;
                var max = histDatum.range.max;
                var range = {
                    type: 'number',
                    min: min,
                    max: max
                };

                rangeData.push({
                    'data': range,
                    'occurrences': histDatum.occurrences
                });
                filteredRangeData.push({
                    'data': range,
                    'filteredOccurrences': state.playground.filter.gridFilters.length ? getRangeFilteredOccurrence(min, max) : histDatum.occurrences
                });
            });

            service.histogram = initVerticalHistogram('occurrences', 'Occurrences', rangeData);
            service.filteredHistogram = initVerticalHistogram('filteredOccurrences', 'Filtered Occurrences', filteredRangeData);
        }

        /**
         * @ngdoc method
         * @name initDateRangeHistogram
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @param {Array} histoData Array of data
         * @description Adapt the date range data to fit histogram format
         */
        function initDateRangeHistogram(histoData) {
            if (!histoData) {
                return;
            }

            var patterns = _.chain(state.playground.grid.selectedColumn.statistics.patternFrequencyTable)
                .pluck('pattern')
                .map(TextFormatService.convertJavaDateFormatToMomentDateFormat)
                .value();

            var rangeData = _.map(histoData.items, function (histDatum) {
                //range are UTC dates. We convert them to local zone date, so the app date manipulation is easier.
                var minDate = new Date(histDatum.range.min);
                minDate.setTime(minDate.getTime() + (minDate.getTimezoneOffset() * 60 * 1000));
                var maxDate = new Date(histDatum.range.max);
                maxDate.setTime(maxDate.getTime() + (maxDate.getTimezoneOffset() * 60 * 1000));

                return {
                    'data': {
                        type: 'date',
                        label: getDateLabel(histoData.pace, minDate, maxDate),
                        min: minDate.getTime(),
                        max: maxDate.getTime()
                    },
                    'occurrences': histDatum.occurrences
                };
            });

            //init the main histogram
            service.histogram = initVerticalHistogram('occurrences', 'Occurrences', rangeData);

            //execute  a web worker that will compute the filtered occurrences
            dateFilteredWorkerWrapper = WorkerService.create(
                ['/worker/moment.js', '/worker/moment-jdateformatparser.js', '/worker/lodash.js'],
                [isInDateLimits],
                dateFilteredOccurrenceWorker);

            var filteredOccurrences = state.playground.filter.gridFilters.length ? state.playground.grid.filteredOccurences : null;
            dateFilteredWorkerWrapper.postMessage([rangeData, patterns, filteredOccurrences])
                .then(function (filteredRangeData) {
                    service.filteredHistogram = initVerticalHistogram('filteredOccurrences', 'Filtered Occurrences', filteredRangeData);
                })
                .finally(function () {
                    dateFilteredWorkerWrapper.clean();
                });
        }

        /**
         * @ngdoc method
         * @name isInDateLimits
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @description Predicate that test if a date is in the range
         * @param {number} minTimestamp The range min timestamp
         * @param {number} maxTimestamp The range max timestamp
         * @param {Array} patterns The date patterns to use for date parsing
         */
        function isInDateLimits(minTimestamp, maxTimestamp, patterns) {
            return function (value) {
                var parsedMoment = _.chain(patterns)
                    .map(function (pattern) {
                        return moment(value, pattern, true);
                    })
                    .find(function (momentDate) {
                        return momentDate.isValid();
                    })
                    .value();

                if (!parsedMoment) {
                    return false;
                }

                var time = parsedMoment.toDate().getTime();
                return time === minTimestamp || (time > minTimestamp && time < maxTimestamp);
            };
        }

        /**
         * @ngdoc method
         * @name dateFilteredOccurrenceWorker
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @description Web worker function to execute to get the date pattern filtered occurrences
         * @param {object} rangeData The range data
         * @param {Array} patterns The patterns to use for date parsing
         * @param {Array} filteredOccurences The filtered occurrences
         */
        function dateFilteredOccurrenceWorker(rangeData, patterns, filteredOccurences) {
            _.forEach(rangeData, function (range) {
                var minTimestamp = range.data.min;
                var maxTimestamp = range.data.max;

                range.filteredOccurrences = !filteredOccurences ?
                    range.occurrences :
                    _.chain(filteredOccurences)
                        .keys()
                        .filter(isInDateLimits(minTimestamp, maxTimestamp, patterns))
                        .map(function (key) {
                            return filteredOccurences[key];
                        })
                        .reduce(function (accu, value) {
                            return accu + value;
                        }, 0)
                        .value();
            });

            return rangeData;
        }

        /**
         * @ngdoc method
         * @name getDateFormat
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @param {String} pace The histogram time pace
         * @param {Date} startDate The range starting date
         * @description Returns the date pattern that fit the pace at the starting date
         */
        function getDateFormat(pace, startDate) {
            switch (pace) {
                case 'CENTURY':
                case 'DECADE':
                case 'YEAR':
                    return 'yyyy';
                case 'HALF_YEAR':
                    return '\'H\'' + (startDate.getMonth() / 6 + 1) + ' yyyy';
                case 'QUARTER':
                    return 'Q' + (startDate.getMonth() / 3 + 1) + ' yyyy';
                case 'MONTH':
                    return 'MMM yyyy';
                case 'WEEK':
                    return 'Www yyyy';
                default:
                    return 'mediumDate';
            }
        }

        /**
         * @ngdoc method
         * @name getDateLabel
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @param {string} pace The histogram time pace
         * @param {Date} minDate The range starting date
         * @param {Date} maxDate The range ending date
         * @description Returns the range label
         */
        function getDateLabel(pace, minDate, maxDate) {
            var dateFilter = $filter('date');
            var format = getDateFormat(pace, minDate);

            switch (pace) {
                case 'YEAR':
                case 'HALF_YEAR':
                case 'QUARTER':
                case 'MONTH':
                case 'WEEK':
                case 'DAY':
                    return dateFilter(minDate, format);
                default:
                    return '[' + dateFilter(minDate, format) + ', ' + dateFilter(maxDate, format) + '[';
            }
        }

        /**
         * @ngdoc method
         * @name getClassicFilteredOccurrence
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @param {string} value The value to have
         * @description Get the occurrence of the provided value
         * @returns {number} The Number of records
         */
        function getClassicFilteredOccurrence(value) {
            var occurrence = state.playground.grid.filteredOccurences[value];
            return occurrence || 0;
        }

        /**
         * @ngdoc method
         * @name initClassicHistogram
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @param {string} key The value key
         * @param {string} label The value label
         * @param {Array} dataTable The table to display
         * @description Set the frequency table that fit the histogram format (filter is managed in frontend)
         */
        function initClassicHistogram(key, label, dataTable) {
            if (!dataTable || !dataTable.length) {
                return;
            }

            var adaptedData = _.map(dataTable, function (rec) {
                rec.formattedValue = TextFormatService.adaptToGridConstraints(rec.data);
                rec.filteredOccurrences = getClassicFilteredOccurrence(rec.data);
                return rec;
            });

            service.histogram = {
                data: adaptedData,
                key: key,
                label: label,
                column: state.playground.grid.selectedColumn
            };
        }


        /**
         * @ngdoc method
         * @name initAggregationHistogram
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @param {string} key The value key
         * @param {string} label The value label
         * @param {Array} dataTable The table to display
         * @description Set the frequency table that fit the histogram format for aggregation (filter is managed in backend)
         */
        function initAggregationHistogram(key, label, dataTable) {
            var adaptedData = _.map(dataTable, function (rec) {
                rec.formattedValue = TextFormatService.adaptToGridConstraints(rec.data);
                return rec;
            });

            service.histogram = {
                data: adaptedData,
                key: key,
                label: label,
                column: state.playground.grid.selectedColumn
            };
        }

        /**
         * @ngdoc method
         * @name initVerticalHistogram
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @param {string} key The value key
         * @param {string} label The value label
         * @param {Array} dataTable The table to display
         * @description Create a records frequency ranges table that fit the histogram format
         */
        function initVerticalHistogram(key, label, dataTable) {
            return {
                data: dataTable,
                key: key,
                label: label,
                column: state.playground.grid.selectedColumn,
                activeLimits: null,
                vertical: true
            };
        }

        /**
         * @ngdoc method
         * @name getValueWithinRange
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @description Get the corresponding value within [min, max] interval.
         * If the value is not in the interval, we return min or max if it is under min or above max respectively
         */
        function getValueWithinRange(value, min, max) {
            if (value < min) {
                return min;
            }
            if (value > max) {
                return max;
            }
            return value;
        }

        /**
         * @ngdoc method
         * @name initRangeLimits
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @description Set the range slider limits to update the rangeSlider handlers
         * and the active/inactive bars of the vertical barchart
         */
        function initRangeLimits() {
            if (!service.histogram) {
                return;
            }

            var column = state.playground.grid.selectedColumn;
            var statistics = column.statistics;
            var currentRangeFilter = _.find(state.playground.filter.gridFilters, function (filter) {
                return filter.colId === column.id && filter.type === 'inside_range';
            });

            var rangeLimits = {
                min: statistics.min,
                max: statistics.max
            };

            if (currentRangeFilter) {
                var filterMin = currentRangeFilter.args.interval[0];
                var filterMax = currentRangeFilter.args.interval[1];

                rangeLimits.minFilterVal = filterMin;
                rangeLimits.maxFilterVal = filterMax;

                rangeLimits.minBrush = getValueWithinRange(filterMin, statistics.min, statistics.max);
                rangeLimits.maxBrush = getValueWithinRange(filterMax, statistics.min, statistics.max);

                service.histogram.activeLimits = [rangeLimits.minBrush, rangeLimits.maxBrush];
            }

            service.rangeLimits = rangeLimits;
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
            return isNaN(value) || value === parseInt(value, 10) ? value : +value.toFixed(2);
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
                case 'integer':
                case 'decimal':
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
                    specificStats.MIN_LENGTH = stats.textLengthSummary.minimalLength;
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

        /**
         * @ngdoc method
         * @name updateFilteredPatternsFrequency
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @description update patterns statistics
         * @param {string} column The column to be updated
         */
        function updateFilteredPatternsFrequency(column) {
            var patternFrequency = column.statistics.patternFrequencyTable;
            service.patterns = patternFrequency;

            datePatternWorkerWrapper = WorkerService.create(
                ['/worker/moment.js', '/worker/lodash.js', '/worker/moment-jdateformatparser.js'],
                [
                    {
                        workerFn0: workerFn0,
                        workerFn1: workerFn1
                    },
                    TextFormatService.escapeRegex,
                    valueMatchPatternFn,
                    isDatePattern,
                    valueMatchDatePatternFn,
                    valueMatchRegexFn,
                    valueMatchPatternFn
                ],
                patternOccurrenceWorker
            );

            var filteredRecords = state.playground.filter.gridFilters.length ? state.playground.grid.filteredRecords : null;
            datePatternWorkerWrapper.postMessage([column.id, patternFrequency, filteredRecords])
                .then(function (patternFrequencies) {
                    service.patterns = patternFrequencies;
                })
                .finally(function () {
                    datePatternWorkerWrapper.clean();
                });
        }

        /**
         * @ngdoc method
         * @name patternOccurrenceWorker
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @description Web worker function to execute to get the pattern filtered occurrences
         * @param {string} columnId The column id
         * @param {Array} patternFrequencyTable The pattern frequencies to update
         * @param {Array} filteredRecords The filtered records to process for the filtered occurrences number
         */
        function patternOccurrenceWorker(columnId, patternFrequencyTable, filteredRecords) {
            _.forEach(patternFrequencyTable, function (patternFrequency) {
                var pattern = patternFrequency.pattern;
                var matchingFn = valueMatchPatternFn(pattern);

                patternFrequency.filteredOccurrences = !filteredRecords ?
                    patternFrequency.occurrences :
                    _.chain(filteredRecords)
                        .pluck(columnId)
                        .filter(matchingFn)
                        .groupBy(function (value) {
                            return value;
                        })
                        .mapValues('length')
                        .reduce(function (accu, value) {
                            return accu + value;
                        }, 0)
                        .value();
            });

            return patternFrequencyTable;
        }

        /**
         * @ngdoc method
         * @name isDatePattern
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @description Check if the pattern is a date pattern
         * @param {string} pattern The pattern to check
         */
        function isDatePattern(pattern) {
            return (pattern.indexOf('d') > -1 ||
            pattern.indexOf('M') > -1 ||
            pattern.indexOf('y') > -1 ||
            pattern.indexOf('H') > -1 ||
            pattern.indexOf('h') > -1 ||
            pattern.indexOf('m') > -1 ||
            pattern.indexOf('s') > -1);
        }

        /**
         * @ngdoc method
         * @name valueMatchDatePatternFn
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @description Create a predicate that check if a value match the date pattern
         * @param {string} pattern The date pattern to match
         */
        function valueMatchDatePatternFn(pattern) {
            var datePattern = workerFn0(pattern);
            return function (value) {
                return value && moment(value, datePattern, true).isValid();
            };
        }

        /**
         * @ngdoc method
         * @name valueMatchRegexFn
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @description Create a predicate that check if a value match the regex pattern
         * @param {string} pattern The pattern to match
         */
        function valueMatchRegexFn(pattern) {
            var regex = workerFn1(pattern);
            return function (value) {
                return value && value.match(regex);
            };
        }

        /**
         * @ngdoc method
         * @name valueMatchPatternFn
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @description Create the adequat predicate that match the pattern. It can be empty, a date pattern, or an alphanumeric pattern
         * @param {string} pattern The pattern to match
         */
        function valueMatchPatternFn(pattern) {
            if (pattern === '') {
                return function (value) {
                    return value === '';
                };
            }
            else if (isDatePattern(pattern)) {
                return valueMatchDatePatternFn(pattern);
            }
            else {
                return valueMatchRegexFn(pattern);
            }
        }

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
         * @name getRangeFilterRemoveFn
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @description Create a remove callback to reinit the current active limits on the current column range chart
         */
        function getRangeFilterRemoveFn() {
            var selectedColumn = state.playground.grid.selectedColumn;
            var columnMin = selectedColumn.statistics.min;
            var columnMax = selectedColumn.statistics.max;

            return function removeFilterFn(filter) {
                var actualSelectedColumn = state.playground.grid.selectedColumn;
                if (filter.colId === actualSelectedColumn.id) {
                    initRangeLimits();
                    //to reset the vertical bars colors
                    service.histogram.activeLimits = [columnMin, columnMax];
                }
            };
        }

        //--------------------------------------------------------------------------------------------------------------
        //---------------------------------------------NON AGGREGATION--------------------------------------------------
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
            var simplifiedType = ConverterService.simplifyType(column.type);
            switch (simplifiedType) {
                case 'integer':
                case 'decimal':
                    initRangeHistogram(column.statistics.histogram);
                    updateBoxplotData();
                    initRangeLimits();
                    break;
                case 'date':
                    initDateRangeHistogram(column.statistics.histogram);
                    break;
                case 'text':
                case 'boolean':
                    initClassicHistogram('occurrences', 'Occurrences', column.statistics.frequencyTable);
                    break;
                default :
                    console.log('nor a number neither a boolean neither a string but a ' + simplifiedType);
            }
        }

        /**
         * @ngdoc method
         * @name processData
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @description Processes the statistics data for visualization on the selected column
         */
        function processData() {
            var column = state.playground.grid.selectedColumn;
            reset(true, false, false);

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

        //--------------------------------------------------------------------------------------------------------------
        //-------------------------------------------------Aggregation--------------------------------------------------
        //--------------------------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name processAggregation
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @param {object} column The column to visualize
         * @param {string} aggregation The aggregation to perform
         * @description Processes the statistics aggregation for visualization
         */
        function processAggregation(column, aggregation) {
            if (!aggregation) {
                removeSavedColumnAggregation();
                return processData();
            }

            reset(true, false, false);
            var datasetId = state.playground.dataset.id;
            var preparationId = state.playground.preparation && state.playground.preparation.id;
            var stepId = preparationId && RecipeService.getLastActiveStep() && RecipeService.getLastActiveStep().transformation.stepId;
            var selectedColumn = state.playground.grid.selectedColumn;

            var aggregationParameters = {
                datasetId: preparationId ? null : datasetId,
                preparationId: preparationId,
                stepId: stepId,
                operations: [{
                    operator: aggregation,
                    columnId: column.id
                }],
                groupBy: [selectedColumn.id]
            };

            //add filter in parameters only if there are filters
            aggregationParameters = _.extend(aggregationParameters, FilterAdapterService.toTree(state.playground.filter.gridFilters));

            StatisticsRestService.getAggregations(aggregationParameters)
                .then(function (response) {
                    initAggregationHistogram(aggregation, $filter('translate')(aggregation), response);
                    service.histogram.aggregationColumn = column;
                    service.histogram.aggregation = aggregation;

                    saveColumnAggregation();
                });
        }

        /**
         * @ngdoc method
         * @name getSavedColumnAggregation
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @description Get the saved dataset column aggregation.
         */
        function getSavedColumnAggregation() {
            var datasetId = state.playground.dataset && state.playground.dataset.id;
            var preparationId = state.playground.preparation && state.playground.preparation.id;
            var columnId = state.playground.grid.selectedColumn && state.playground.grid.selectedColumn.id;
            return StorageService.getAggregation(datasetId, preparationId, columnId);
        }

        /**
         * @ngdoc method
         * @name removeSavedColumnAggregation
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @description Delete the actual column aggregation key in localStorage
         */
        function removeSavedColumnAggregation() {
            var datasetId = state.playground.dataset && state.playground.dataset.id;
            var preparationId = state.playground.preparation && state.playground.preparation.id;
            var columnId = state.playground.grid.selectedColumn && state.playground.grid.selectedColumn.id;
            return StorageService.removeAggregation(datasetId, preparationId, columnId);
        }

        /**
         * @ngdoc method
         * @name saveColumnAggregation
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @description Update the actual dataset column aggregation in localStorage
         */
        function saveColumnAggregation() {
            var datasetId = state.playground.dataset && state.playground.dataset.id;
            var preparationId = state.playground.preparation && state.playground.preparation.id;
            var columnId = state.playground.grid.selectedColumn && state.playground.grid.selectedColumn.id;

            var aggregation = {};
            aggregation.aggregation = service.histogram.aggregation;
            aggregation.aggregationColumnId = service.histogram.aggregationColumn.id;

            return StorageService.setAggregation(datasetId, preparationId, columnId, aggregation);
        }

        /**
         * @ngdoc method
         * @name resetCharts
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @description Removes all the data to disable all visualization
         */
        function getAggregationColumns() {
            if (state.playground.data) {
                var column = state.playground.grid.selectedColumn;
                //TODO JSO : put a cache again that is invalidated when one of the columns change
                return DatagridService.getNumericColumns(column);
            }
        }

        //--------------------------------------------------------------------------------------------------------------
        //-------------------------------------------------COMMON-------------------------------------------------------
        //--------------------------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name updateStatistics
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @description update aggregation for a selected column
         */
        function updateStatistics() {
            var column = state.playground.grid.selectedColumn;
            reset(true, true, false);
            initStatisticsValues(column);

            var columnAggregation = getSavedColumnAggregation();
            var aggregatedColumn = columnAggregation && _.findWhere(getAggregationColumns(), {id: columnAggregation.aggregationColumnId});
            var aggregation = columnAggregation && columnAggregation.aggregation;

            processAggregation(aggregatedColumn, aggregation);
            updateFilteredPatternsFrequency(column);
        }

        /**
         * @ngdoc method
         * @name reset
         * @methodOf data-prep.services.statistics.service:StatisticsService
         * @param {boolean} charts Remove charts
         * @param {boolean} statistics Remove statistics data
         * @param {boolean} cache Clear cache
         * @description Removes data depending on the parameters
         */
        function reset(charts, statistics, cache) {
            if (charts) {
                service.boxPlot = null;
                service.histogram = null;
                service.filteredHistogram = null;
                service.rangeLimits = null;
                service.stateDistribution = null;
            }

            if (statistics) {
                service.statistics = null;
            }

            if (cache) {
                StatisticsRestService.resetCache();
            }

            if (dateFilteredWorkerWrapper) {
                dateFilteredWorkerWrapper.terminate();
                dateFilteredWorkerWrapper = null;
            }
            if (datePatternWorkerWrapper) {
                datePatternWorkerWrapper.terminate();
                datePatternWorkerWrapper = null;
            }
        }
    }

    angular.module('data-prep.services.statistics')
        .service('StatisticsService', StatisticsService);
})();