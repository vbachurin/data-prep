(function() {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.dataset-list
     * @description This module contains the controller and directives to manage the dataset list
     * @requires talend.widget
     * @requires data-prep.services.dataset
     * @requires data-prep.services.playground
     * @requires data-prep.services.utils
     * @requires data-prep.services.preparation
     */
    angular.module('data-prep.dataset-list', [
        'ui.router',
        'pascalprecht.translate',
        'talend.widget',
        'data-prep.services.dataset',
        'data-prep.services.playground',
        'data-prep.services.utils',
        'data-prep.services.preparation'
    ]);
})();