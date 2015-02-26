(function () {
    'use strict';

    function DatasetTransformMenuCtrl($rootScope, DatasetGridService, TransformationService) {
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
         * Menu selected. 3 choices :
         * 1 - divider : no action
         * 2 - no parameter and no choice is required : transformation call
         * 3 - parameter or choice required : show modal
         */
        vm.select = function () {
            if (vm.menu.isDivider) {
                return;
            }

            if (vm.menu.parameters || vm.menu.items) {
                initParamsValues();
                vm.showModal = true;
            }
            else {
                vm.transform();
            }
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

            vm.transform(transformationParams);
            vm.showModal = false;
        };

        /**
         * Perform a transformation on the column
         */
        vm.transform = function (params) {
            $rootScope.$emit('talend.loading.start');

            params = params || {};
            /*jshint camelcase: false */
            params.column_name = vm.column.id;

            TransformationService.transform(vm.metadata.id, vm.menu.name, params)
                .then(function (response) {
                    DatasetGridService.updateRecords(response.data.records);
                })
                .finally(function () {
                    $rootScope.$emit('talend.loading.stop');
                });
        };
    }

    angular.module('data-prep-dataset')
        .controller('DatasetTransformMenuCtrl', DatasetTransformMenuCtrl);
})();