/**
 * @ngdoc directive
 * @name data-prep.preparation-list.directive:PreparationList
 * @description This directive display the preparations list.
 * @restrict E
 */
export default function PreparationList() {
    return {
        restrict: 'E',
        templateUrl: 'app/components/preparation-list/preparation-list.html',
        bindToController: true,
        controllerAs: 'preparationListCtrl',
        controller: 'PreparationListCtrl'
    };
}