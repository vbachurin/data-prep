'use strict';

// Declare app level module which depends on views, and components
var app = angular.module('data-prep', []);

app.controller('RecordsCtrl', function($scope, $http) {
	
	$scope.openDataset = function(id) {
		$http.get('http://10.42.10.99:8081/datasets/'+id+'?metadata=false').success(function(data) {
			$scope.columns = data.columns;
			$scope.records = data.records;
//			loadTableFeedbackStyles();
		});
	};
	
	$scope.listDatasets = function() {
		$http.get('http://10.42.10.99:8081/datasets').success(function(data) {
			$scope.datasets = data;
		});
	};
	
	$scope.datasets = [];
	$scope.listDatasets();
});

app.controller('ColumnCtrl', ['$scope', function($scope) {
	var MIN_PERCENT = 10;
	
	$scope.column.total = $scope.column.quality.valid + $scope.column.quality.empty + $scope.column.quality.invalid;
	
	// *_percent is the real % of empty/valid/invalid records, while *_percent_width is the width % of the bar.
	// They can be differents if less than MIN_PERCENT are valid/invalid/empty, to assure a min width of each bar. To be usable by the user.
	// TODO remove completely one bar if absolute zero records match (ie: if 0 invalid records, do not display invalid bar)
	$scope.column.quality.empty_percent = Math.ceil($scope.column.quality.empty * 100 / $scope.column.total);
	$scope.column.quality.empty_percent_width = Math.max($scope.column.quality.empty_percent, MIN_PERCENT);
	
	$scope.column.quality.invalid_percent = Math.ceil($scope.column.quality.invalid * 100 / $scope.column.total);
	$scope.column.quality.invalid_percent_width = Math.max($scope.column.quality.invalid_percent, MIN_PERCENT);
	
	$scope.column.quality.valid_percent = 100 - $scope.column.quality.empty_percent - $scope.column.quality.invalid_percent;
	$scope.column.quality.valid_percent_width = 100 - $scope.column.quality.empty_percent_width - $scope.column.quality.invalid_percent_width;
}]);

app.directive('datasetGrid', function() {
	return {
		restrict: 'E',
		templateUrl: 'partials/dataset-grid.html'
	};
});
app.directive('datasetColumn', function() {
	return {
		restrict: 'A',
		templateUrl: 'partials/dataset-column.html'
	};
});
app.directive('importLocalFile', function() {
	return {
		restrict: 'E',
		templateUrl: 'partials/import-local-file.html'
	};
});
app.directive('datasetsList', function() {
	return {
		restrict: 'E',
		templateUrl: 'partials/datasets-list.html'
	};
});

app.filter('upperCase', function(){
   return function(input){
      var str = input;
      var res = str.toUpperCase();
      return res; 
   };
});
