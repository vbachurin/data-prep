(function() {
	'use strict';

	/**
	 * @ngdoc object
	 * @name data-prep.services.lookup
	 * @description This module contains the services to load dataset lookup
	 * @requires data-prep.services.dataset
	 * @requires data-prep.services.transformation
	 * @requires data-prep.services.state
	 * @requires data-prep.services.utils
	 */
	angular.module('data-prep.services.lookup', [
		'data-prep.services.dataset',
		'data-prep.services.transformation',
		'data-prep.services.state',
		'data-prep.services.utils'
	]);
})();