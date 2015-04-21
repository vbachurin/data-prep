(function() {
    'use strict';

    /**
     * Escape all regexp characters except * wildcard, and adapt * wildcard to regexp (* --> .*)
     * @param str
     * @returns {*}
     */
    function escapeRegExpExceptStar(str) {
        return str.replace(/[\-\[\]\/\{\}\(\)\+\?\.\\\^\$\|]/g, '\\$&').replace(/\*/g, '.*');
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
        self.__defineGetter__('value', function(){
            switch(self.type) {
                case 'contains':
                    return self.args.phrase;
            }
        });
    }

    /**
     * @ngdoc service
     * @name data-prep.services.filter.service:FilterService
     * @description Filter service. This service holds the filters list and provide the entry point to datagrid filters
     * @requires data-prep.services.dataset.service:DatasetGridService
     */
    function FilterService(DatasetGridService) {
        var self = this;

        /**
         * @ngdoc property
         * @name filters
         * @propertyOf data-prep.services.filter.service:FilterService
         * @description the filters list
         */
        self.filters = [];

        /**
         * @ngdoc method
         * @name getColumnsContaining
         * @methodOf data-prep.services.filter.service:FilterService
         * @param {string} phrase - to match. Wildcard (*) accepted
         * @description Return the column with a cell that can match the phrase. It take into account a possible wildcard (*)
         * @returns {string[]} - the columns id that contains a matching value
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
         * @ngdoc method
         * @name removeAllFilters
         * @methodOf data-prep.services.filter.service:FilterService
         * @description Remove all the filters and update datagrid filters
         */
        self.removeAllFilters = function() {
            DatasetGridService.resetFilters();
            self.filters = [];
        };

        /**
         * @ngdoc method
         * @name createContainFilter
         * @methodOf data-prep.services.filter.service:FilterService
         * @param {string} colId - the column id
         * @param {string} phrase - the phrase that the item must contain
         * @description [PRIVATE] Create a 'contains' filter function
         * @returns {function} - the predicated function
         */
        var createContainFilter = function(colId, phrase) {
            var lowerCasePhrase = phrase.toLowerCase();
            var regexp = new RegExp(escapeRegExpExceptStar(lowerCasePhrase));
            return function(item) {
                return item[colId].toLowerCase().match(regexp);
            };
        };

        /**
         * @ngdoc method
         * @name addFilter
         * @methodOf data-prep.services.filter.service:FilterService
         * @param {string} type - the filter type (ex : contains)
         * @param {string} colId - the column id
         * @param {string} args - the filter arguments (ex for 'contains' type : {phrase: 'toto'})
         * @description Add a filter and update datagrid filters
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
         * @ngdoc method
         * @name removeFilter
         * @methodOf data-prep.services.filter.service:FilterService
         * @param {object} filter - the filter to delete
         * @description Remove a filter and update datagrid filters
         */
        self.removeFilter = function(filter) {
            var filterIndex = self.filters.indexOf(filter);
            if(filterIndex > -1) {
                DatasetGridService.removeFilter(filter.filterFn);
                self.filters.splice(filterIndex, 1);
            }
        };

        /**
         * @ngdoc method
         * @name updateFilter
         * @methodOf data-prep.services.filter.service:FilterService
         * @param {object} oldFilter - the filter to update
         * @param {object} newValue - the filter update parameters
         * @description Update an existing filter and update datagrid filters
         */
        self.updateFilter = function(oldFilter, newValue) {
            var index = self.filters.indexOf(oldFilter);
            var oldFn = oldFilter.filterFn;

            var newArgs;
            var newFilterFn;
            switch(oldFilter.type) {
                case 'contains':
                    newArgs = { phrase: newValue};
                    newFilterFn = createContainFilter(oldFilter.colId, newValue);
                    break;
            }
            var newFilter = new Filter(oldFilter.type, oldFilter.colId, newArgs, newFilterFn);

            DatasetGridService.updateFilter(oldFn, newFilter.filterFn);
            self.filters.splice(index, 1, newFilter);
        };
    }

    angular.module('data-prep.services.filter')
        .service('FilterService', FilterService);
})();