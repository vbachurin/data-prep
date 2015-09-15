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
         * @name getParams
         * @methodOf data-prep.transformation-params.controller:TransformParamsCtrl
         * @description [PRIVATE] Get item parameters into one object for REST call
         * @returns {object} - the parameters
         */
        var getParams = function () {
            var params = {};
            if (vm.transformation.parameters) {
                _.forEach(vm.transformation.parameters, function (paramItem) {
                    params[paramItem.name] = paramItem.value? paramItem.value : paramItem.default;
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
         * @name getClusterParams
         * @methodOf data-prep.transformation-params.controller:TransformParamsCtrl
         * @description [PRIVATE] Get cluster choice and choice parameters into one object for REST call
         * @returns {object} - the parameters
         */
        var getClusterParams = function() {
            var params = {};
            if (vm.transformation.cluster) {
                _.chain(vm.transformation.cluster.clusters)
                    .filter('active')
                    .forEach(function (cluster) {
                        var replaceValue = cluster.replace.value;
                        _.forEach(cluster.parameters, function(param) {
                            if(param.value) {
                                params[param.name] = replaceValue;
                            }
                        });
                    })
                    .value();
            }

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
            var clusterParams = getClusterParams();
            return _.merge(_.merge(params, choiceParams), clusterParams);
            //var dateParams = getDateParams();
            //return _.merge(_.merge(_.merge(params, choiceParams), clusterParams),dateParams);
        };

        /**
         * @ngdoc method
         * @name transformWithParam
         * @methodOf data-prep.transformation-params.controller:TransformParamsCtrl
         * @param {boolean} invalid validity of the form
         * @description Gather params and perform a transformation on the column if the form is valid
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
            var params = gatherParams();
            vm.onSubmitHoverOn({params: params});
        };

        /**
         * @ngdoc method
         * @name submitHoverOff
         * @methodOf data-prep.transformation-params.controller:TransformParamsCtrl
         * @description Gather params and perform the submit mouseleave action
         */
        vm.submitHoverOff = function() {
            var params = gatherParams();
            vm.onSubmitHoverOff({params: params});
        };
    }

    angular.module('data-prep.transformation-params')
        .controller('TransformParamsCtrl', TransformParamsCtrl);
})();