/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

/**
 * @ngdoc service
 * @name data-prep.services.folder.service:FolderService
 * @description Folder service. This service provide the entry point to the Folder service.
 * @requires data-prep.services.state.constant:state
 * @requires data-prep.services.state.service:StateService
 * @requires data-prep.services.folder.service:FolderRestService
 * @requires data-prep.services.utils.service:StorageService
 */
export default function FolderService($state, $translate, state, StateService, FolderRestService, StorageService) {
    'ngInject';

    var ROOT_FOLDER = {
        path: '',
        name: '/'
    };
    $translate('HOME_FOLDER').then(function (homeName) {
        ROOT_FOLDER.name = homeName;
    });

    return {
        // folder operations
        children: FolderRestService.children,
        create: FolderRestService.create,
        rename: FolderRestService.rename,
        remove: FolderRestService.remove,
        search: FolderRestService.search,
        getContent: getContent,
        goToFolder: goToFolder,

        // shared folder ui mngt
        populateMenuChildren: populateMenuChildren,

        refreshDatasetsSort: refreshDatasetsSort,
        refreshDatasetsOrder: refreshDatasetsOrder
    };

    /**
     * @ngdoc method
     * @name buildStackFromId
     * @methodOf data-prep.services.folder.service:FolderService
     * @description Build the folder stack from the the given id
     * @param {string} folderId The folder id
     * @returns {Array} the folder stack
     */
    function buildStackFromId(folderId) {
        var foldersStack = [];
        foldersStack.push(ROOT_FOLDER);

        if (folderId) {
            folderId = _.trim(folderId, '/'); //remove leading & ending '/' coming from the enterprise version
            var paths = folderId.split('/');
            for (var i = 1; i <= paths.length + 1; i++) {
                if (paths[i - 1]) {
                    if (i > 1) {
                        foldersStack.push({
                            path: foldersStack[i - 1].path + '/' + paths[i - 1],
                            name: paths[i - 1]
                        });
                    } else {
                        foldersStack.push({path: paths[i - 1], name: paths[i - 1]});
                    }
                }
            }
        }

        return foldersStack;
    }

    /**
     * @ngdoc method
     * @name populateMenuChildren
     * @methodOf data-prep.folder.controller:FolderCtrl
     * @description Init the state with the folder's children
     * @param {object} folder The folder definition
     */
    function populateMenuChildren(folder) {
        return FolderRestService.getContent(folder && folder.path)
            .then(function (content) {
                StateService.setMenuChildren(content.data.folders);
            });
    }

    /**
     * @ngdoc method
     * @name getContent
     * @methodOf data-prep.folder.controller:FolderCtrl
     * @param {object} folder The folder to list
     * @returns {Promise} The GET promise
     */
    function getContent(folder) {
        var sort = state.inventory.sort.id;
        var order = state.inventory.order.id;
        var promise = FolderRestService.getContent(folder && folder.path, sort, order);

        promise.then(function (result) {
            var content = result.data;
            var foldersStack = buildStackFromId(folder ? folder.path : '');
            var currentFolder = folder ? folder : ROOT_FOLDER;

            StateService.setCurrentFolder(currentFolder);
            StateService.setCurrentFolderContent(content);
            StateService.setFoldersStack(foldersStack);
        });
        return promise;
    }

    /**
     * @ngdoc method
     * @methodOf data-prep.folder.controller:FolderCtrl
     * @name refreshDatasetsSort
     * @description refresh the actual sort parameter
     * */
    function refreshDatasetsSort() {
        var savedSort = StorageService.getDatasetsSort();
        if (savedSort) {
            StateService.setDatasetsSort(_.find(state.inventory.sortList, {id: savedSort}));
        }
    }

    /**
     * @ngdoc method
     * @methodOf data-prep.folder.controller:FolderCtrl
     * @name refreshDatasetsOrder
     * @description refresh the actual order parameter
     */
    function refreshDatasetsOrder() {
        var savedSortOrder = StorageService.getDatasetsOrder();
        if (savedSortOrder) {
            StateService.setDatasetsOrder(_.find(state.inventory.orderList, {id: savedSortOrder}));
        }
    }

    /**
     * @ngdoc method
     * @methodOf data-prep.folder.controller:FolderCtrl
     * @name goToFolder
     * @description go into a folder
     */
    function goToFolder(stateString, options) {
        $state.go(stateString, options);
    }

}