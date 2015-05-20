(function () {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.transformation-params.directive:TransformSimpleParams
     * @description This directive display a transformation simple parameters form
     * @restrict E
     * @usage
     <transform-params
             transformation="transformation"
             on-submit="callback()">
        <transform-simple-params
            parameters="parameters">
        </transform-simple-params>
     </transform-params>
     * @param {object} parameters The transformation simple parameters
     */
    function TransformSimpleParams() {
        return {
            restrict: 'E',
            templateUrl: 'components/transformation/params/simple/transformation-simple-params.html',
            replace: true,
            scope: {
                parameters: '='
            },
            bindToController: true,
            controllerAs: 'simpleParamsCtrl',
            controller: 'TransformSimpleParamsCtrl'
        };
    }

    angular.module('data-prep.transformation-params')
        .directive('transformSimpleParams', TransformSimpleParams);
})();