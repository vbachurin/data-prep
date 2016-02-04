/**
 * @ngdoc directive
 * @name data-prep.transformation-form.directive:TransformParams
 * @description This directive display a transformation parameters form
 * @restrict E
 * @usage <transform-params parameters="parameters"></transform-params>
 * @param {array} parameters The parameters to render
 */
export default function TransformParams() {
    return {
        restrict: 'E',
        templateUrl: 'app/components/transformation/form/params/transformation-params.html',
        scope: {
            parameters: '='
        },
        bindToController: true,
        controllerAs: 'paramsCtrl',
        controller: 'TransformParamsCtrl'
    };
}