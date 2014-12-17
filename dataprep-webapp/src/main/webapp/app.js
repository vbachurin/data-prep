'use strict';

// Declare app level module which depends on views, and components
var app = angular.module('data-prep', []);

app.controller('RecordsCtrl', function($scope, $http) {
	
	$scope.loadFile = function() {
		$http.get('ranking').success(function(data) {
			$scope.columns = data.columns;
			$scope.records = data.records;
		});
	};
	$scope.loadFileJson = function() {
		$http.get('customers_100_full.json').success(function(data) {
			$scope.columns = data.columns;
			$scope.records = data.records;
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
