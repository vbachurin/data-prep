/**
 * @ngdoc service
 * @name data-prep.services.easter-eggs.service:EasterEggsService
 * @description Easter Eggs service. This service that deals with easter eggs.
 * @requires data-prep.services.state.service:StateService
 */
export default function EasterEggsService(StateService) {
    'ngInject';

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