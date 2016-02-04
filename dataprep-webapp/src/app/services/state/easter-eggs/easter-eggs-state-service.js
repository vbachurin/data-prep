export const easterEggsState = {
    currentEasterEgg: '',
    displayEasterEgg: false
};

/**
 * @ngdoc service
 * @name data-prep.services.state.service:EasterEggsStateService
 * @description Manage the state of the easter eggs
 */
export function EasterEggsStateService() {

    return {
        enableEasterEgg: enableEasterEgg,
        disableEasterEgg: disableEasterEgg
    };

    /**
     * @ngdoc method
     * @name enableEasterEgg
     * @methodOf data-prep.services.state.service:EasterEggsStateService
     * @description Set the easter egg to display
     * @param easterEgg the easter egg to display
     */
    function enableEasterEgg(easterEgg) {
        easterEggsState.currentEasterEgg = easterEgg;
        easterEggsState.displayEasterEgg = true;
    }

    /**
     * @ngdoc method
     * @name disableEasterEgg
     * @methodOf data-prep.services.state.service:EasterEggsStateService
     * @description disable the easter egg to display
     */
    function disableEasterEgg() {
        easterEggsState.currentEasterEgg = '';
        easterEggsState.displayEasterEgg = false;

    }
}