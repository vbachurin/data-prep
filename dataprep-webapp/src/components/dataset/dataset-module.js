(function() {
    'use strict';

    angular.module('data-prep-dataset', [
        'talend.widget',
        'data-prep-transformation',
        'data-prep-utils',
        'angularFileUpload', //file upload with progress support
        'toaster' //toast notifications
    ]);
})();