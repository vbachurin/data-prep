(function() {
    'use strict';

    function Lookup() {
        return {
            restrict: 'E',
            templateUrl: 'components/lookup/lookup.html',
            scope: {
                visible: '='
            },
            bindToController: true,
            controllerAs: 'LookupCtrl',
            controller: 'lookupCtrl'
        };
    }

    angular.module('data-prep.lookup')
        .directive('lookup', Lookup);
})();