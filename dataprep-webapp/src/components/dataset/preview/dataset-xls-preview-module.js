(function() {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.dataset-xls-preview
     * @description This module contains the controller and directives to manage the dataset xls preview
     * @requires talend.widget
     * @requires data-prep.services.dataset
     * @requires data-prep.services.utils
     */
    angular.module('data-prep.dataset-xls-preview', [
        'ui.router',
        'pascalprecht.translate',
        'talend.widget',
        'data-prep.services.dataset',
        'data-prep.services.utils'
    ]);
})();