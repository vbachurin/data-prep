'use strict';

angular.module('data-prep')
.directive('datasetGrid', function() {
	return {
		restrict: 'E',
		templateUrl: 'components/dataset-grid/dataset-grid-directive.html'
	};
})
;
