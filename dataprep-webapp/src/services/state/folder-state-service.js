(function() {
    'use strict';

    // currentFolder is initalized with root value
    var folderState = {
                        currentFolder:{id:'', path: 'All files'},
                        currentFolderContent: {},
                        foldersStack: [],
                        menuChilds: []
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
            setMenuChilds: setMenuChilds
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
         * @name setCurrentChilds
         * @methodOf data-prep.services.state.service:FolderStateService
         * @param {object} array the children of the current folder
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
         * @name setCurrentChilds
         * @methodOf data-prep.services.state.service:FolderStateService
         * @param {object} array the current children of the current menu entry
         */
        function setMenuChilds(children){
            folderState.menuChilds = children;
        }

    }

    angular.module('data-prep.services.state')
        .service('FolderStateService', FolderStateService)
        .constant('folderState', folderState);
})();