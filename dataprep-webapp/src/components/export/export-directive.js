(function () {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.lookup-datagrid.directive:Export
     * @description This directive create the Export<br/>
     * @restrict E
     */
    function Export() {
        return {
            templateUrl: 'components/export/export.html',
            restrict: 'E',
            bindToController: true,
            controllerAs: 'exportCtrl',
            controller: 'ExportCtrl',
            link: function(scope, iElement, iAttrs, ctrl) {
                ctrl.form = iElement.find('#exportForm').eq(0)[0];
            }
        };
    }

    angular.module('data-prep.export')
        .directive('export', Export);
})();