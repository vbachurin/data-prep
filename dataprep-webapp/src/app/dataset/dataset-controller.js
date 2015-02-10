(function() {
    'use strict';
    
    function DatasetCtrl(datasetDetails) {
        var vm = this;
        vm.metadata = datasetDetails.metadata;
        vm.data = datasetDetails;
    }
    
    angular.module('data-prep')
        .controller('DatasetCtrl', DatasetCtrl);
})();