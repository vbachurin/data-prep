(function() {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.services.module:dataset
     * @description This module contains the services to manipulate datasets
     * @requires data-prep.services.module:utils
     */
    angular.module('data-prep.services.dataset', [
        'data-prep.services.utils',
        'angularFileUpload' //file upload with progress support
    ]);
})();