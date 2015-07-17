(function() {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.playground
     * @description This module contains the controller and directives to manage the playground
     * @requires talend.widget
     * @requires data-prep.datagrid
     * @requires data-prep.export
     * @requires data-prep.filter-search
     * @requires data-prep.filter-list
     * @requires data-prep.history-control
     * @requires data-prep.recipe
     * @requires data-prep.suggestions
     * @requires data-prep.services.preparation
     * @requires data-prep.services.playground
     * @requires data-prep.services.recipe
     */
    angular.module('data-prep.playground', [
        'ui.router',
        'pascalprecht.translate',
        'talend.widget',
        'data-prep.datagrid',
        'data-prep.export',
        'data-prep.filter-search',
        'data-prep.filter-list',
        'data-prep.history-control',
        'data-prep.recipe',
        'data-prep.horizontalBarchart',
        'data-prep.suggestions',
        'data-prep.services.preparation',
        'data-prep.services.playground',
        'data-prep.services.recipe'
    ]);
})();