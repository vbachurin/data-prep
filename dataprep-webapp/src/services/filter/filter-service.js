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
        this.type = type;
        this.colId = colId;
        this.args = args;
        this.filterFn = filterFn;
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
            return function(item) {
                return item[colId].indexOf(phrase) > -1;
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

            if(filterFn) {
                var filterInfos = new Filter(type, colId, args, filterFn);
                DatasetGridService.addFilter(filterFn);
                self.filters.push(filterInfos);
            }
        };
    }

    angular.module('data-prep.services.filter')
        .service('FilterService', FilterService);
})();