(function() {
    'use strict';

    // currentFolder is initalized with root value
    var folderState = {
                        currentFolder:{id:'', path: 'All files'},
                        currentFolderContent: {},
                        foldersStack: [],
                        menuChildren: []
                      };

    /**
     * @ngdoc service
     * @name data-prep.services.state.service:FolderStateService
     * @description Folder state service. Manage the folder state
     */
    function FolderStateService() {
        return {
            setCurrentFolder: setCurrentFolder,
            setCurrentFolderContent: setCurrentFolderContent,
            setFoldersStack: setFoldersStack,
            setMenuChildren: setMenuChildren
        };

        /**
         * @ngdoc method
         * @name setCurrentFolder
         * @methodOf data-prep.services.state.service:FolderStateService
         * @param {object} the current folder
         * @description Update the current folder
         */
        function setCurrentFolder(folder) {
            folderState.currentFolder = folder;
        }

        /**
         * @ngdoc method
         * @name setCurrentFolderContent
         * @methodOf data-prep.services.state.service:FolderStateService
         * @param {object} the content of the current folder
         */
        function setCurrentFolderContent(children){
            folderState.currentFolderContent = children;
        }

        /**
         * @ngdoc method
         * @name setFoldersStack
         * @methodOf data-prep.services.state.service:FolderStateService
         * @param {object} the current folders stack
         */
        function setFoldersStack(stack){
            folderState.foldersStack = stack;
        }

        /**
         * @ngdoc method
         * @name setMenuChildren
         * @methodOf data-prep.services.state.service:FolderStateService
         * @param {object} array the current children of the current menu entry
         */
        function setMenuChildren(children){
            folderState.menuChildren = children;
        }

    }

    angular.module('data-prep.services.state')
        .service('FolderStateService', FolderStateService)
        .constant('folderState', folderState);
})();