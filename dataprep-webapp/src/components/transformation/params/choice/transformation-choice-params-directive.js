(function () {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.transformation-params.directive:TransformChoiceParams
     * @description This directive display a transformation choice parameters form
     * @restrict E
     * @usage
     <transform-params>
        <transform-choice-params
            choices="choices">
        </transform-choice-params>
     </transform-params>
     * @param {object} choices The transformation choices parameters
     */
    function TransformChoiceParams() {
        return {
            restrict: 'E',
            templateUrl: 'components/transformation/params/choice/transformation-choice-params.html',
            replace: true,
            scope: {
                choices: '='
            },
            bindToController: true,
            controllerAs: 'choiceParamsCtrl',
            controller: 'TransformChoiceParamsCtrl'
        };
    }

    angular.module('data-prep.transformation-params')
        .directive('transformChoiceParams', TransformChoiceParams);
})();