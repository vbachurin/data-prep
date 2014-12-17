'use strict';

// Declare app level module which depends on views, and components
var app = angular.module('people', []);

app.controller('PeoplesCtrl', function($scope, $http) {
	
	$scope.loadFile = function() {
		$http.get('ranking').success(function(data) {
			$scope.peoples = data;
		});
	};
	$scope.loadFileJson = function() {
		$http.get('customers_100.json').success(function(data) {
			$scope.peoples = data;
		});
	};
});

app.directive('datasetGrid', function() {
	return {
		restrict: 'E',
		templateUrl: 'partials/dataset-grid.html'
	};
});
