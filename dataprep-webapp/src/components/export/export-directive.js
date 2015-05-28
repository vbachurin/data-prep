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
                ctrl.export = function (type) {
                    var form = document.getElementById('exportForm');
                    form.action = ctrl.exportUrl;
                    form.exportType.value = type;
                    form.submit();
                };
            }
        };
    }

    angular.module('data-prep.export')
        .directive('export', Export);
})();