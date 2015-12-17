(function() {
	'use strict';

	/**
	 * @ngdoc controller
	 * @name data-prep.easter-eggs
	 * @description Easter eggs controller.
	 * @requires data-prep.services.state.constant:state
	 * @requires data-prep.services.state.service:StateService
	 */
	function EasterEggsCtrl(state, StateService) {
		var vm = this;
		vm.state = state;
		vm.disableEasterEgg = StateService.disableEasterEgg;
	}

	angular.module('data-prep.easter-eggs')
		.controller('EasterEggsCtrl', EasterEggsCtrl);
})();
