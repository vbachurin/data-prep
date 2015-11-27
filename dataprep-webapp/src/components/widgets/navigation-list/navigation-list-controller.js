(function () {
    'use strict';

    /**
     * @ngdoc controller
     * @name talend.widget.controller:NavigationListCtrl
     * @description navigation-list directive controller
     */
    function NavigationListCtrl($scope) {
        var vm = this;

        /**
         * @ngdoc property
         * @name firstLabelIndex
         * @propertyOf talend.widget.controller:NavigationListCtrl
         * @description index from which to start showing labels
         * @type {Number}
         */
        vm.firstLabelIndex = 0;

        /**
         * @ngdoc property
         * @name shownLabelsIndexes
         * @propertyOf talend.widget.controller:NavigationListCtrl
         * @description index from which to end showing labels
         * @type {Number}
         */
        vm.lastLabelIndex = +vm.nbreLabelsToShow;

        /**
         * @ngdoc method
         * @name showBack
         * @methodOf talend.widget.controller:NavigationListCtrl
         * @description navigate in the list to the left
         */
        vm.showBack = function showBack(){
            vm.firstLabelIndex--;
            vm.lastLabelIndex--;
        };

        /**
         * @ngdoc method
         * @name showForth
         * @methodOf talend.widget.controller:NavigationListCtrl
         * @description navigate in the list to the right
         */
        vm.showForth = function showforth(){
            vm.firstLabelIndex++;
            vm.lastLabelIndex++;
        };

        /**************************************************************************************************************/
        /******************************** Watcher to reset the navigation list  limits ********************************/
        /**************************************************************************************************************/
        $scope.$watch(function(){
            return vm.list;
        }, function() {
            vm.firstLabelIndex = 0;
            vm.lastLabelIndex = +vm.nbreLabelsToShow;
        });
    }

    angular.module('talend.widget')
        .controller('NavigationListCtrl', NavigationListCtrl);
})();