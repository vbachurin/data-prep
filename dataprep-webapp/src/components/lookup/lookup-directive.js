/**
 * @ngdoc directive
 * @name data-prep.lookup.directive:Lookup
 * @description This directive displays the lookup window
 * @restrict E
 * @usage <lookup visible="visible"></lookup>
 * @param {Boolean} visible boolean to show or hide the lookup
 */
export default function Lookup() {
    return {
        restrict: 'E',
        templateUrl: 'app/components/lookup/lookup.html',
        scope: {
            visible: '='
        },
        bindToController: true,
        controllerAs: 'lookupCtrl',
        controller: 'LookupCtrl'
    };
}