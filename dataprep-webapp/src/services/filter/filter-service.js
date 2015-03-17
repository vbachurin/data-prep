(function() {
    'use strict';

    /**
     * Escape all regexp characters except * wildcard, and adapt * wildcard to regexp (* --> .*)
     * @param str
     * @returns {*}
     */
    function escapeRegExpExceptStar(str) {
        return str.replace(/[\-\[\]\/\{\}\(\)\+\?\.\\\^\$\|]/g, "\\$&").replace(/\*/g, '.*');
    }

    /**
     * Filter infos object
     * @param type - the filter type
     * @param colId - the column id
     * @param args - the filter arguments
     * @param filterFn - the filter function
     * @constructor
     */
    function Filter(type, colId, args, filterFn) {
        var self = this;

        self.type = type;
        self.colId = colId;
        self.args = args;
        self.filterFn = filterFn;
        self.toString = function() {
            var result = self.colId.toUpperCase() + ': ';
            switch(type) {
                case 'contains':
                    result += args.phrase;
                    break;
            }

            return result;
        };
    }

    function FilterService(DatasetGridService) {
        var self = this;
        self.filters = [];

        /**
         * Return the column with a cell that can match the phrase
         * @param phrase - to match
         * @returns {Array}
         */
        self.getColumnsContaining = function(phrase) {
            if (!phrase) {
                return [];
            }

            var regexp = new RegExp(escapeRegExpExceptStar(phrase));
            var canBeNumeric = !isNaN(phrase.replace(/\*/g, ''));
            var canBeBoolean = 'true'.match(regexp) || 'false'.match(regexp);

            return DatasetGridService.getColumnsContaining(regexp, canBeNumeric, canBeBoolean);
        };

        /**
         * Remove all the filters and update datagrid filters
         */
        self.removeAllFilters = function() {
            DatasetGridService.resetFilters();
            self.filters = [];
        };

        /**
         * Create 'contains' filter function
         * @param colId - the column id
         * @param phrase - the phrase that the item must contain
         * @returns {Function}
         */
        var createContainFilter = function(colId, phrase) {
            var lowerCasePhrase = phrase.toLowerCase();
            var regexp = new RegExp(escapeRegExpExceptStar(lowerCasePhrase));
            return function(item) {
                return item[colId].toLowerCase().match(regexp);
            };
        };

        /**
         * Add a filter and update datagrid filters
         * @param type - the filter type (ex : contains)
         * @param colId - the column id
         * @param args - the filter arguments (ex for 'contains' type : {phrase: 'toto'})
         */
        self.addFilter = function(type, colId, args) {
            var filterFn;
            switch(type) {
                case 'contains':
                    filterFn = createContainFilter(colId, args.phrase);
                    break;
            }

            var filterInfos = new Filter(type, colId, args, filterFn);
            DatasetGridService.addFilter(filterFn);
            self.filters.push(filterInfos);
        };

        /**
         * Remove a filter and update datagrid filters
         * @param filter
         */
        self.removeFilter = function(filter) {
            var filterIndex = self.filters.indexOf(filter);
            if(filterIndex > -1) {
                DatasetGridService.removeFilter(filter.filterFn);
                self.filters.splice(filterIndex, 1);
            }
        };
    }

    angular.module('data-prep.services.filter')
        .service('FilterService', FilterService);
})();