(function () {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.transformation-params.controller:TransformChoiceParamsCtrl
     * @description Transformation choices controller.
     */
    function TransformChoiceParamsCtrl() {
        var vm = this;

        /**
         * @ngdoc method
         * @name initChoiceItemDefaultValue
         * @methodOf data-prep.transformation-params.controller:TransformChoiceParamsCtrl
         * @param {object} choice - the choice item
         * @description [PRIVATE] Save a choice item selected value. The value defines the default value. If no default defined, select the first element
         */
        var initChoiceItemDefaultValue = function (choice) {
            var defaultValue = _.find(choice.values, function (value) {
                return value.default;
            });

            choice.selectedValue = defaultValue || choice.values[0];
        };

        /**
         * @ngdoc method
         * @name initParamsValues
         * @methodOf data-prep.transformation-params.controller:TransformChoiceParamsCtrl
         * @description [PRIVATE] Init all choice element default value
         */
        var initParamsValues = function () {
            _.forEach(vm.choices, function (choice) {
                initChoiceItemDefaultValue(choice);
            });
        };

        initParamsValues();
    }

    angular.module('data-prep.transformation-params')
        .controller('TransformChoiceParamsCtrl', TransformChoiceParamsCtrl);
})();