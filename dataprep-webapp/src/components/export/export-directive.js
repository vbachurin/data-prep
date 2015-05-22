(function () {
    'use strict';

    function Export() {
        return {
            templateUrl: 'components/export/export.html',
            restrict: 'E',
            bindToController: true,
            controllerAs: 'exportCtrl',
            controller: 'ExportCtrl',
            link: function (scope, iElement, iAttrs, ctrl) {
                ctrl.export = function () {
                    var form = document.getElementById('csvExport');
                    form.action = ctrl.exportUrl;
                    form.submit();
                };
            }
        };
    }

    angular.module('data-prep.export')
        .directive('export', Export);
})();