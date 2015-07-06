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
     * Filter info object
     * @param type - the filter type
     * @param colId - the column id
     * @param colName - the column name
     * @param editable - true if the filter is editable
     * @param args - the filter arguments
     * @param filterFn - the filter function
     * @constructor
     */
    function Filter(type, colId, colName, editable, args, filterFn) {
        var self = this;

        self.type = type;
        self.colId = colId;
        self.colName = colName;
        self.editable = editable;
        self.args = args;
        self.filterFn = filterFn;
        self.__defineGetter__('value', function(){
            switch(self.type) {
                case 'contains':
                    return self.args.phrase;
                case 'invalid_records':
                    return 'invalid records';
                case 'empty_records':
                    return 'empty records';
                case 'valid_records':
                    return 'valid records';
            }
        });
    }

    /**
     * @ngdoc service
     * @name data-prep.services.filter.service:FilterService
     * @description Filter service. This service holds the filters list and provide the entry point to datagrid filters
     * @requires data-prep.services.playground.service:DatagridService
     */
    function FilterService(DatagridService) {
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
         * @returns {Object[]} - the columns id that contains a matching value (col.id & col.name)
         */
        self.getColumnsContaining = function(phrase) {
            if (!phrase) {
                return [];
            }

            var regexp = new RegExp(escapeRegExpExceptStar(phrase));
            var canBeNumeric = !isNaN(phrase.replace(/\*/g, ''));
            var canBeBoolean = 'true'.match(regexp) || 'false'.match(regexp);

            return DatagridService.getColumnsContaining(regexp, canBeNumeric, canBeBoolean);
        };

        /**
         * @ngdoc method
         * @name removeAllFilters
         * @methodOf data-prep.services.filter.service:FilterService
         * @description Remove all the filters and update datagrid filters
         */
        self.removeAllFilters = function() {
            DatagridService.resetFilters();
            self.filters = [];
        };

        /**
         * @ngdoc method
         * @name createContainFilterFn
         * @methodOf data-prep.services.filter.service:FilterService
         * @param {string} colId - the column id
         * @param {string} phrase - the phrase that the item must contain
         * @description [PRIVATE] Create a 'contains' filter function
         * @returns {function} - the predicated function
         */
        var createContainFilterFn = function(colId, phrase) {
            var lowerCasePhrase = phrase.toLowerCase();
            var regexp = new RegExp(escapeRegExpExceptStar(lowerCasePhrase));
            return function(item) {
                // col could be removed by a step
                if (item[colId]) {
                    return item[colId].toLowerCase().match(regexp);
                }
                else {
                    return false;
                }
            };
        };

        /**
         * @ngdoc method
         * @name createEqualFilterFn
         * @methodOf data-prep.services.filter.service:FilterService
         * @param {string} colId - the column id
         * @param {string[]} values - the values to compare the record value against
         * @description [PRIVATE] Create an 'equals' filter function
         * @returns {function} - the predicated function
         */
        var createEqualFilterFn = function(colId, values) {
            return function(item) {
                return values.indexOf(item[colId]) > -1;
            };
        };

        /**
         * @ngdoc method
         * @name createValidFilterFn
         * @methodOf data-prep.services.filter.service:FilterService
         * @param {string} colId - the column id
         * @description [PRIVATE] Create a 'valid' filter function
         * @returns {function} - the predicated function
         */
        var createValidFilterFn = function(colId, values){
            return function(item) {
                return values.indexOf(item[colId]) === -1 && item[colId];
            };
        };

        /**
         * @ngdoc method
         * @name createEmptyFilterFn
         * @methodOf data-prep.services.filter.service:FilterService
         * @param {string} colId - the column id
         * @description [PRIVATE] Create an 'empty' filter function
         * @returns {function} - the predicated function
         */
        var createEmptyFilterFn = function(colId) {
            return function(item) {
                return !item[colId];
            };
        };

        /**
         * @ngdoc method
         * @name addFilter
         * @methodOf data-prep.services.filter.service:FilterService
         * @param {string} type - the filter type (ex : contains)
         * @param {string} colId - the column id
         * @param {string} colName - the column name
         * @param {string} args - the filter arguments (ex for 'contains' type : {phrase: 'toto'})
         * @description Add a filter and update datagrid filters
         */
        self.addFilter = function(type, colId, colName, args) {
            var filterFn;
            var filterInfo;
            switch(type) {
                case 'contains':
                    filterFn = createContainFilterFn(colId, args.phrase);
                    filterInfo = new Filter(type, colId, colName, true, args, filterFn);
                    break;
                case 'invalid_records':
                    filterFn = createEqualFilterFn(colId, args.values);
                    filterInfo = new Filter(type, colId, colName, false, args, filterFn);
                    break;
                case 'empty_records':
                    filterFn = createEmptyFilterFn(colId);
                    filterInfo = new Filter(type, colId, colName, false, args, filterFn);
                    break;
                case 'valid_records':
                    filterFn = createValidFilterFn(colId, args.values);
                    filterInfo = new Filter(type, colId, colName, false, args, filterFn);
                    break;
            }
            DatagridService.addFilter(filterFn);
            self.filters.push(filterInfo);
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
                DatagridService.removeFilter(filter.filterFn);
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
            var newFilter;
            switch(oldFilter.type) {
                case 'contains':
                    newArgs = { phrase: newValue};
                    newFilterFn = createContainFilterFn(oldFilter.colId, newValue);
                    newFilter = new Filter(oldFilter.type, oldFilter.colId, oldFilter.colName, true, newArgs, newFilterFn);
                    break;
            }

            DatagridService.updateFilter(oldFn, newFilter.filterFn);
            self.filters.splice(index, 1, newFilter);
        };
    }

    angular.module('data-prep.services.filter')
        .service('FilterService', FilterService);
})();