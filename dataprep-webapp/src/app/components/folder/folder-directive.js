/**
 * @ngdoc directive
 * @name data-prep.datagrid.directive:Folder
 * @description This directive create the folder
 * @restrict E
 * @usage <folder></folder>
 */
export default function Folder() {
    return {
        templateUrl: 'app/components/folder/folder.html',
        restrict: 'E',
        bindToController: true,
        controllerAs: 'folderCtrl',
        controller: 'FolderCtrl'
    };
}