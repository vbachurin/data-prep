(function () {
	'use strict';

	/**
	 * @ngdoc service
	 * @name data-prep.services.dataset.service:DatasetLookupService
	 * @description Column types service
	 */
	function DatasetLookupService(DatasetRestService, StateService) {
		var service = {
			loadLookupContent: loadLookupContent,
			getLookupPossibleActions: getLookupPossibleActions,
			resetLookup: resetLookup
		};

		return service;


		function loadLookupContent(lookupDsUrl){
			DatasetRestService.getLookupContent(lookupDsUrl)
				.then(function(lookupDsContent){
					service.currentLookupCols = lookupDsContent.data.columns;
					service.currentLookupRecs = lookupDsContent.data.records;
					service.currentLookupMeta = lookupDsContent.data.metadata;
					service.lookupDsContent = lookupDsContent.data;

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
			return DatasetRestService.getLookupActions(datasetId);
		}

		function resetLookup(){
			service.currentLookupCols = null;
			service.currentLookupRecs = null;
			service.currentLookupMeta = null;
			service.lookupDsContent = null;
		}
	}

	angular.module('data-prep.services.dataset')
		.service('DatasetLookupService', DatasetLookupService);
})();