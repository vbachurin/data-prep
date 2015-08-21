(function() {
	'use strict';

	/**
	 * @ngdoc object
	 * @name data-prep.services.upload-workflow
	 * @description This module contains the services to manage uploaded datasets
	 * @requires data-prep.services.dataset
	 * @requires data-prep.services.utils
	 * @requires data-prep.services.playground
	 * @requires ui.router
	 * @requires data-prep.services.dataset
	 */
	angular.module('data-prep.services.uploadWorkflowService', [
		'data-prep.services.dataset',
		'data-prep.services.utils',
		'data-prep.services.playground',
		'ui.router'
	]);
})();