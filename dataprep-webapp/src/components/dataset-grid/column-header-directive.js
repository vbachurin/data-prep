'use strict';

angular.module('data-prep')
.directive('datasetColumn', function() {
	return {
		restrict: 'E',
		scope:{
			column : '='
		},
		templateUrl: 'components/dataset-grid/column-header-directive.html'
	};
})
;
