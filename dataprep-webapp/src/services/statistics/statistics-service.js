(function() {
    'use strict';

    function StatisticsService(DatasetGridService) {
        var self = this;

        //------------------------------------------------------------------------------------------------------
        //-----------------------------------------------DISTRIBUTIONS------------------------------------------
        //------------------------------------------------------------------------------------------------------
        /**
         * Calculate column value distribution
         * @param columnId
         * @param keyName - distribution key name (default : 'colValue');
         * @param valueName - distribution value name (default : 'frequency')
         * @param keyTransformer - transformer applied to the distribution key
         * @returns [{colValue: string, frequency: integer}} - colValue (or specified) : the grouped value, frequency (or specified) : the nb of time the value appears
         */
        self.getDistribution = function(columnId, keyName, valueName, keyTransformer) {
            keyName = keyName || 'colValue';
            valueName = valueName || 'frequency';

            var records = DatasetGridService.data.records;

            var result = _.chain(records)
                .groupBy(function(item) {
                    return item[columnId];
                })
                .map(function(val, index) {
                    var item = {};
                    item[keyName] = keyTransformer ? keyTransformer(index) : index;
                    item[valueName] = val.length;
                    return item;
                })
                .sortBy(valueName)
                .reverse()
                .value();

            return result;
        };

        /**
         * Calculate range distribution
         * @param column
         * @returns {*}
         */
        self.getRangeDistribution = function (column) {
            //Get all values, casted in number
            var records = DatasetGridService.data.records;
            var values = _.chain(records)
                .map(function(item) {
                    return parseFloat(item[column.id]);
                })
                .filter(function(item) {
                    return !isNaN(item);
                })
                .sortBy(function(value) {return value;})
                .value();

            //determine the step
            var min = values[0];
            var max = values[values.length - 1];
            var step = getStep(max - min);

            //repartition
            return _.chain(values)
                .groupBy(function(item) {
                    return Math.floor(item / step) * step;
                })
                .mapValues(function(value, key) {
                    return {
                        min: parseFloat(key),
                        max: parseFloat(key) + step,
                        key: key + ' - ' + (parseInt(key) + step),
                        frequency: value.length
                    };
                })
                .sortBy('min')
                .value();
        };

        /**
         * Calculate geo distribution, and targeted map
         * @param column
         * @returns {{map: string, data: [{}]}}
         */
        self.getGeoDistribution = function(column) {
            var keyPrefix = 'us-';
            var map = 'countries/us/us-all';

            return {
                map : map,
                data : self.getDistribution(column.id, 'hc-key', 'value', function(key) {return keyPrefix + key.toLowerCase();})
            };
        };

        //------------------------------------------------------------------------------------------------------
        //--------------------------------------------------UTILES----------------------------------------------
        //------------------------------------------------------------------------------------------------------
        /**
         * Get range step
         * @param diff - the diff between the max and min element
         * @returns {int} - the step
         */
        var getStep = function(diff) {
            //if the dumb step is too low, we return this dumb step
            var averagePace = diff / 5;
            if(averagePace < 10) {
                return Math.floor(averagePace);
            }

            //we search for the multiplum of 10 so we can have 5-6 ranges
            var quotient = averagePace;
            var tmpDivider = 1;
            var realDivider = 1;

            while(quotient >= 1) {
                realDivider = tmpDivider;

                tmpDivider *= 10;
                quotient = averagePace / tmpDivider;
            }

            return parseInt(averagePace / realDivider) * realDivider;
        };
    }

    angular.module('data-prep.services.statistics')
        .service('StatisticsService', StatisticsService);
})();