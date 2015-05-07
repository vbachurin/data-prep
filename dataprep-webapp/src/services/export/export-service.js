(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.export.service:ExportService
     * @description Export service. This service export a loaded dataset to CSV
     * @requires data-prep.services.playground.service:DatagridService
     */
    function ExportService(DatagridService) {
        var rowSeparator = '\r\n';

        /**
         * @ngdoc method
         * @name getDatasets
         * @methodOf data-prep.services.export.service:ExportService
         * @description Get the loaded dataset columns
         * @returns {string[]} - the columns id
         */
        var getColumns = function() {
            return _.map(DatagridService.data.columns, function(col) {
                return col.id;
            });
        };

        /**
         * @ngdoc method
         * @name serializeValue
         * @methodOf data-prep.services.export.service:ExportService
         * @description [PRIVATE] Serialize value :
          - Add double-quotes surrounding value
          - Double all the double-quotes to escape it
         * @returns {string} - the serialized value
         */
        var serializeValue = function(value) {
            var quoteEscapedValue = value.replace(/"/g, '""');
            return quoteEscapedValue.search(/("|,|\t|;|\n)/g) > -1 ? '"' + quoteEscapedValue + '"' : quoteEscapedValue;
        };

        /**
         * @ngdoc method
         * @name serializeLineFn
         * @methodOf data-prep.services.export.service:ExportService
         * @param {string} separator - the value separator
         * @description [PRIVATE] Create line serialization function closure
         * @returns {function} - the serialized value
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
         * @ngdoc method
         * @name serializeToCSV
         * @methodOf data-prep.services.export.service:ExportService
         * @param {string} separator - the value separator
         * @description [PRIVATE] Serialize the current data into CSV string
         * @returns {string} - the serialized data
         */
        var serializeToCSV = function(separator) {
            var serializeLine = serializeLineFn(separator);

            var serializedCols = _.map(getColumns(), serializeValue).join(separator);
            var serializedLines = _.map(DatagridService.data.records, serializeLine).join(rowSeparator);

            return serializedCols + rowSeparator + serializedLines;
        };

        /**
         * Return all the CSV infos of the current data
         * @param separator
         * @returns {{name: string, content: string, charset: string}}
         */

        /**
         * @ngdoc method
         * @name exportToCSV
         * @methodOf data-prep.services.export.service:ExportService
         * @param {string} separator - the value separator
         * @description Return all the CSV infos of the current data
         * @returns {object} - the CSV infos and value {{name: string, content: string, charset: string}}
         */
        this.exportToCSV = function(separator) {
            var csv = serializeToCSV(separator);
            var name = DatagridService.metadata.name + '.csv';

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