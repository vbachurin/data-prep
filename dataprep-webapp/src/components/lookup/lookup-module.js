(function() {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.lookup
     * @description This module contains the dataset lookup
     * @requires talend.widget
     */
    angular.module('data-prep.lookup', [
        'talend.widget',
        'data-prep.services.state',
        'data-prep.services.dataset',
        'data-prep.services.transformation'
    ]);
})();