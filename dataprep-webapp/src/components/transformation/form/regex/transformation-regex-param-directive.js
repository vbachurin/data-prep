/**
 * @ngdoc directive
 * @name data-prep.transformation-form.directive:TransformRegexParam
 * @description This directive display a regex parameters form
 * @restrict E
 * @usage <transform-regex-param parameters="parameters"></transform-regex-param>
 * @param {object} parameter The parameter to render
 */
export default function TransformRegexParam() {
    return {
        restrict: 'E',
        templateUrl: 'app/components/transformation/form/regex/transformation-regex-param.html',
        scope: {
            parameter: '='
        },
        bindToController: true,
        controllerAs: 'regexParamCtrl',
        controller: 'TransformRegexParamCtrl'
    };
}