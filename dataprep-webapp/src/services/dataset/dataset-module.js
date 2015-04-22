(function() {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.services.dataset
     * @description This module contains the services to manipulate datasets
     * @requires data-prep.services.utils
     */
    angular.module('data-prep.services.dataset', [
        'data-prep.services.utils',
        'angularFileUpload' //file upload with progress support
    ]);
})();