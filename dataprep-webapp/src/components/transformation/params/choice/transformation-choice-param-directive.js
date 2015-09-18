(function () {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.transformation-params.directive:TransformChoiceParam
     * @description This directive display a transformation choice parameter form
     * @restrict E
     * @usage
     <transform-params>
        <transform-choice-param
            parameter="parameter">
        </transform-choice-params>
     </transform-params>
     * @param {object} choices The transformation choices parameter
     */
    function TransformChoiceParam() {
        return {
            restrict: 'E',
            templateUrl: 'components/transformation/params/choice/transformation-choice-param.html',
            replace: true,
            scope: {
                parameter: '='
            },
            bindToController: true,
            controllerAs: 'choiceParamCtrl',
            controller: 'TransformChoiceParamCtrl'
        };
    }

    angular.module('data-prep.transformation-params')
        .directive('transformChoiceParam', TransformChoiceParam);
})();