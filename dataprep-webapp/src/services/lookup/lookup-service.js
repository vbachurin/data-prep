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
			getLookupPossibleActions: getLookupPossibleActions,
			loadSelectedLookupContent: loadSelectedLookupContent
		};

		return service;

		/**
		 * @ngdoc meth???
		 * @name loadL???upContent
		 * @methodOf d???-prep.services.lookup.service:LookupService
		 * @param {str???} lookupDsUrl dataset lookup url
		 * @descriptio???iven a dataset Lookup url, it loads its content
		 */
		function loadLookupContent(lookupDsUrl){
			LookupRestService.getLookupContent(lookupDsUrl)
				.then(function(lookupDsContent){
					StateService.setCurrentLookupData(lookupDsContent.data);
				});
		}

		/**
		 * @ngdoc method
		 * @name getLookupPossibleActions
		 * @methodOf data-prep.services.lookup.service:LookupService
		 * @param {string} datasetId dataset id
		 * @description given a dataset id, it loads its possible actions
		 */
		function getLookupPossibleActions(datasetId){
			return LookupRestService.getLookupActions(datasetId);
		}

		/**
		 * @ngdoc met????
		 * @name load????ctedLookupContent
		 * @methodOf ????-prep.lookup.controller:LookupCtrl
		 * @param {ob????} dsLookup dataset lookup action
		 * @descripti????oads the content of the selected lookup dataset
		 */
		function loadSelectedLookupContent(){
			loadLookupContent(getDsUrl(state.playground.lookupGrid.dataset));
		}

		function getDsUrl (item){
			if(item){
				return _.find(item.parameters, {name:'lookup_ds_url'}).default;
			}
		}
	}

	angular.module('data-prep.services.lookup')
		.service('LookupService', LookupService);
})();