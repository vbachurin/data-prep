(function () {
    'use strict';

    function Export() {
        return {
            templateUrl: 'components/export/export.html',
            restrict: 'E',
            bindToController: true,
            controllerAs: 'exportCtrl',
            controller: 'ExportCtrl',
        };
    }

    angular.module('data-prep.export')
        .directive('export', Export);
})();