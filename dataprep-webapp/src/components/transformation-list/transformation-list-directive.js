(function() {
    'use strict';

    function TransformationList() {
        return {
            restrict: 'E',
            templateUrl: 'components/transformation-list/transformation-list.html',
            scope: {
                metadata: '='
            },
            bindToController: true,
            controllerAs: 'transformationListCtrl',
            controller: 'TransformationListCtrl'
        };
    }

    angular.module('data-prep.transformation-list')
        .directive('transformationList', TransformationList);
})();