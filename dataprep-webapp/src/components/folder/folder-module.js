import FolderCtrl from './folder-controller';
import Folder from './folder-directive';

(() => {
    'use strict';

    angular.module('data-prep.folder',
        [
            'data-prep.services.state',
            'data-prep.services.folder'
        ])
        .controller('FolderCtrl', FolderCtrl)
        .directive('folder', Folder);
})();