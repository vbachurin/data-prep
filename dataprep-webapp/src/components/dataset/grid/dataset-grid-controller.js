(function () {
    'use strict';

    function DatasetGridCtrl($rootScope) {
        var vm = this;
        /**
         * Show datagrid modal flag
         * @type {boolean}
         */
        vm.showDataGrid = false;

        /**
         * Replace records on transform event
         */
        $rootScope.$on('talend.dataset.transform', function(event, args) {
            vm.data.records = args.data.records;
        });

        /**
         * Open dataset event : replace data and display modal
         */
        $rootScope.$on('talend.dataset.open', function(event, args) {
            vm.metadata = args.metadata;
            vm.data = args.data;
            vm.showDataGrid = true;
        });
    }

    angular.module('data-prep-dataset')
        .controller('DatasetGridCtrl', DatasetGridCtrl);
})();

