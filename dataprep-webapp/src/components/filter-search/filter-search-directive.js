(function() {
    'use strict';

    function FilterSearch() {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: 'components/filter-search/filter-search.html',
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