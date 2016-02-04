/**
 * @ngdoc directive
 * @name data-prep.filter-search.directive:FilterSearch
 * @description This directive create an input to add a filter. The `keydown` event is stopped to avoid propagation
 * to a possible {@link talend.widget.directive:TalendModal TalendModal} container
 * @restrict E
 */
export default function FilterSearch() {
    return {
        restrict: 'E',
        templateUrl: 'app/components/filter/search/filter-search.html',
        scope: {},
        bindToController: true,
        controllerAs: 'filterCtrl',
        controller: 'FilterSearchCtrl',
        link: function (scope, iElement, attrs, ctrl) {
            iElement.bind('keydown', function (e) {
                if (e.keyCode === 27) {
                    e.stopPropagation();
                }
            });

            var inputElement = iElement.find('input');
            inputElement[0].onblur = function () {
                ctrl.filterSearch = '';
            };
        }
    };
}