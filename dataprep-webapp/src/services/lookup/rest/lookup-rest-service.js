(function () {
	'use strict';

	/**
	 * @ngdoc service
	 * @name data-prep.services.dataset.service:DatasetRestService
	 * @description Dataset service. This service provide the entry point to the backend dataset REST api.<br/>
	 * <b style="color: red;">WARNING : do NOT use this service directly.
	 * {@link data-prep.services.dataset.service:DatasetService DatasetService} must be the only entry point for datasets</b>
	 */
	function LookupRestService($http, RestURLs) {
		return {
			getLookupActions: getLookupActions,
			getLookupContent: getLookupContent,
		};


		/**
		 * @ngdoc method
		 * @name getLookupActions
		 * @methodOf data-prep.services.dataset.service:DatasetRestService
		 * @description get the possible actions of the current dataset
		 * @returns {Promise} The GET promise
		 */
		function getLookupActions (datasetId){
			var url = RestURLs.datasetActionsUrl+ '/' + datasetId + '/actions';
			return $http.get(url);
		}

		function getLookupContent(lookupDsUrl){
			return $http.get(lookupDsUrl);
		}

	}

	angular.module('data-prep.services.lookup')
		.service('LookupRestService', LookupRestService);
})();