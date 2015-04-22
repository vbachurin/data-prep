(function() {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.preparation-list
     * @description This module contains the controller and directives to manage the preparation list
     * @requires talend.widget
     * @requires data-prep.services.preparation
     * @requires data-prep.services.playground
     */
    angular.module('data-prep.preparation-list', [
        'ui.router',
        'talend.widget',
        'data-prep.services.preparation',
        'data-prep.services.playground'
    ]);
})();