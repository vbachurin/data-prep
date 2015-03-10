(function() {
    'use strict';

    angular.module('data-prep-dataset', [
        'talend.widget',
        'data-prep-utils',
        'data-prep-transformation', //transformation actions and list management
        'data-prep-recipe', //recipe actions and list management
        'data-prep-filter', //filter actions and list management
        'angularFileUpload' //file upload with progress support
    ]);
})();