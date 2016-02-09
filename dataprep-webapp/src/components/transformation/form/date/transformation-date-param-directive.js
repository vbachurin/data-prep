/*  ============================================================================

  Copyright (C) 2006-2016 Talend Inc. - www.talend.com

  This source code is available under agreement available at
  https://github.com/Talend/data-prep/blob/master/LICENSE

  You should have received a copy of the agreement
  along with this program; if not, write to Talend SA
  9 rue Pages 92150 Suresnes, France

  ============================================================================*/

(function () {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.transformation-form.directive:TransformDateParam
     * @description This directive display a transformation date parameter form
     * @restrict E
     * @usage
     <transform-params
        transformation="transformation"
        on-submit="callback()">

        <div ng-repeat="parameter in paramsCtrl.transformation.parameters track by $index" ng-switch="parameter.type">

            <transform-date-param
                ng-switch-when="date"
                parameter="parameter">
            </transform-date-param>
        </div>

     </transform-params>
     * @param {object} parameters The transformation date parameter
     * @param {object} label Do NOT display label if 'false'. Display it otherwise (by default).
     */
    function TransformDateParam() {
        return {
            restrict: 'E',
            templateUrl: 'components/transformation/form/date/transformation-date-param.html',
            scope: {
                parameter: '=',
                label: '@'
            },
            bindToController: true,
            controllerAs: 'dateParamCtrl',
            controller: 'TransformDateParamCtrl'
        };
    }

    angular.module('data-prep.transformation-form')
        .directive('transformDateParam', TransformDateParam);
})();