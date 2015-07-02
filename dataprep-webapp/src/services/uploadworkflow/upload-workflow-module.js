(function() {
	'use strict';

	/**
	 * @ngdoc object
	 * @name data-prep.services.uploadworkflow
	 * @description This module contains the services to manage uploaded datasets
	 * @requires data-prep.services.dataset
	 * @requires data-prep.services.utils
	 */
	angular.module('data-prep.services.uploadWorkflowService', [
		'data-prep.services.dataset',
		'data-prep.services.utils',
		'ui.router'
	]);
})();