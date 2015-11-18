(function () {
	'use strict';

	/**
	 * @ngdoc service
	 * @name data-prep.services.lookup.service:LookupService
	 * @description Column types service
	 */
	function LookupService(LookupRestService, StateService) {
		var service = {
			loadLookupContent: loadLookupContent,
			getLookupPossibleActions: getLookupPossibleActions
		};

		return service;

		/**
		 * @ngdoc method
		 * @name getLookupPossibleActions
		 * @methodOf data-prep.services.dataset.service:DatasetService
		 * @param {string} datasetId ???????????????
		 * @description Get a ??????????????? base namr)"
		 * @returns {string} ???????????????
		 */
		function loadLookupContent(lookupDsUrl){
			LookupRestService.getLookupContent(lookupDsUrl)
				.then(function(lookupDsContent){
					StateService.setCurrentLookupData(lookupDsContent.data);
					StateService.setLookupDataset(lookupDsContent.data.metadata);
				});
		}

		/**
		 * @ngdoc method
		 * @name getLookupPossibleActions
		 * @methodOf data-prep.services.dataset.service:DatasetService
		 * @param {string} datasetId ???????????????
		 * @description Get a ??????????????? base namr)"
		 * @returns {string} ???????????????
		 */
		function getLookupPossibleActions(datasetId){
			return LookupRestService.getLookupActions(datasetId);
		}
	}

	angular.module('data-prep.services.lookup')
		.service('LookupService', LookupService);
})();