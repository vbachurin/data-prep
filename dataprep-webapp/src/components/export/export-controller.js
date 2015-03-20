(function() {
    'use strict';

    function ExportCtrl(ExportService) {
        var vm = this;
        vm.separator = ';';
        vm.initExportLink = null; //init in directive post linker

        vm.export = function() {
            var csv = ExportService.exportToCSV(vm.separator);
            var link = vm.initExportLink(csv);
            link.click();
        };
    }

    angular.module('data-prep.export')
        .controller('ExportCtrl', ExportCtrl);
})();