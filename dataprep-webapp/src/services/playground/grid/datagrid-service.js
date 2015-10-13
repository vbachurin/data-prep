(function () {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.playground.service:DatagridService
     * @description Datagrid service. This service holds the datagrid (SlickGrid) view and the (SlickGrid) filters<br/>
     * <b style="color: red;">WARNING : do NOT use this service directly for FILTERS.
     * {@link data-prep.services.filter.service:FilterService FilterService} must be the only entry point for datagrid filters</b>
     * @requires data-prep.services.utils.service:ConverterService
     * @requires data-prep.state.service:StateService
     */
    function DatagridService(ConverterService, StateService, state, $timeout) {
        var DELETE = 'DELETE';
        var REPLACE = 'REPLACE';
        var INSERT = 'INSERT';

        var self = this;

        /**
         * @ngdoc property
         * @name metadata
         * @propertyOf data-prep.services.playground.service:DatagridService
         * @description the loaded metadata
         * @type {Object}
         */
        self.metadata = null;

        /**
         * @ngdoc prope??????????????????
         * @name dataVi??????????????????
         * @propertyOf ??????????????????
         * @description??????????????????
         * @type {Object}
         */
        StateService.setDataView(new Slick.Data.DataView({inlineFilters: false}));

        /**
         * @ngdoc property
         * @name filters
         * @propertyOf data-prep.services.playground.service:DatagridService
         * @description the filters applied to the dataview
         * @type {function[]}
         */
        self.filters = [];

        //------------------------------------------------------------------------------------------------------
        //---------------------------------------------------DATA-----------------------------------------------
        //------------------------------------------------------------------------------------------------------

        /**
         * @ngdoc method
         * @name updateDataviewRecords
         * @methodOf data-prep.services.playground.service:DatagridService
         * @param {Object[]} records - the records to insert
         * @description [PRIVATE] Set dataview records
         */
        //var updateDataviewRecords = function (records) {
        //    self.dataView.beginUpdate();
        //    self.dataView.setItems(records, 'tdpId');
        //    self.dataView.endUpdate();
        //};

        /**
         * @ngdoc method
         * @name setDataset
         * @methodOf data-prep.services.playground.service:DatagridService
         * @param {Object} metadata - the new metadata to load
         * @param {Object} data - the new data to load
         * @description Set dataview records and metadata to the datagrid
         */
        self.setDataset = function (metadata, data) {
            self.metadata = metadata;
            StateService.setCurrentData(data);
            self.focusedColumn = null;
            //updateDataviewRecords(data.records);
        };

        /**
         * @ngdoc method
         * @name getLastNewColumnId
         * @methodOf data-prep.services.playground.service:DatagridService
         * @param {object} columns The new columns
         * @description Get the last new created column
         */
        function getLastNewColumnId(columns) {
            var ancientColumnsIds = _.map(state.playground.data.columns, 'id');
            var newColumnsIds = _.map(columns, 'id');
            var diffIds = _.difference(newColumnsIds, ancientColumnsIds);

            return diffIds[diffIds.length - 1];
        }

        /**
         * @ngdoc method
         * @name updateData
         * @methodOf data-prep.services.playground.service:DatagridService
         * @param {Object} data - the new data (columns and records)
         * @description Update the data in the datagrid
         */
        self.updateData = function (data) {
            if (state.playground.data.columns.length < data.columns.length) {
                self.focusedColumn = getLastNewColumnId(data.columns);
            }
            StateService.setCurrentData(data);
        };

        //------------------------------------------------------------------------------------------------------
        //--------------------------------------------------PREVIEW---------------------------------------------
        //------------------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name execute
         * @methodOf data-prep.services.playground.service:DatagridService
         * @param {Object} executor The infos to apply on the dataset
         * @description Update the data in the datagrid with a set of instructions and the column list to apply.
         * This allows to update the dataset, with limited SlickGrid computation, for more performant operations than
         * setItems which compute everything on the whole dataset.
         */
        self.execute = function execute(executor) {
            if (!executor) {
                return;
            }

            var revertInstructions = [];

            state.playground.dataView.beginUpdate();
            _.forEach(executor.instructions, function (step) {
                switch (step.type) {
                    case INSERT:
                        state.playground.dataView.insertItem(step.index, step.row);
                        revertInstructions.push({
                            type: DELETE,
                            row: step.row
                        });
                        break;
                    case DELETE:
                        var index = state.playground.dataView.getIdxById(step.row.tdpId);
                        state.playground.dataView.deleteItem(step.row.tdpId);
                        revertInstructions.push({
                            type: INSERT,
                            row: step.row,
                            index: index
                        });
                        break;
                    case REPLACE:
                        var originalRow = state.playground.dataView.getItemById(step.row.tdpId);
                        state.playground.dataView.updateItem(step.row.tdpId, step.row);
                        revertInstructions.push({
                            type: REPLACE,
                            row: originalRow
                        });
                        break;
                }
            });
            state.playground.dataView.endUpdate();

            var reverter = {
                instructions: revertInstructions,
                preview: state.playground.data.preview,
                columns: state.playground.data.columns
            };

            if (state.playground.data.columns.length < executor.columns.length) {
                self.focusedColumn = getLastNewColumnId(executor.columns);
            }

            StateService.setCurrentData({
                    columns: executor.columns,
                    records: state.playground.data.records,
                    preview: executor.preview
                });

            return reverter;
        };

        /**
         * @ngdoc method
         * @name previewDataExecutor
         * @methodOf data-prep.services.playground.service:DatagridService
         * @param {Object} data The new preview data to insert
         * @description Create an executor that reflect the provided preview data, in order to update the current dataset
         */
        self.previewDataExecutor = function previewDataExecutor(data) {
            var executor = {
                columns: data.columns,
                instructions: [],
                preview: true
            };

            var nextInsertionIndex = state.playground.dataView.getIdxById(data.records[0].tdpId);
            _.forEach(data.records, function (row) {
                if (row.__tdpRowDiff || row.__tdpDiff) {
                    if (row.__tdpRowDiff === 'new') {
                        executor.instructions.push({
                            type: INSERT,
                            row: row,
                            index: nextInsertionIndex
                        });
                    }
                    else {
                        executor.instructions.push({
                            type: REPLACE,
                            row: row
                        });
                    }
                }
                nextInsertionIndex++;
            });

            return executor;
        };

        //------------------------------------------------------------------------------------------------------
        //------------------------------------------------DATA UTILS--------------------------------------------
        //------------------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name getColumns
         * @methodOf data-prep.services.playground.service:DatagridService
         * @param {boolean} excludeNumeric - filter the numeric columns
         * @param {boolean} excludeBoolean - filter the boolean columns
         * @description Filter the column ids
         * @returns {Object[]} - the column list that match the desired filters (id & name)
         */
        self.getColumns = function (excludeNumeric, excludeBoolean) {
            var cols = state.playground.data.columns;

            if (excludeNumeric) {
                cols = _.filter(cols, function (col) {
                    var simplifiedType = ConverterService.simplifyType(col.type);
                    return simplifiedType !== 'integer' && simplifiedType !== 'decimal';
                });
            }
            if (excludeBoolean) {
                cols = _.filter(cols, function (col) {
                    return col.type !== 'boolean';
                });
            }

            return _.map(cols, function (col) {
                return {'id': col.id, 'name': col.name};
            });
        };

        /**
         * @ngdoc method
         * @name getColumnsContaining
         * @methodOf data-prep.services.playground.service:DatagridService
         * @param {string} regexp - the regexp
         * @param {boolean} canBeNumeric - filter the numeric columns
         * @param {boolean} canBeBoolean - filter the boolean columns
         * @description Return the column id list that has a value that match the regexp
         * @returns {Object[]} - the column list that contains a value that match the regexp (col.id & col.name)
         */
        self.getColumnsContaining = function (regexp, canBeNumeric, canBeBoolean) {
            var results = [];

            var data = state.playground.data.records;
            var potentialColumns = self.getColumns(!canBeNumeric, !canBeBoolean);

            //we loop over data while there is data and potential columns that can contains the searched term
            //if a col value for a row contains the term, we add it to result
            var dataIndex = 0;
            while (dataIndex < data.length && potentialColumns.length) {
                var record = data[dataIndex];
                for (var colIndex in potentialColumns) {
                    var col = potentialColumns[colIndex];
                    if (record[col.id].toLowerCase().match(regexp)) {
                        potentialColumns.splice(colIndex, 1);
                        results.push(col);
                    }
                }

                potentialColumns = _.difference(potentialColumns, results);
                dataIndex++;
            }

            return results;
        };

        /**
         * @ngdoc method
         * @name getSameContentConfig
         * @methodOf data-prep.services.playground.service:DatagridService
         * @param {string} colId The column index
         * @param {string} term The cell content to search
         * @param {string} cssClass The css class to apply
         * @description Return displayed rows index where data[rowId][colId] contains the searched term
         * @returns {Object} The SlickGrid css config for each column with the provided content
         */
        self.getSameContentConfig = function (colId, term, cssClass) {
            var config = {};
            for (var i = 0; i < state.playground.dataView.getLength(); ++i) {
                var item = state.playground.dataView.getItem(i);
                if (term === item[colId]) {
                    config[i] = {};
                    config[i][colId] = cssClass;
                }
            }

            return config;
        };

        /**
         * @ngdoc method
         * @name getNumericColumns
         * @methodOf data-prep.services.playground.service:DatagridService
         * @param {object} columnToSkip The column to skip
         * @description Filter numeric columns
         * @returns {array} The numeric columns
         */
        self.getNumericColumns = function getNumericColumns(columnToSkip) {
            return _.chain(state.playground.data.columns)
                .filter(function (column) {
                    return !columnToSkip || column.id !== columnToSkip.id;
                })
                .filter(function (column) {
                    var simpleType = ConverterService.simplifyType(column.type);
                    return simpleType === 'integer' || simpleType === 'decimal';
                })
                .value();
        };

        //------------------------------------------------------------------------------------------------------
        //-------------------------------------------------FILTERS----------------------------------------------
        //------------------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name filterFn
         * @methodOf data-prep.services.playground.service:DatagridService
         * @param {object} item - the item to test
         * @param {object} args - object containing the filters predicates
         * @description [PRIVATE] Filter function. It iterates over all filters and return if the provided item fit the predicates
         * @returns {boolean} - true if the item pass all the filters
         */
        function filterFn(item, args) {
            //init filters with actual data
            var initializedFilters = _.map(args.filters, function (filter) {
                return filter(state.playground.data);
            });
            //execute each filter on the value
            for (var i = 0; i < initializedFilters.length; i++) {
                var filter = initializedFilters[i];
                if (!filter(item)) {
                    return false;
                }
            }
            return true;
        }

        ///**
        // * @ngdoc method
        // * @name getAllFiltersFn
        // * @methodOf data-prep.services.playground.service:DatagridService
        // * @description [PRIVATE] Create a closure that contains the active filters to execute
        // * @returns {function} The filters closure
        // */
        //self.getAllFiltersFn = function () {
        //    return function (item) {
        //        return filterFn(item, self);
        //    };
        //};

        /**
         * @ngdoc method
         * @name updateDataViewFilters
         * @methodOf data-prep.services.playground.service:DatagridService
         * @description [PRIVATE] Update filters in dataview
         */
        var updateDataViewFilters = function () {
            state.playground.dataView.beginUpdate();
            state.playground.dataView.setFilterArgs({
                filters: self.filters
            });
            state.playground.dataView.setFilter(filterFn);
            state.playground.dataView.endUpdate();
            // $timeout due to the latency while changing a dataset
            $timeout(StateService.updateShownLinesLength);
        };

        /**
         * @ngdoc method
         * @name addFilter
         * @methodOf data-prep.services.playground.service:DatagridService
         * @description Add a filter in dataview
         * @param {object} filter - the filter function to add
         */
        self.addFilter = function (filter) {
            self.filters.push(filter);
            updateDataViewFilters();
        };

        /**
         * @ngdoc method
         * @name updateFilter
         * @methodOf data-prep.services.playground.service:DatagridService
         * @description Update a filter in dataview
         * @param {object} oldFilter - the filter function to replace
         * @param {object} newFilter - the new filter function
         */
        self.updateFilter = function (oldFilter, newFilter) {
            var index = self.filters.indexOf(oldFilter);
            self.filters.splice(index, 1, newFilter);
            updateDataViewFilters();
        };

        /**
         * @ngdoc method
         * @name removeFilter
         * @methodOf data-prep.services.playground.service:DatagridService
         * @description Remove a filter in dataview
         * @param {object} filter - the filter function to remove
         */
        self.removeFilter = function (filter) {
            var filterIndex = self.filters.indexOf(filter);
            if (filterIndex > -1) {
                self.filters.splice(filterIndex, 1);
                updateDataViewFilters();
            }
        };

        /**
         * @ngdoc method
         * @name resetFilters
         * @methodOf data-prep.services.playground.service:DatagridService
         * @description Remove all filters from dataview
         */
        self.resetFilters = function () {
            self.filters = [];
            updateDataViewFilters();
        };
    }

    angular.module('data-prep.services.playground')
        .service('DatagridService', DatagridService);

})();