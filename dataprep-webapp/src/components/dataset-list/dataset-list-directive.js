'use strict';

angular.module('data-prep')
.directive('datasetsList', function() {
	return {
		restrict: 'E',
		scope : {
			datasets : '=',
			select : '&onSelect'
		},
		templateUrl: 'components/dataset-list/dataset-list-directive.html'
	};
})
;
