'use strict';

// Declare app level module which depends on views, and components
var app = angular.module('data-prep', []);

app.controller('RecordsCtrl', function($scope, $http) {
	
	$scope.loadFile = function() {
		$http.get('ranking').success(function(data) {
			$scope.columns = data.columns;
			$scope.records = data.records;
			loadTableFeedbackStyles();
		});
	};
	$scope.loadFileJson = function() {
		$http.get('customers_100_full.json').success(function(data) {
			$scope.columns = data.columns;
			$scope.records = data.records;
			loadTableFeedbackStyles();
		});
	};
	
});

app.directive('datasetGrid', function() {
	return {
		restrict: 'E',
		templateUrl: 'partials/dataset-grid.html'
	};
});
app.directive('importLocalFile', function() {
	return {
		restrict: 'E',
		templateUrl: 'partials/import-local-file.html'
	};
});
