(function() {
	'use strict';
	function FilterListCtrl(FilterService, state){
		var vm = this;

		vm.filterService = FilterService;
		vm.state = state;

		/**
		 * @ngdoc method
		 * @name calculateLinesPercentage
		 * @methodOf data-prep.filter-list.controller:FilterListCtrl
		 * @description clculates the percentage of the shown lines nulber over the total lines numbers.
		 * @returns {String} the label in a quotient format
		 */
		vm.calculateLinesPercentage = function(){
			return ((state.playground.shownLinesLength / state.playground.allLinesLength)*100).toFixed(0) + '%';
		};

	}

	angular.module('data-prep.filter-list')
		.controller('FilterListCtrl', FilterListCtrl);
})();