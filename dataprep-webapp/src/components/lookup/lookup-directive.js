(function() {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.lookup.directive:Lookup
     * @description This directive displays the lookup window
     * @restrict E
     * @usage
     * <lookup visible="visible"></lookup>
     * @param {Boolean} visible boolean to show or hide the lookup
     */
    function Lookup() {
        return {
            restrict: 'E',
            templateUrl: 'components/lookup/lookup.html',
            scope: {
                visible: '='
            },
            bindToController: true,
            controllerAs: 'lookupCtrl',
            controller: 'LookupCtrl'
        };
    }

    angular.module('data-prep.lookup')
        .directive('lookup', Lookup);
})();