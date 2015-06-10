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
     * @param {object} label Do NOT display label if 'false'. Display it otherwise (by default).
     * @param {object} tooltip Do NOT display tooltip if 'false'. Display it otherwise (by default).
     */
    function TransformSimpleParams() {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: 'components/transformation/params/simple/transformation-simple-params.html',
            scope: {
                editableSelect: '=',
                parameters: '=',
                label: '@',
                tooltip: '@'
            },
            bindToController: true,
            controllerAs: 'simpleParamsCtrl',
            controller: 'TransformSimpleParamsCtrl'
        };
    }

    angular.module('data-prep.transformation-params')
        .directive('transformSimpleParams', TransformSimpleParams);
})();