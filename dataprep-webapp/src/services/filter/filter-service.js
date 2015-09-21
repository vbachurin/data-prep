(function () {
    'use strict';

    /**
     * Escape all regexp characters except * wildcard, and adapt * wildcard to regexp (* --> .*)
     * @param {string} str The string to escape
     * @returns {*}
     */
    function escapeRegExpExceptStar(str) {
        return str.replace(/[\-\[\]\/\{\}\(\)\+\?\.\\\^\$\|]/g, '\\$&').replace(/\*/g, '.*');
    }

    /**
     * Filter info object
     * @param {string} type The filter type
     * @param {string} colId The column id
     * @param {string} colName The column name
     * @param {boolean} editable True if the filter is editable
     * @param {object} args The filter arguments
     * @param {function} filterFn The filter function
     * @param {function} removeFilterFn The remove filter callback
     * @constructor
     */
    function Filter(type, colId, colName, editable, args, filterFn, removeFilterFn) {
        var self = this;

        self.type = type;
        self.colId = colId;
        self.colName = colName;
        self.editable = editable;
        self.args = args;
        self.filterFn = filterFn;
        self.removeFilterFn = removeFilterFn;
        self.__defineGetter__('value', function () {
            switch (self.type) {
                case 'contains':
                    return self.args.phrase;
                case 'exact_filter':
                    return self.args.phrase;
                case 'invalid_records':
                    return 'invalid records';
                case 'empty_records':
                    return 'empty records';
                case 'valid_records':
                    return 'valid records';
                case 'inside_range':
                    return '[' + d3.format(',')(args.interval[0]) + ' .. ' + d3.format(',')(args.interval[1]) + ']';
            }
        });
    }

    /**
     * @ngdoc service
     * @name data-prep.services.filter.service:FilterService
     * @description Filter service. This service holds the filters list and provide the entry point to datagrid filters
     * @requires data-prep.services.playground.service:DatagridService
     */
    function FilterService(DatagridService, NumbersValidityService) {
        var service = {
            /**
             * @ngdoc property
             * @name filters
             * @propertyOf data-prep.services.filter.service:FilterService
             * @description The filters list
             */
            filters: [],

            //utils
            getColumnsContaining: getColumnsContaining,

            //life
            addFilter: addFilter,
            updateFilter: updateFilter,
            removeAllFilters: removeAllFilters,
            removeFilter: removeFilter
        };
        return service;

        //--------------------------------------------------------------------------------------------------------------
        //---------------------------------------------------UTILS------------------------------------------------------
        //--------------------------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name getColumnsContaining
         * @methodOf data-prep.services.filter.service:FilterService
         * @param {string} phrase To match. Wildcard (*) accepted
         * @description Return the column with a cell that can match the phrase. It take into account a possible wildcard (*)
         * @returns {Object[]} The columns id that contains a matching value (col.id & col.name)
         */
        function getColumnsContaining(phrase) {
            if (!phrase) {
                return [];
            }

            var regexp = new RegExp(escapeRegExpExceptStar(phrase));
            var canBeNumeric = !isNaN(phrase.replace(/\*/g, ''));
            var canBeBoolean = 'true'.match(regexp) || 'false'.match(regexp);

            return DatagridService.getColumnsContaining(regexp, canBeNumeric, canBeBoolean);
        }

        //--------------------------------------------------------------------------------------------------------------
        //---------------------------------------------------FILTER FNs-------------------------------------------------
        //--------------------------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name createContainFilterFn
         * @methodOf data-prep.services.filter.service:FilterService
         * @param {string} colId The column id
         * @param {string} phrase The phrase that the item must contain
         * @description [PRIVATE] Create a 'contains' filter function
         * @returns {function} The predicate function
         */
        function createContainFilterFn(colId, phrase) {
            var lowerCasePhrase = phrase.toLowerCase();
            var regexp = new RegExp(escapeRegExpExceptStar(lowerCasePhrase));
            return function (item) {
                // col could be removed by a step
                if (item[colId]) {
                    return item[colId].toLowerCase().match(regexp);
                }
                else {
                    return false;
                }
            };
        }

        /**
         * @ngdoc method
         * @name createExactFilterFn
         * @methodOf data-prep.services.filter.service:FilterService
         * @param {string} colId The column id
         * @param {string} phrase The phrase that the item must be exactly equal to
         * @description [PRIVATE] Create a 'exact_filter' filter function
         * @returns {function} The predicate function
         */
        function createExactFilterFn(colId, phrase) {
            return function (item) {
                // col could be removed by a step
                if (item[colId]) {
                    return item[colId] === phrase;
                }
                else {
                    return false;
                }
            };
        }

        /**
         * @ngdoc method
         * @name createEqualFilterFn
         * @methodOf data-prep.services.filter.service:FilterService
         * @param {string} colId The column id
         * @param {string[]} values The invalid values
         * @description Create an 'equals' filter function
         * @returns {function} The predicate function
         */
        function createEqualFilterFn(colId, values) {
            return function (item) {
                return values.indexOf(item[colId]) > -1;
            };
        }

        /**
         * @ngdoc method
         * @name createValidFilterFn
         * @methodOf data-prep.services.filter.service:FilterService
         * @param {string} colId The column id
         * @param {string[]} values The invalid values
         * @description Create a 'valid' filter function
         * @returns {function} The predicate function
         */
        function createValidFilterFn(colId, values) {
            return function (item) {
                return item[colId] && values.indexOf(item[colId]) === -1;
            };
        }

        /**
         * @ngdoc method
         * @name createEmptyFilterFn
         * @methodOf data-prep.services.filter.service:FilterService
         * @param {string} colId The column id
         * @description Create an 'empty' filter function
         * @returns {function} The predicate function
         */
        function createEmptyFilterFn(colId) {
            return function (item) {
                return !item[colId];
            };
        }

        /**
         * @ngdoc method
         * @name createRangeFilterFn
         * @methodOf data-prep.services.filter.service:FilterService
         * @param {string} colId The column id
         * @param {Array} values The filter interval
         * @description Create a 'range' filter function
         * @returns {function} The predicate function
         */
        function createRangeFilterFn(colId, values) {
            return function (item) {
                return NumbersValidityService.toNumber(item[colId]) >= values[0] && NumbersValidityService.toNumber(item[colId]) <= values[1];
            };
        }

        //--------------------------------------------------------------------------------------------------------------
        //---------------------------------------------------FILTER LIFE------------------------------------------------
        //--------------------------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name addFilter
         * @methodOf data-prep.services.filter.service:FilterService
         * @param {string} type The filter type (ex : contains)
         * @param {string} colId The column id
         * @param {string} colName The column name
         * @param {string} args The filter arguments (ex for 'contains' type : {phrase: 'toto'})
         * @param {function} removeFilterFn An optional remove callback
         * @description Add a filter and update datagrid filters
         */
        function addFilter(type, colId, colName, args, removeFilterFn) {
            var filterFn;
            var filterInfo;
            switch (type) {
                case 'contains':
                    filterFn = createContainFilterFn(colId, args.phrase);
                    filterInfo = new Filter(type, colId, colName, true, args, filterFn, removeFilterFn);
                    break;
                case 'exact_filter':
                    filterFn = createExactFilterFn(colId, args.phrase);
                    filterInfo = new Filter(type, colId, colName, true, args, filterFn, removeFilterFn);
                    break;
                case 'invalid_records':
                    filterFn = createEqualFilterFn(colId, args.values);
                    filterInfo = new Filter(type, colId, colName, false, args, filterFn, removeFilterFn);
                    break;
                case 'empty_records':
                    filterFn = createEmptyFilterFn(colId);
                    filterInfo = new Filter(type, colId, colName, false, args, filterFn, removeFilterFn);
                    break;
                case 'valid_records':
                    filterFn = createValidFilterFn(colId, args.values);
                    filterInfo = new Filter(type, colId, colName, false, args, filterFn, removeFilterFn);
                    break;
                case 'inside_range':
                    var existingNumColFilter = _.find(service.filters, function (filter) {
                        return filter.colId === colId && filter.type === 'inside_range';
                    });

                    if (existingNumColFilter) {
                        service.updateFilter(existingNumColFilter, args.interval);
                        return;
                    }
                    else {
                        filterFn = createRangeFilterFn(colId, args.interval);
                        filterInfo = new Filter(type, colId, colName, false, args, filterFn, removeFilterFn);
                    }
                    break;
            }
            DatagridService.addFilter(filterFn);
            service.filters.push(filterInfo);
        }

        /**
         * @ngdoc method
         * @name updateFilter
         * @methodOf data-prep.services.filter.service:FilterService
         * @param {object} oldFilter The filter to update
         * @param {object} newValue The filter update parameters
         * @description Update an existing filter and update datagrid filters
         */
        function updateFilter(oldFilter, newValue) {
            var index = service.filters.indexOf(oldFilter);
            var oldFn = oldFilter.filterFn;

            var newFilterFn;
            var newFilter;
            var newArgs = {};
            var editableFilter;
            switch (oldFilter.type) {
                case 'contains':
                    newArgs.phrase = newValue;
                    newFilterFn = createContainFilterFn(oldFilter.colId, newValue);
                    editableFilter = true;
                    break;
                case 'exact_filter':
                    newArgs.phrase = newValue;
                    newFilterFn = createExactFilterFn(oldFilter.colId, newValue);
                    editableFilter = true;
                    break;
                case 'inside_range':
                    newArgs.interval = newValue;
                    newFilterFn = createRangeFilterFn(oldFilter.colId, newValue);
                    editableFilter = false;
                    break;
            }
            newFilter = new Filter(oldFilter.type, oldFilter.colId, oldFilter.colName, editableFilter, newArgs, newFilterFn, oldFilter.removeFilterFn);

            DatagridService.updateFilter(oldFn, newFilter.filterFn);
            service.filters.splice(index, 1, newFilter);
        }

        /**
         * @ngdoc method
         * @name removeAllFilters
         * @methodOf data-prep.services.filter.service:FilterService
         * @description Remove all the filters and update datagrid filters
         */
        function removeAllFilters() {
            DatagridService.resetFilters();
            service.filters = [];
        }

        /**
         * @ngdoc method
         * @name removeFilter
         * @methodOf data-prep.services.filter.service:FilterService
         * @param {object} filter The filter to delete
         * @description Remove a filter and update datagrid filters
         */
        function removeFilter(filter) {
            var filterIndex = service.filters.indexOf(filter);
            if (filterIndex > -1) {
                DatagridService.removeFilter(filter.filterFn);
                service.filters.splice(filterIndex, 1);
            }
            if(filter.removeFilterFn) {
                filter.removeFilterFn(filter);
            }
        }
    }

    angular.module('data-prep.services.filter')
        .service('FilterService', FilterService);
})();