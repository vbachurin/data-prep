(function() {
    'use strict';

    angular.module('data-prep-dataset')
        .directive('datasetsList', function() {
            return {
                restrict: 'E',
                templateUrl: 'components/dataset/dataset-list/dataset-list-directive.html',
                scope : {
                    datasets : '=',
                    delete : '&onDelete'
                },
                bindToController: true,
                controllerAs: 'datasetListCtrl',
                controller: function() {}
            };
        });
})();