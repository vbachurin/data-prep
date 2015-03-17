(function () {
    'use strict';

    function DatasetGridService() {
        var self = this;

        self.visible = false;
        self.metadata = null;
        self.data = null;
        self.dataView = new Slick.Data.DataView({inlineFilters: true});
        self.filters = [];

        //------------------------------------------------------------------------------------------------------
        //------------------------------------------------VISIBILITY--------------------------------------------
        //------------------------------------------------------------------------------------------------------
        /**
         * Set visibility flag to true
         */
        self.show = function () {
            self.visible = true;
        };

        /**
         * Set visibility flag to false
         */
        self.hide = function () {
            self.visible = false;
        };

        //------------------------------------------------------------------------------------------------------
        //---------------------------------------------------DATA-----------------------------------------------
        //------------------------------------------------------------------------------------------------------

        /**
         * Insert unique id for each record (needed for DataView)
         * @param records
         */
        var insertUniqueIds = function (records) {
            _.forEach(records, function (item, index) {
                item.tdpId = index;
            });
        };

        /**
         * Set dataview records
         * @param records
         */
        var updateDataviewRecords = function (records) {
            insertUniqueIds(records);

            self.dataView.beginUpdate();
            self.dataView.setItems(records, 'tdpId');
            self.dataView.endUpdate();

        };

        /**
         * Set dataset metadata and records
         * @param metadata - the new metadata
         * @param data - the new data
         */
        self.setDataset = function (metadata, data) {
            updateDataviewRecords(data.records);

            self.metadata = metadata;
            self.data = data;
        };

        /**
         * Update data records
         * @param records - the new records
         */
        self.updateRecords = function (records) {
            updateDataviewRecords(records);
            self.data.records = records;
        };

        //------------------------------------------------------------------------------------------------------
        //------------------------------------------------DATA UTILS--------------------------------------------
        //------------------------------------------------------------------------------------------------------
        /**
         * Return the column ids
         * @param excludeNumeric - if true, the numeric columns won't be returned
         * @param excludeBoolean - if true, the boolean columns won't be returned
         * @returns {*}
         */
        self.getColumns = function(excludeNumeric, excludeBoolean) {
            var numericTypes = ['numeric', 'integer', 'float', 'double'];
            var cols = self.data.columns;

            if(excludeNumeric) {
                cols = _.filter(cols, function (col) {
                    return numericTypes.indexOf(col.type) === -1;
                });
            }
            if(excludeBoolean) {
                cols = _.filter(cols, function(col) {
                    return col.type !== 'boolean';
                });
            }

            return _.map(cols, function (col) {
                return col.id;
            });
        };

        /**
         * Return the column id list that match regexp
         * @param regexp - the regexp that the content must match
         * @returns {Array}
         */
        self.getColumnsContaining = function(regexp, canBeNumeric, canBeBoolean) {
            var results = [];

            var data = self.data.records;
            var potentialColumns = self.getColumns(!canBeNumeric, !canBeBoolean);

            //we loop over the datas while there is data and potential columns that can contains the searched term
            //if a col value for a row contains the term, we add it to result
            var dataIndex = 0;
            while (dataIndex < data.length && potentialColumns.length) {
                var record = data[dataIndex];
                for (var colIndex in potentialColumns) {
                    var colId = potentialColumns[colIndex];
                    if (record[colId].toLowerCase().match(regexp)) {
                        potentialColumns.splice(colIndex, 1);
                        results.push(colId);
                    }
                }

                potentialColumns = _.difference(potentialColumns, results);
                dataIndex++;
            }

            return results;
        };

        /**
         * Return displayed rows index where data[rowId][colId] contains the searched term
         * @param colId - the column id
         * @param term - the term the cell must contain
         * @returns {*}
         */
        self.getRowsContaining = function(colId, term) {
            var result = [];
            for(var i = 0; i < self.dataView.getLength(); ++i) {
                var item = self.dataView.getItem(i);
                if((term === '' && item[colId] === '') || (term !== '' && item[colId].indexOf(term) > -1)) {
                    result.push(i);
                }
            }

            return result;
        };

        //------------------------------------------------------------------------------------------------------
        //-------------------------------------------------FILTERS----------------------------------------------
        //------------------------------------------------------------------------------------------------------
        /**
         * Filter function. It iterates over all filters and return if the provided item fit the predicates
         * @param item - the item to test
         * @param args - object containing the filters predicates
         * @returns {boolean} - true if the item pass all filters
         */
        function filterFn(item, args) {
            for (var i = 0; i < args.filters.length; i++) {
                var filter = args.filters[i];
                if(!filter(item)) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Update filters in dataview
         */
        var updateDataViewFilters = function() {
            self.dataView.beginUpdate();
            self.dataView.setFilterArgs({
                filters: self.filters
            });
            self.dataView.setFilter(filterFn);
            self.dataView.endUpdate();
        };

        /**
         * Add a filter in dataview
         * @param filter - the filter function to add
         */
        self.addFilter = function(filter) {
            self.filters.push(filter);
            updateDataViewFilters();
        };

        /**
         * Remove a filter in dataview
         * @param filter - the filter function to remove
         */
        self.removeFilter = function(filter) {
            var filterIndex = self.filters.indexOf(filter);
            if(filterIndex > -1) {
                self.filters.splice(filterIndex, 1);
                updateDataViewFilters();
            }
        };

        /**
         * Remove all filters from dataview
         */
        self.resetFilters = function() {
            self.filters = [];
            updateDataViewFilters();
        };
    }

    angular.module('data-prep.services.dataset')
        .service('DatasetGridService', DatasetGridService);

})();