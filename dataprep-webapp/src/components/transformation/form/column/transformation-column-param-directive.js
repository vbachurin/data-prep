/**
 * @ngdoc directive
 * @name data-prep.transformation-form.directive:TransformColumnParam
 * @description This directive display a select parameter form to select a column
 * @restrict E
 * @usage <transform-column-param parameter="parameter"></transform-column-param>
 * @param {object} parameter The column parameter
 */
export default function TransformColumnParam() {
    return {
        restrict: 'E',
        templateUrl: 'app/components/transformation/form/column/transformation-column-param.html',
        scope: {
            parameter: '='
        },
        bindToController: true,
        controllerAs: 'columnParamCtrl',
        controller: 'TransformColumnParamCtrl'
    };
}