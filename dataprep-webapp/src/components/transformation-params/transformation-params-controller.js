(function () {
    'use strict';

    function TransformParamsCtrl() {
        var vm = this;

        /**
         * Adapt param default value to the requested type
         * @param param - the targeted param
         * @returns {*} - the adapted value
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
         * Init params values to default
         * @param params
         */
        var initParamItemValues = function (params) {
            _.forEach(params, function (param) {
                if (param.default) {
                    param.value = adaptParamDefaultValue(param);
                }
            });
        };

        /**
         * Init all param values to default for menu params and choice item params
         */
        var initParamsValues = function () {
            initParamItemValues(vm.menu.parameters);

            _.forEach(vm.menu.items, function (choice) {
                _.forEach(choice.values, function (choiceItem) {
                    initParamItemValues(choiceItem.parameters);
                });
            });
        };

        /**
         * Get item parameters
         * @returns {object}
         */
        var getParams = function () {
            var params = {};
            if (vm.menu.parameters) {
                _.forEach(vm.menu.parameters, function (paramItem) {
                    params[paramItem.name] = paramItem.value;
                });
            }

            return params;
        };

        /**
         * Get item choice and choice parameters
         * @returns {object}
         */
        var getChoiceParams = function () {
            var params = {};
            _.forEach(vm.menu.items, function (item) {
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
         * Gather params and perform a transformation on the column
         */
        vm.transformWithParam = function () {
            var params = getParams();
            var choiceParams = getChoiceParams();

            var transformationParams = _.merge(params, choiceParams);
            vm.onSubmit({params: transformationParams});
        };

        initParamsValues();
    }

    angular.module('data-prep.transformation-params')
        .controller('TransformParamsCtrl', TransformParamsCtrl);
})();