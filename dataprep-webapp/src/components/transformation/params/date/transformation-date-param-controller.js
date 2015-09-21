(function () {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.transformation-params.controller:TransformDateParamCtrl
     * @description Transformation date parameter controller.
     * @requires data-prep.services.utils.service:ConverterService
     */
    function TransformDateParamCtrl(ConverterService) {
        var vm = this;

        /**
         * @ngdoc method
         * @name initParamValues
         * @methodOf data-prep.transformation-params.controller:TransformDateParamCtrl
         * @description [PRIVATE] Init date parameter values to default
         */
        var initParamValue = function () {

            var param =vm.parameter;

            if(param.initialValue) {
                param.initialValue = ConverterService.adaptValue(param.type, param.initialValue);
            }

            if (param.value) {
                param.value = ConverterService.adaptValue(param.type, param.value);
            }

            else if (param.default) {
                param.default = ConverterService.adaptValue(param.type, param.default);
                param.value = param.default;
            }
        };

        /**
         * @ngdoc method
         * @name initInputType
         * @methodOf data-prep.transformation-params.controller:TransformDateParamCtrl
         * @description [PRIVATE] Init params input type, depending on param type
         */
        var initInputType = function() {
            vm.parameter.inputType = ConverterService.toInputType(vm.parameter.type);
        };

        initParamValue();
        initInputType();
    }

    angular.module('data-prep.transformation-params')
        .controller('TransformDateParamCtrl', TransformDateParamCtrl);
})();