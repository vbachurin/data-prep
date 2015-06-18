(function () {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.datagrid.directive:Export
     * @description This directive create the Export<br/>
     * @restrict E
     */
    function Export() {
        return {
            templateUrl: 'components/export/export.html',
            restrict: 'E',
            bindToController: true,
            controllerAs: 'exportCtrl',
            controller: 'ExportCtrl'
        };
    }

    angular.module('data-prep.export')
        .directive('export', Export);
})();