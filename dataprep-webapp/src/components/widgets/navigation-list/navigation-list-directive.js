(function() {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.widget.directive:NavigationList
     * @description This directive display a navigable list of elements
     * @restrict E
     * @usage
     * <navigation-list
            list="list"
            on-click="callbackOn(item)"
            selected-item="selectedItem"
            get-label="callback(item)"
            nbre-labels-to-show="4">
       </navigation-list>
     * @param {Array} list the collection to navigate into
     * @param {object} selectedItem selected Item from the given list
     * @param {function} onClick The callback executed on an item clicked
     * @param {function} getLabel The function that returns the label to show
     * @param {number} nbreLabelsToShow the number of labels to show
     */
    function NavigationList() {
        return {
            restrict: 'E',
            templateUrl: 'components/widgets/navigation-list/navigation-list.html',
            scope: {
                list: '=',
                selectedItem: '=',
                onClick: '&',
                getLabel: '&',
                nbreLabelsToShow: '@'
            },
            bindToController: true,
            controllerAs: 'navigationListCtrl',
            controller: 'NavigationListCtrl'
        };
    }

    angular.module('talend.widget')
        .directive('navigationList', NavigationList);
})();