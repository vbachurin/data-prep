(function () {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.transformation-params.directive:TransformDateParam
     * @description This directive display a transformation date parameter form
     * @restrict E
     * @usage
     <transform-params
        transformation="transformation"
        on-submit="callback()">

        <div ng-repeat="parameter in paramsCtrl.transformation.parameters" ng-switch="parameter.type">

            <transform-date-param
                ng-switch-when="simple"
                parameter="parameter">
            </transform-date-param>
        </div>

     </transform-params>
     * @param {object} parameters The transformation date parameter
     * @param {object} label Do NOT display label if 'false'. Display it otherwise (by default).
     * @param {object} tooltip Do NOT display tooltip if 'false'. Display it otherwise (by default).
     */
    function TransformDateParam() {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: 'components/transformation/params/date/transformation-date-param.html',
            scope: {
                editableSelect: '=',
                parameter: '=',
                label: '@',
                tooltip: '@'
            },
            bindToController: true,
            controllerAs: 'dateParamCtrl',
            controller: 'TransformDateParamCtrl'
        };
    }

    angular.module('data-prep.transformation-params')
        .directive('transformDateParam', TransformDateParam);
})();