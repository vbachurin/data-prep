(function() {
    'use strict';

    function ColumnProfileCtrl(DatasetGridService) {
        var vm = this;
        vm.datasetGridService = DatasetGridService;
    }

    Object.defineProperty(ColumnProfileCtrl.prototype,
        'selectedColumnId', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.datasetGridService.selectedColumnId;
            }
        });

    angular.module('data-prep.column-profile')
        .controller('ColumnProfileCtrl', ColumnProfileCtrl);
})();