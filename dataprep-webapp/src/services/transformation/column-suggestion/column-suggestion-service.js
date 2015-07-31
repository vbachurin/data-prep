(function () {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.transformation.service:ColumnSuggestionService
     * @description Transformation Column suggestion service. This service provide the current column suggestions
     * @requires data-prep.services.transformation.service:TransformationCacheService
     * @requires data-prep.services.utils.service:ConverterService
     */
    function ColumnSuggestionService(TransformationCacheService, ConverterService) {
        var COLUMN_CATEGORY = 'columns';
        var self = this;

        /**
         * @ngdoc property
         * @name currentColumn
         * @propertyOf data-prep.services.transformation.service:ColumnSuggestionService
         * @description The currently selected column
         * @type {Object}
         */
        self.currentColumn = null;

        /**
         * @ngdoc property
         * @name transformations
         * @propertyOf data-prep.services.transformation.service:ColumnSuggestionService
         * @description The currently selected column transformations
         * @type {Object}
         */
        self.transformations = null;

        /**
         * @ngdoc property
         * @name statistics
         * @propertyOf data-prep.services.transformation.service:ColumnSuggestionService
         * @description The currently selected column statistics
         * @type {Object}
         */
        self.statistics = null;

        /**
         * @ngdoc method
         * @name filterAndGroup
         * @methodOf data-prep.services.transformation.service:ColumnSuggestionService
         * @param {object[]} transfos The transformations list
         * @description Keep only the non 'columns' category and group them by category
         * @returns {object} An object containing {key: value} = {category: [transformations]}
         */
        function filterAndGroup(transfos) {
            return _.chain(transfos)
                .filter(function (transfo) {
                    return transfo.category !== COLUMN_CATEGORY;
                })
                .sortBy(function (action) {
                    return action.label.toLowerCase();
                })
                .value();
        }

        /**
         * @ngdoc method
         * @name initTransformations
         * @methodOf data-prep.services.transformation.service:ColumnSuggestionService
         * @param {object} column The target column
         * @description Get and preparation the transformations from backend
         */
        function initTransformations(column) {
            TransformationCacheService.getTransformations(column)
                .then(function (transformations) {
                    if (self.currentColumn === column) {
                        self.transformations = filterAndGroup(transformations);
                    }
                });
        }

        /**
         * @ngdoc method
         * @name clean
         * @methodOf data-prep.services.transformation.service:ColumnSuggestionService
         * @param {number} value Value to clean the float
         * @description Cleans the value to have 2 decimal (5.2568845842587425588 -> 5.25)
         * @returns {number} The value in the clean format
         */
        function clean(value) {
            return value === parseInt(value, 10) ? value : value.toFixed(2);
        }

        /**
         * @ngdoc method
         * @name initStatistics
         * @methodOf data-prep.services.transformation.service:ColumnSuggestionService
         * @param {object} column The target column
         * @description Initialize the statistics to display
         */
        function initStatistics(column) {
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

            self.statistics = {
                common: commonStats,
                specific: specificStats
            };
        }

        /**
         * @ngdoc method
         * @name setColumn
         * @methodOf data-prep.services.transformation.service:ColumnSuggestionService
         * @param {object} column The new selected column
         * @description Set the selected column and init its suggested transformations and statistics
         */
        this.setColumn = function setColumn(column) {
            if (column === self.currentColumn) {
                return;
            }

            self.currentColumn = column;
            self.transformations = null;
            initTransformations(column);
            initStatistics(column);
        };

        /**
         * @ngdoc method
         * @name reset
         * @methodOf data-prep.services.transformation.service:ColumnSuggestionService
         * @description Reset the current column and the transformations
         */
        this.reset = function reset() {
            self.currentColumn = null;
            self.transformations = null;
            self.statistics = null;
        };
    }

    angular.module('data-prep.services.transformation')
        .service('ColumnSuggestionService', ColumnSuggestionService);
})();