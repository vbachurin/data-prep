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
            resetFilter();
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

        /**
         * Return the column ids
         * @param excludeNumeric - if true, the numeric columns won't be returned
         * @returns {*}
         */
        self.getColumns = function(excludeNumeric) {
            var numericTypes = ['numeric', 'integer', 'float', 'double'];
            var cols;
            if(!excludeNumeric) {
                cols = self.data.columns;
            }
            else {
                cols = _.filter(self.data.columns, function(col) {
                    return numericTypes.indexOf(col.type) === -1;
                });
            }

            return _.map(cols, function (col) {
                return col.id;
            });
        };

        //------------------------------------------------------------------------------------------------------
        //-------------------------------------------------FILTERS---------------------------------------------
        //------------------------------------------------------------------------------------------------------
        function filterFn(item, args) {
            for (var i = 0; i < args.filters.length; i++) {
                var filter = args.filters[i];
                if(!filter(item)) {
                    return false;
                }
            }
            return true;
        }

        self.addContainFilter = function(colName, phrase) {
            self.filters.push(function(item) {
                return item[colName].indexOf(phrase) > -1;
            });

            self.dataView.beginUpdate();
            self.dataView.setFilterArgs({
                filters: self.filters
            });
            self.dataView.setFilter(filterFn);
            self.dataView.endUpdate();
        };

        var resetFilter = function() {
            self.filters = [];
            self.dataView.setFilterArgs({
                filters: self.filters
            });
        };
    }

    angular.module('data-prep.services.dataset')
        .service('DatasetGridService', DatasetGridService);

})();