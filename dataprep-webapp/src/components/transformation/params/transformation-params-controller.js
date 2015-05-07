(function () {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.transformation-params.controller:TransformParamsCtrl
     * @description Transformation parameters controller.
     */
    function TransformParamsCtrl() {
        var vm = this;

        /**
         * @ngdoc method
         * @name adaptParamDefaultValue
         * @methodOf data-prep.transformation-params.controller:TransformParamsCtrl
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
         * @name initParamItemValues
         * @methodOf data-prep.transformation-params.controller:TransformParamsCtrl
         * @param {object} param - the targeted param
         * @description [PRIVATE] Init params values to default
         */
        var initParamItemValues = function (params) {
            _.forEach(params, function (param) {
                if (param.default) {
                    param.default = adaptParamDefaultValue(param);
                    param.value = param.default;
                }
            });
        };

        /**
         * @ngdoc method
         * @name initChoiceItemDefaultValue
         * @methodOf data-prep.transformation-params.controller:TransformParamsCtrl
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
         * @methodOf data-prep.transformation-params.controller:TransformParamsCtrl
         * @description [PRIVATE] Init all param values to default for menu params and choice item params
         */
        var initParamsValues = function () {
            initParamItemValues(vm.transformation.parameters);

            _.forEach(vm.transformation.items, function (choice) {
                initChoiceItemDefaultValue(choice);
                _.forEach(choice.values, function (choiceItem) {
                    initParamItemValues(choiceItem.parameters);
                });
            });
        };

        /**
         * @ngdoc method
         * @name getParams
         * @methodOf data-prep.transformation-params.controller:TransformParamsCtrl
         * @description [PRIVATE] Get item parameters into one object for REST call
         * @returns {object} - the parameters
         */
        var getParams = function () {
            var params = {};
            if (vm.transformation.parameters) {
                _.forEach(vm.transformation.parameters, function (paramItem) {
                    params[paramItem.name] = paramItem.value;
                });
            }

            return params;
        };

        /**
         * @ngdoc method
         * @name getChoiceParams
         * @methodOf data-prep.transformation-params.controller:TransformParamsCtrl
         * @description [PRIVATE] Get item choice and choice parameters into one object for REST call
         * @returns {object} - the parameters
         */
        var getChoiceParams = function () {
            var params = {};
            _.forEach(vm.transformation.items, function (item) {
                var selectedChoice = item.selectedValue;
                params[item.name] = selectedChoice.name;

                if (selectedChoice.parameters) {
                    _.forEach(selectedChoice.parameters, function (choiceParamItem) {
                        params[choiceParamItem.name] = choiceParamItem.value;
                    });
                }
            });

            return params;
        };

        /**
         * @ngdoc method
         * @name gatherParams
         * @methodOf data-prep.transformation-params.controller:TransformParamsCtrl
         * @description [PRIVATE] Gather params into one unique object
         * @returns {object} The entire parameter values
         */
        var gatherParams = function() {
            var params = getParams();
            var choiceParams = getChoiceParams();

            return _.merge(params, choiceParams);
        };

        /**
         * @ngdoc method
         * @name transformWithParam
         * @methodOf data-prep.transformation-params.controller:TransformParamsCtrl
         * @description Gather params and perform a transformation on the column
         */
        vm.transformWithParam = function () {
            var transformationParams = gatherParams();
            vm.onSubmit({params: transformationParams});
        };

        /**
         * @ngdoc method
         * @name submitHoverOn
         * @methodOf data-prep.transformation-params.controller:TransformParamsCtrl
         * @description Gather params and perform the submit mouseenter action
         */
        vm.submitHoverOn = function() {
            if(vm.onSubmitHoverOn) {
                var params = gatherParams();
                vm.onSubmitHoverOn({params: params});
            }
        };

        /**
         * @ngdoc method
         * @name submitHoverOff
         * @methodOf data-prep.transformation-params.controller:TransformParamsCtrl
         * @description Gather params and perform the submit mouseleave action
         */
        vm.submitHoverOff = function() {
            if(vm.onSubmitHoverOff) {
                var params = gatherParams();
                vm.onSubmitHoverOff({params: params});
            }
        };

        initParamsValues();
    }

    angular.module('data-prep.transformation-params')
        .controller('TransformParamsCtrl', TransformParamsCtrl);
})();