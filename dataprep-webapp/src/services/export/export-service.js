(function() {
    'use strict';

    function ExportService(DatasetGridService) {
        var rowSeparator = '\r\n';

        /**
         * Get array of column ids
         * @returns {Array}
         */
        var getColumns = function() {
            return _.map(DatasetGridService.data.columns, function(col) {
                return col.id;
            });
        };

        /**
         * Serialize value :
         * - Add double-quotes surrounding value
         * - Double all the double-quotes to escape it
         * @param value - the value to serialize
         * @returns {string}
         */
        var serializeValue = function(value) {
            var quoteEscapedValue = value.replace(/"/g, '""');
            return quoteEscapedValue.search(/("|,|\t|;|\n)/g) > -1 ? '"' + quoteEscapedValue + '"' : quoteEscapedValue;
        };

        /**
         * Create line serialization function closure, taking the
         * @param separator - the value separator
         * @returns {Function}
         */
        var serializeLineFn = function(separator) {
            var columns = getColumns();
            return function(line) {
                return _.map(columns, function(col) {
                    return serializeValue(line[col]);
                }).join(separator);
            };
        };

        /**
         * Serialize the current data into CSV string
         * @param separator - the value separator to use
         * @returns {string}
         */
        var serializeToCSV = function(separator) {
            var serializeLine = serializeLineFn(separator);

            var serializedCols = _.map(getColumns(), serializeValue).join(separator);
            var serializedLines = _.map(DatasetGridService.data.records, serializeLine).join(rowSeparator);

            return serializedCols + rowSeparator + serializedLines;
        };

        /**
         * Return all the CSV infos of the current data
         * @param separator
         * @returns {{name: string, content: string, charset: string}}
         */
        this.exportToCSV = function(separator) {
            var csv = serializeToCSV(separator);
            var name = DatasetGridService.metadata.name + '.csv';

            return {
                name: name,
                content: csv,
                charset: 'utf-8'
            };
        };
    }

    angular.module('data-prep.services.export')
        .service('ExportService', ExportService);
})();