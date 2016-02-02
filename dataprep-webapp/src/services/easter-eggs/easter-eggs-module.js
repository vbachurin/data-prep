import EasterEggsService from './easter-eggs-service';

(() => {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.services.easter-eggs
     * @description This module contains the services for easter eggs
     * @requires data-prep.services.state
     * @requires data-prep.services.utils
     */
    angular.module('data-prep.services.easter-eggs',
        [
            'data-prep.services.state',
            'data-prep.services.utils'
        ])
        .service('EasterEggsService', EasterEggsService);
})();