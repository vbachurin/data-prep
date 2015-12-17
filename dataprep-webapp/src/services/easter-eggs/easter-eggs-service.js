(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.easter-eggs.service:EasterEggsService
     * @description Easter Eggs service. This service that deals with easter eggs.
     * @requires data-prep.services.state.service:StateService
     */
    function EasterEggsService(StateService) {
        /**
         * @ngdoc method
         * @name enableEasterEgg
         * @methodOf data-prep.services.easter-eggs.service:EasterEggsService
         * @description Enable easter egg depending on the given input.
         */
        this.enableEasterEgg = function enableEasterEgg(input) {
            if (input === 'star wars') {
                StateService.enableEasterEgg(input);
            }
        };

    }

    angular.module('data-prep.services.easter-eggs')
        .service('EasterEggsService', EasterEggsService);
})();