(function () {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.transformation-params.directive:TransformSimpleParams
     * @description This directive display a transformation date parameters form
     * @restrict E
     * @usage
     <transform-params
             transformation="transformation"
             on-submit="callback()">
        <transform-date-params
            parameters="parameters">
        </transform-date-params>
     </transform-params>
     * @param {object} parameters The transformation date parameters
     * @param {object} label Do NOT display label if 'false'. Display it otherwise (by default).
     * @param {object} tooltip Do NOT display tooltip if 'false'. Display it otherwise (by default).
     */
    function TransformDateParams() {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: 'components/transformation/params/date/transformation-date-params.html',
            scope: {
                editableSelect: '=',
                parameters: '=',
                label: '@',
                tooltip: '@'
            },
            bindToController: true,
            controllerAs: 'dateParamsCtrl',
            controller: 'TransformDateParamsCtrl'
        };
    }

    angular.module('data-prep.transformation-params')
        .directive('transformDateParams', TransformDateParams);
})();