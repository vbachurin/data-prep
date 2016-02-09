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
     * @name data-prep.transformation-form.directive:TransformParams
     * @description This directive display a transformation parameters form
     * @restrict E
     * @usage
     <transform-params parameters="parameters">
     </transform-params>
     * @param {array} parameters The parameters to render
     */
    function TransformParams() {
        return {
            restrict: 'E',
            templateUrl: 'components/transformation/form/params/transformation-params.html',
            scope: {
                parameters: '='
            },
            bindToController: true,
            controllerAs: 'paramsCtrl',
            controller: 'TransformParamsCtrl'
        };
    }

    angular.module('data-prep.transformation-form')
        .directive('transformParams', TransformParams);
})();