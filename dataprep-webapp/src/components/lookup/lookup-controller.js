(function() {
	'use strict';

	/**
	 * @ngdoc controller
	 * @name data-prep.lookup
	 * @description Lookup controller.
	 * @requires data-prep.services.state.constant:state
	 * @requires data-prep.services.state.service:StateService
	 * @requires data-prep.services.lookup.service:LookupService
	 * @requires data-prep.services.playground.service:EarlyPreviewService
	 * @requires data-prep.services.transformation.service:TransformationApplicationService
	 */
	function LookupCtrl(state, StateService, LookupService, EarlyPreviewService,
						TransformationApplicationService) {
		var vm = this;
		vm.state = state;
		vm.cancelEarlyPreview = EarlyPreviewService.cancelEarlyPreview;

		/**
		 * @ngdoc method
		 * @name hoverSubmitBtn
		 * @methodOf data-prep.lookup.controller:LookupCtrl
		 * @description trigger a preview mode on the main dataset to show the lookup action effet
		 */
		vm.hoverSubmitBtn = function hoverSubmitBtn(){
			var previewClosure = EarlyPreviewService.earlyPreview(vm.state.playground.lookupGrid.dataset, 'dataset');
			previewClosure(getParams());
		};

		/**
		 * @ngdoc method
		 * @name getDsName
		 * @methodOf data-prep.lookup.controller:LookupCtrl
		 * @param {object} item dataset lookup action
		 * @returns {String} the name of th dataset to be shown in the list
		 * @description loops over the dataset lookup action parameters to collect the dataset name
		 */
		vm.getDsName = function getDsName (item){//TODO should be put into lookupService
			if(item){
				return _.find(item.parameters, {name:'lookup_ds_name'}).default;
			}
		};

		/**
		 * @ngdoc method
		 * @name loadLookupDsContent
		 * @methodOf data-prep.lookup.controller:LookupCtrl
		 * @description loads the content of the selected dataset
		 * @params {Object} dataset
		 */
		vm.loadLookupDsContent = function loadLookupDsContent (dataset){
			StateService.setLookupDataset(dataset);
			LookupService.loadLookupContent();
		};

		/**
		 * @ngdoc method
		 * @name extractLookupParams
		 * @methodOf data-prep.lookup.controller:LookupCtrl
		 * @param {object} dsLookup dataset lookup action
		 * @returns {object} the params object
		 * @description loops over the dataset lookup action parameters to collect the params
		 */
		function extractLookupParams (dsLookup){
			return _.reduce(dsLookup.parameters, function(res, param){
				res[param.name] = param.default;
				return res;
			},{});
		}

		/**
		 * @ngdoc method
		 * @name getParams
		 * @methodOf data-prep.lookup.controller:LookupCtrl
		 * @description returns the params of the lookup action
		 */
		function getParams () {
			var params = extractLookupParams(vm.state.playground.lookupGrid.dataset);
			/*jshint camelcase: false */
			params.column_id = vm.state.playground.grid.selectedColumn.id;
			params.column_name = vm.state.playground.grid.selectedColumn.name;
			params.lookup_join_on = vm.state.playground.lookupGrid.selectedColumn.id;
			params.lookup_join_on_name = vm.state.playground.lookupGrid.selectedColumn.name;
			params.lookup_selected_cols = vm.state.playground.lookupGrid.lookupColumnsToAdd;
			return params;
		}

		/**
		 * @ngdoc method
		 * @name submitLookup
		 * @methodOf data-prep.lookup.controller:LookupCtrl
		 * @description submits the lookup action
		 */
		vm.submitLookup = function submitLookup() {
			EarlyPreviewService.deactivatePreview();
			EarlyPreviewService.cancelPendingPreview();

			TransformationApplicationService.append(vm.state.playground.lookupGrid.dataset, 'dataset', getParams())
				.finally(function() {
					setTimeout(EarlyPreviewService.activatePreview, 500);
					StateService.setLookupVisibility(false);
				});
		};
	}

	angular.module('data-prep.lookup')
		.controller('LookupCtrl', LookupCtrl);
})();
