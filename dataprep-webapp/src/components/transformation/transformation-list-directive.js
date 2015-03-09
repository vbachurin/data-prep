(function() {
    'use strict';

    function TransformationList() {
        return {
            restrict: 'E',
            templateUrl: '/components/transformation/transformation-list.html',
            scope: {
                metadata: '='
            },
            bindToController: true,
            controllerAs: 'transformationCtrl',
            controller: 'TransformationCtrl'
        }
    }

    angular.module('data-prep-transformation')
        .directive('transformationsList', TransformationList);
})();