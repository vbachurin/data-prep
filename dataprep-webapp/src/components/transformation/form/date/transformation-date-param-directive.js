/**
 * @ngdoc directive
 * @name data-prep.transformation-form.directive:TransformDateParam
 * @description This directive display a transformation date parameter form
 * @restrict E
 * @usage <transform-date-param label="label" parameter="parameter">  </transform-date-param>
 * @param {object} parameters The transformation date parameter
 * @param {object} label Do NOT display label if 'false'. Display it otherwise (by default).
 */
export default function TransformDateParam() {
    return {
        restrict: 'E',
        templateUrl: 'app/components/transformation/form/date/transformation-date-param.html',
        scope: {
            parameter: '=',
            label: '@'
        },
        bindToController: true,
        controllerAs: 'dateParamCtrl',
        controller: 'TransformDateParamCtrl'
    };
}