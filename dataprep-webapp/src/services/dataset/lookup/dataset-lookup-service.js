(function () {
	'use strict';

	/**
	 * @ngdoc service
	 * @name data-prep.services.dataset.service:DatasetLookupService
	 * @description Column types service
	 */
	function DatasetLookupService(DatasetRestService) {
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
				});
		}

		/**
		 * @ngdoc method
		 * @name getLookupPossibleActions
		 * @methodOf data-prep.services.dataset.service:DatasetService
		 * @param {string} name ???????????????
		 * @description Get a ??????????????? base namr)"
		 * @returns {string} ???????????????
		 */
		function getLookupPossibleActions(datasetId){
			return DatasetRestService.getLookupActions(datasetId)
				.then(extractLookupFiles);
		}

		function extractLookupFiles (resp){
			var actionsFormat = _.pluck(resp.data, 'parameters').map(function(paramsTab){
				return _.reduce(paramsTab, function(res, param){
					res[param.name] = param.default;
					return res;
				},{});
			});
			return {
				transformationFormat : resp.data,
				actionsFormat : actionsFormat
			};
		}

		function resetLookup(){
			service.currentLookupCols = null;
			service.currentLookupRecs = null;
		}
	}

	angular.module('data-prep.services.dataset')
		.service('DatasetLookupService', DatasetLookupService);
})();