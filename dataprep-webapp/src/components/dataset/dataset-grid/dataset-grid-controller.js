(function () {
    'use strict';

    function DatasetGridCtrl($rootScope) {
        var vm = this;

        $rootScope.$on('talend.dataset.transform', function(event, args) {
            vm.data.records = args.data.records;
        });
    }

    angular.module('data-prep-dataset')
        .controller('DatasetGridCtrl', DatasetGridCtrl);
})();

