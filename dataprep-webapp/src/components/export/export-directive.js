/**
 * @ngdoc directive
 * @name data-prep.datagrid.directive:Export
 * @description This directive create the Export<br/>
 * @restrict E
 * @usage <export></export>
 */
export default function Export() {
    return {
        templateUrl: 'app/components/export/export.html',
        restrict: 'E',
        bindToController: true,
        controllerAs: 'exportCtrl',
        controller: 'ExportCtrl',
        link: function (scope, iElement, iAttrs, ctrl) {
            ctrl.form = iElement.find('#exportForm').eq(0)[0];
        }
    };
}