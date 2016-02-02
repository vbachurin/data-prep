/**
 * @ngdoc directive
 * @name data-prep.transformation-form.directive:TransformChoiceParam
 * @description This directive display a transformation choice parameter form
 * @restrict E
 * @usage <transform-choice-param parameter="parameter"></transform-choice-params>
 * @param {object} parameter The transformation choices parameter
 */
export default function TransformChoiceParam($rootScope, $compile) {
    'ngInject';

    return {
        restrict: 'E',
        templateUrl: 'app/components/transformation/form/choice/transformation-choice-param.html',
        scope: {
            parameter: '='
        },
        bindToController: true,
        controllerAs: 'choiceParamCtrl',
        controller: 'TransformChoiceParamCtrl',
        link: function (scope, iElement, iAttrs, ctrl) {
            _.chain(ctrl.parameter.configuration.values)
                .filter(function (optionValue) {
                    return optionValue.parameters && optionValue.parameters.length;
                })
                .forEach(function (optionValue) {
                    var isolatedScope = $rootScope.$new(true);
                    isolatedScope.parameter = ctrl.parameter;
                    isolatedScope.optionValue = optionValue;

                    var template = '<transform-params ' +
                        'parameters="optionValue.parameters" ' +
                        'ng-if="parameter.value === optionValue.value" ' +
                        '></transform-params>';
                    $compile(template)(isolatedScope, function (cloned) {
                        iElement.append(cloned);
                    });
                })
                .value();
        }
    };
}