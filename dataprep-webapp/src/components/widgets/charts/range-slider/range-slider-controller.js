(function () {
    'use strict';

    /**
     * @ngdoc controller
     * @name talend.widget.controller:RangeSliderCtrl
     * @description The rangeSlider controller
     */
    function RangeSliderCtrl() {
        var vm = this;

        /**
         * @ngdoc method
         * @name toNumber
         * @propertyOf talend.widget.controller:RangeSliderCtrl
         * @description check if the entered value is a number
         * @param {string} value The value to transform
         */
        vm.toNumber = function toNumber(value) {
            value = value.trim();
            if (/^[-+]?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?$/.test(value)) {
                return Number(value);
            }
            return null;
        };

        /**
         * @ngdoc method
         * @name checkCommaExistence
         * @propertyOf talend.widget.controller:RangeSliderCtrl
         * @description check if the entered values contain a comma
         */
        vm.checkCommaExistence = function checkCommaExistence(minMaxStr) {
            return minMaxStr.indexOf(',') > -1;
        };

        /**
         * @ngdoc method
         * @name decimalPlaces
         * @propertyOf talend.widget.controller:RangeSliderCtrl
         * @description Return the decimal index
         */
        vm.decimalPlaces = function decimalPlaces(num) {
            var match = ('' + num).match(/(?:\.(\d+))?(?:[eE]([+-]?\d+))?$/);
            return Math.max(
                0,
                // Number of digits right of decimal point.
                (match[1] ? match[1].length : 0) -
                // Adjust for scientific notation.
                (match[2] ? +match[2] : 0));
        };

        /**
         * @ngdoc method
         * @name adaptRangeValues
         * @propertyOf talend.widget.controller:RangeSliderCtrl
         * @description Adapt the entered values to respect the rules :
         * <ul>
         *     <li>The entered min value < the entered max value</li>
         *     <li>The entered min value is within the range defined by 'minimum' and 'maximum'</li>
         * </ul>
         * @param enteredMin The min input value
         * @param enteredMax The max input value
         * @param minimum The minimum value in the range
         * @param maximum The maximum value in the range
         * @param nbDecimals The number precision
         */
        vm.adaptRangeValues = function adaptRangeValues(enteredMin, enteredMax, minimum, maximum, nbDecimals) {
            //switch entered values if necessary
            if (enteredMin > enteredMax) {
                var _aux = enteredMin;
                enteredMin = enteredMax;
                enteredMax = _aux;
            }

            //maximum limits
            if (enteredMax > maximum || enteredMax < minimum) {
                enteredMax = maximum;
            }

            //minimum limits
            if (enteredMin > maximum || enteredMin < minimum) {
                enteredMin = minimum;
            }

            if (enteredMin === enteredMax) {
                var exp = '1e-' + (nbDecimals + 1);
                return [enteredMin, enteredMin + Number(exp)];
            }
            else {
                return [enteredMin, enteredMax];
            }

        };
    }

    angular.module('talend.widget')
        .controller('RangeSliderCtrl', RangeSliderCtrl);
})();