(function() {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.filter-search.directive:FilterSearch
     * @description This directive create an input to add a filter. The `keydown` event is stopped to avoid propagation
     * to a possible {@link talend.widget.TalendModal TalendModal} container
     * @restrict E
     */
    function FilterSearch() {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: 'components/filter/search/filter-search.html',
            scope: {},
            bindToController: true,
            controllerAs: 'filterCtrl',
            controller: 'FilterSearchCtrl',
            link: function(scope, iElement) {
                iElement.bind('keydown', function (e) {
                    if(e.keyCode === 27) {
                        e.stopPropagation();
                    }
                });
            }
        };
    }

    angular.module('data-prep.filter-search')
        .directive('filterSearch', FilterSearch);
})();