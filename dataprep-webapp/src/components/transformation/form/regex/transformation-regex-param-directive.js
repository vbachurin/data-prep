/*  ============================================================================

  Copyright (C) 2006-2016 Talend Inc. - www.talend.com

  This source code is available under agreement available at
  https://github.com/Talend/data-prep/blob/master/LICENSE

  You should have received a copy of the agreement
  along with this program; if not, write to Talend SA
  9 rue Pages 92150 Suresnes, France

  ============================================================================*/

(function() {
    'use strict';

    function TransformRegexParam() {
        return {
            restrict: 'E',
            templateUrl: 'components/transformation/form/regex/transformation-regex-param.html',
            scope: {
                parameter: '='
            },
            bindToController: true,
            controllerAs: 'regexParamCtrl',
            controller: 'TransformRegexParamCtrl'
        };
    }

    angular.module('data-prep.transformation-form')
        .directive('transformRegexParam', TransformRegexParam);
})();