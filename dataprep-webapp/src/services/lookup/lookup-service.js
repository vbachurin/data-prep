(function () {
	'use strict';

	/**
	 * @ngdoc service
	 * @name data-prep.services.lookup.service:LookupService
	 * @description Lookup service. This service provide the entry point to load lookup content
	 * @requires data-prep.services.state.service:StateService
	 * @requires data-prep.services.utils.service:RestURLs
	 */
	function LookupService(LookupRestService, StateService, state) {
		var service = {
			loadLookupContent: loadLookupContent,
			getLookupPossibleActions: getLookupPossibleActions
		};

		return service;

		/**
		 * @ngdoc method
		 * @name loadLookupContent
		 * @methodOf data-prep.services.lookup.service:LookupService
		 * @description loads the lookup dataset content
		 */
		function loadLookupContent(){
			LookupRestService.getLookupContent(getDsUrl(state.playground.lookupGrid.dataset))
				.then(function(lookupDsContent){
					StateService.setCurrentLookupData(lookupDsContent.data);
				});
		}

		/**
		 * @ngdoc method
		 * @name getLookupPossibleActions
		 * @methodOf data-prep.services.lookup.service:LookupService
		 * @param {string} datasetId dataset id
		 * @description given a dataset id, it loads its possible lookup datasets
		 */
		function getLookupPossibleActions(datasetId){
			return LookupRestService.getLookupActions(datasetId)
				.then(function(datasets){
					StateService.setLookupDatasets(datasets.data);
				});
		}

		/**
		 * @ngdoc method
		 * @name getDsUrl
		 * @methodOf data-prep.services.lookup.service:LookupService
		 * @param {object} item dataset lookup action
		 * @returns {String} the url of the lookup dataset
		 * @description loops over the dataset lookup action parameters to pick up the dataset url
		 */
		function getDsUrl (item){
			if(item){
				return _.find(item.parameters, {name:'lookup_ds_url'}).default;
			}
		}
	}

	angular.module('data-prep.services.lookup')
		.service('LookupService', LookupService);
})();