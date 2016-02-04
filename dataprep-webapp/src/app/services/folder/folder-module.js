import FolderService from './folder-service';
import FolderRestService from './rest/folder-rest-service';

(() => {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.services.folder
     * @description This module contains the services to manipulate folders
     */
    angular.module('data-prep.services.folder',
        [
            'data-prep.services.dataset',
            'data-prep.services.state',
            'data-prep.services.utils'
        ])
        .service('FolderRestService', FolderRestService)
        .service('FolderService', FolderService);
})();