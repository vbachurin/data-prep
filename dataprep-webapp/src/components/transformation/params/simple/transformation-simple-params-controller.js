(function () {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.transformation-params.controller:TransformSimpleParamsCtrl
     * @description Transformation parameters controller.
     */
    function TransformSimpleParamsCtrl() {
        var vm = this;

        /**
         * @ngdoc method
         * @name adaptParamDefaultValue
         * @methodOf data-prep.transformation-params.controller:TransformSimpleParamsCtrl
         * @param {object} param - the targeted param
         * @description [PRIVATE] Adapt params default value to the requested type
         */
        var adaptParamDefaultValue = function (param) {
            switch (param.type) {
                case 'numeric':
                case 'integer':
                case 'double':
                case 'float':
                    return parseFloat(param.default) || 0;
                default :
                    return param.default;
            }
        };

        /**
         * @ngdoc method
         * @name initParamsValues
         * @methodOf data-prep.transformation-params.controller:TransformSimpleParamsCtrl
         * @description [PRIVATE] Init simple params values to default
         */
        var initParamsValues = function () {
            _.forEach(vm.parameters, function (param) {
                if (param.default) {
                    param.default = adaptParamDefaultValue(param);
                    param.value = param.default;
                }
            });
        };

        initParamsValues();
    }

    angular.module('data-prep.transformation-params')
        .controller('TransformSimpleParamsCtrl', TransformSimpleParamsCtrl);
})();