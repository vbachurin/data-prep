(function () {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.transformation-params.controller:TransformClusterParamsCtrl
     * @description Transformation cluster parameters controller.
     */
    function TransformClusterParamsCtrl() {
        var vm = this;

        /**
         * @ngdoc property
         * @name allCheckboxState
         * @propertyOf data-prep.transformation-params.controller:TransformClusterParamsCtrl
         * @description The global checkbox state
         */
        vm.allCheckboxState = true;

        /**
         * @ngdoc method
         * @name refreshClusterState
         * @methodOf data-prep.transformation-params.controller:TransformClusterParamsCtrl
         * @description Refresh all cluster "active" flag to the state of the global activation checkbox
         */
        vm.refreshClusterState = function() {
            _.forEach(vm.details.clusters, function(cluster) {
                cluster.active = vm.allCheckboxState;
            });
        };

        /**
         * @ngdoc method
         * @name initClusterState
         * @methodOf data-prep.transformation-params.controller:TransformClusterParamsCtrl
         * @description Initialize all cluster "active" flag
         */
        vm.initClusterState = function() {
            _.forEach(vm.details.clusters, function(cluster) {
                cluster.active = cluster.initialActive;
            });
        };

        /**
         * @ngdoc method
         * @name getParams
         * @methodOf data-prep.transformation-params.controller:TransformClusterParamsCtrl
         * @description Refresh the global activation checkbox
         */
        vm.refreshToggleCheckbox = function() {
            var inactiveCluster = _.find(vm.details.clusters, function(cluster) {
                return !cluster.active;
            });
            vm.allCheckboxState = !inactiveCluster;
        };

        /**
         * @ngdoc method
         * @name initParamsValues
         * @methodOf data-prep.transformation-params.controller:TransformClusterParamsCtrl
         * @description Initialize parameters values and checkbox state if needed
         */
        var initParamsValues = function() {
            _.forEach(vm.details.clusters, function(cluster) {
                _.forEach(cluster.parameters, function(param) {
                    param.default = true;
                });
            });


            var clustersInitialized = false;
            for(var i=0; i<vm.details.clusters.length; i++) {
                if (typeof vm.details.clusters[i].initialActive !== 'undefined') {
                    clustersInitialized = true;
                    break;
                }
            }
            if(!clustersInitialized) {
                vm.refreshClusterState();
            } else {
                vm.initClusterState();
            }
        };

        /**
         * @ngdoc method
         * @name initInputTypes
         * @methodOf data-prep.transformation-params.controller:TransformSimpleParamsCtrl
         * @description [PRIVATE] Init params input type, depending on param type
         */
        var initReplaceList = function() {
            _.forEach(vm.details.clusters, function(cluster) {
                cluster.replace.list = _.map(cluster.parameters, 'name');
            });
        };

        initParamsValues();
        initReplaceList();
    }

    angular.module('data-prep.transformation-params')
        .controller('TransformClusterParamsCtrl', TransformClusterParamsCtrl);
})();