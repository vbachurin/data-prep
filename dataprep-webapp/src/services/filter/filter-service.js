(function() {
    'use strict';

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
            return function(item) {
                return item[colId].toLowerCase().indexOf(lowerCasePhrase) > -1;
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