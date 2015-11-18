(function() {
	'use strict';

	/**
	 * @ngdoc controller
	 * @name data-?????????????dataset-list.controller:DatasetListCtrl
	 * @description Dataset list ?????????????
	 On creation, it fetch ?????????????from backend and load playground if 'datasetid' query param is provided
	 * @requires data-?????????????services.dataset.service:DatasetService
	 * @requires data-?????????????services.dataset.service:DatasetListSortService
	 * @requires data-?????????????services.playground.service:PlaygroundService
	 * @requires talend.?????????????service:TalendConfirmService
	 * @requires data-?????????????services.utils.service:MessageService
	 * @requires data-?????????????services.uploadWorkflowService.service:UploadWorkflowService
	 * @requires data-?????????????services.state.service:StateService
	 * @requires data-?????????????services.datasetWorkflowService:UpdateWorkflowService
	 */
	function LookupCtrl($scope, state, StateService, LookupService, EarlyPreviewService, TransformationApplicationService) {
		var vm = this;
		vm.state = state;

		vm.lookupService = LookupService;
		vm.earlyPreview = EarlyPreviewService.earlyPreview;
		vm.cancelEarlyPreview = EarlyPreviewService.cancelEarlyPreview;

		vm.hoverSubmitBtn = function hoverSubmitBtn(){
			var previewClosure = vm.earlyPreview(vm.lookupAction, 'dataset');
			populateParams();
			previewClosure(vm.lookupParams);
		};

		function extractLookupParams (dsLookup){
			return _.reduce(dsLookup.parameters, function(res, param){
				res[param.name] = param.default;
				return res;
			},{});
		}

		vm.loadSelectedLookupContent = function(lookupDs){
			StateService.resetLookup();
			vm.lookupParams = extractLookupParams(lookupDs);
			vm.lookupAction = lookupDs;
			/*jshint camelcase: false */
			vm.lookupService.loadLookupContent(vm.lookupParams.lookup_ds_url);
		};

		function populateParams () {
			/*jshint camelcase: false */
			vm.lookupParams.column_id = vm.state.playground.grid.selectedColumn.id;
			vm.lookupParams.column_name = vm.state.playground.grid.selectedColumn.name;
			vm.lookupParams.lookup_join_on = vm.state.playground.lookupGrid.selectedColumn.id;
			vm.lookupParams.lookup_join_on_name = vm.state.playground.lookupGrid.selectedColumn.name;
			vm.lookupParams.lookup_selected_cols = vm.state.playground.lookupGrid.lookupColumnsToAdd;
		}

		vm.submitLookup = function submitLookup(action) {
			EarlyPreviewService.deactivatePreview();
			EarlyPreviewService.cancelPendingPreview();
			populateParams();

			TransformationApplicationService.append(action, 'dataset', vm.lookupParams)
				.finally(function() {
					setTimeout(EarlyPreviewService.activatePreview, 500);
				});
		};

		//the lookup directive is created before the playground
		//get ALL the possible lookup actions on the current dataset
		$scope.$watch(function(){
			return vm.state.playground.dataset;
		},
		function(newDataset){
			if(newDataset){
				LookupService.getLookupPossibleActions(vm.state.playground.dataset.id)
					.then(function(dsLookup){
						vm.potentialTransformations = dsLookup.data;
					})
					.then(function(){
						if(vm.potentialTransformations.length){
							vm.loadSelectedLookupContent(vm.potentialTransformations[0]);
						}
					});
			}
		});
	}

	angular.module('data-prep.lookup')
		.controller('lookupCtrl', LookupCtrl);
})();
