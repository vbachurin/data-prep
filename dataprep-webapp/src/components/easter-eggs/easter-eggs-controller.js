/**
 * @ngdoc controller
 * @name data-prep.easter-eggs
 * @description Easter eggs controller.
 * @requires data-prep.services.state.constant:state
 * @requires data-prep.services.state.service:StateService
 */
export default function EasterEggsCtrl(state, StateService) {
    'ngInject';

    var vm = this;
    vm.state = state;
    vm.disableEasterEgg = StateService.disableEasterEgg;
}