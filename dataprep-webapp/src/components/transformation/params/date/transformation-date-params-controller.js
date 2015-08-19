(function () {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.transformation-params.controller:TransformDateParamsCtrl
     * @description Transformation parameters controller.
     * @requires data-prep.services.utils.service:ConverterService
     */
    function TransformDateParamsCtrl(ConverterService) {
        var vm = this;



        /**
         * @ngdoc method
         * @name initParamsValues
         * @methodOf data-prep.transformation-params.controller:TransformDateParamsCtrl
         * @description [PRIVATE] Init date params values to default
         */
        var initParamsValues = function () {
            _.forEach(vm.parameters, function (param) {

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
            });
        };

        /**
         * @ngdoc method
         * @name initInputTypes
         * @methodOf data-prep.transformation-params.controller:TransformDateParamsCtrl
         * @description [PRIVATE] Init params input type, depending on param type
         */
        var initInputTypes = function() {
            _.forEach(vm.parameters, function(param) {
                param.inputType = ConverterService.toInputType(param.type);
            });
        };

        initParamsValues();
        initInputTypes();
    }

    angular.module('data-prep.transformation-params')
        .controller('TransformDateParamsCtrl', TransformDateParamsCtrl);
})();