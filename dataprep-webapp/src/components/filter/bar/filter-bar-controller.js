(function() {
	'use strict';
	function FilterBarCtrl(state, FilterService){
		var vm = this;
		vm.filterService = FilterService;
		vm.state = state;
	}

	angular.module('data-prep.filter-bar')
		.controller('FilterBarCtrl', FilterBarCtrl);
})();