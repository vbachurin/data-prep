/**
 * @ngdoc directive
 * @name data-prep.lookup-datagrid-header.directive:DatagridHeader
 * @description This directive creates the lookup datagrid column header
 * @restrict E
 * @usage
 <lookup-datagrid-header
         added="added"
         column="column">
 </lookup-datagrid-header>
 * @param {object} column The column metadata
 * @param {object} added checkbox ng-model
 */
export default function LookupDatagridHeader($timeout) {
    'ngInject';

    return {
        restrict: 'E',
        templateUrl: 'app/components/lookup/grid/header/lookup-datagrid-header.html',
        scope: {
            column: '=',
            added: '='
        },
        bindToController: true,
        controllerAs: 'lookupDatagridHeaderCtrl',
        controller: 'LookupDatagridHeaderCtrl',
        link: function (scope, iElement) {
            var addToLookupDiv, addToLookupCheckbox;
            $timeout(function () {
                addToLookupDiv = iElement.find('.add-to-lookup');
                addToLookupDiv.on('click', function (e) {
                    e.stopPropagation();
                    addToLookupCheckbox = addToLookupDiv.find('input[type=checkbox]');
                    if (addToLookupCheckbox) {
                        addToLookupCheckbox.click();
                    }
                });
            }, 250, false);
        }
    };
}
