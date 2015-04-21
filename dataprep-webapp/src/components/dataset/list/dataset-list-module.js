(function() {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.module:dataset-list
     * @description This module contains the controller and directives to manage the dataset list
     * @requires data-prep.services.module:dataset
     * @requires data-prep.services.module:playground
     * @requires data-prep.services.module:utils
     */
    angular.module('data-prep.dataset-list', [
        'ui.router',
        'pascalprecht.translate',
        'talend.widget',
        'data-prep.services.dataset',
        'data-prep.services.playground',
        'data-prep.services.utils'
    ]);
})();