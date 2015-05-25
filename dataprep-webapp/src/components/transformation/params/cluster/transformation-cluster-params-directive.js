(function () {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.transformation-params.directive:TransformClusterParams
     * @description This directive display a transformation cluster parameters form
     * @restrict E
     * @usage
     <transform-params
             transformation="transformation"
             on-submit="callback()">
        <transform-cluster-params
            details="parameters">
        </transform-cluster-params>
     </transform-params>
     * @param {object} details The transformation cluster parameters details
     */
    function TransformClusterParams() {
        return {
            restrict: 'E',
            templateUrl: 'components/transformation/params/cluster/transformation-cluster-params.html',
            replace: true,
            scope: {
                details: '='
            },
            bindToController: true,
            controllerAs: 'clusterParamsCtrl',
            controller: 'TransformClusterParamsCtrl'
        };
    }

    angular.module('data-prep.transformation-params')
        .directive('transformClusterParams', TransformClusterParams);
})();