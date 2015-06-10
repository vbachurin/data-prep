(function() {
    'use strict';

    function StatisticsService(DatagridService) {
        var self = this;

        self.processOnColumnType = function(column){
            if(column.type === 'numeric' || column.type === 'integer' || column.type === 'float' || column.type === 'double'){
                self.rangeDistribution = true;
                self.data = [
                    {"name":"Azertuire  yrehzL","value":"4"},
                    {"name":"AK","value":"6"},
                    {"name":"AZ","value":"6"},
                    {"name":"AR","value":"2"},
                    {"name":"CA","value":"36"},
                ];
                return 'range';
            }else if(column.type === 'string'){
                self.distribution = true;
                self.data = [{"name":"Azertuire  yrehzL","value":"4"},{"name":"AK","value":"6"},{"name":"AZ","value":"6"},{"name":"AR","value":"2"},{"name":"CA","value":"36"},{"name":"CO","value":"5"},{"name":"CT","value":"35"},{"name":"DE","value":"8"},{"name":"DC","value":"5"},{"name":"FL","value":"1"},{"name":"GA","value":"98"},{"name":"HI","value":"1"},{"name":"ID","value":"15"},{"name":"IL","value":"12"},{"name":"IN","value":"64"},{"name":"IA","value":"30"},{"name":"KS","value":"28"},{"name":"KY","value":"43"},{"name":"LA","value":"44"},{"name":"ME","value":"13"},{"name":"MD","value":"56"},{"name":"MA","value":"65"},{"name":"Mjrehe i zeorife zeiurhI","value":"99"}];
                return 'string';
            }else if(column.type === 'boolean'){
                self.pieDistribution = true;
                self.data = [
                    {"name":"true","value":"2"},
                    {"name":"false","value":"36"},
                ];
                return 'boolean';
            }else if(column.name.toLowerCase() === 'state'){
                self.stateDistribution = true;
                return 'state';
            }else {
                return 'uNkNoWn';
            }
        };
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

            var records = DatagridService.data.records;

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
            var records = DatagridService.data.records;
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