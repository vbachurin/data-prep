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
	function LookupCtrl($scope, state, DatasetLookupService, EarlyPreviewService, TransformationApplicationService) {
		var vm = this;
		vm.state = state;

		vm.datasetLookupService = DatasetLookupService;
		vm.earlyPreview = EarlyPreviewService.earlyPreview;
		vm.cancelEarlyPreview = EarlyPreviewService.cancelEarlyPreview;

		vm.hoverSubmitBtn = function hoverSubmitBtn(){
			//vm.setLookupParams();
			var previewClosure = vm.earlyPreview(vm.lookupAction, 'dataset');
			return function(params){
				params.column_id = vm.state.playground.grid.selectedColumn.id;
				params.lookup_join_on = vm.joinOnId;
				params.lookup_join_on_name = vm.joinOnName;
				params.lookup_selected_cols = vm.lookupColumns.split(',');
				previewClosure(params);
			};
		};

		vm.loadSelectedLookupContent = function(lookupDs){
			vm.lookupAction = lookupDs;
			var lookupDsUrl = lookupDs.parameters[6].default;
			vm.datasetLookupService.loadLookupContent(lookupDsUrl);
		};

		vm.joinOnId = '0000';
		vm.joinOnName = 'identif';
		vm.lookupColumns = '0001';

		vm.submitLookup = function submitLookup(action, scope) {
			return function(params) {
				EarlyPreviewService.deactivatePreview();
				EarlyPreviewService.cancelPendingPreview();

				params.column_id = vm.state.playground.grid.selectedColumn.id;
				params.lookup_join_on = vm.joinOnId;
				params.lookup_join_on_name = vm.joinOnName;
				params.lookup_selected_cols = vm.lookupColumns.split(',');

				TransformationApplicationService.append(action, scope, params)
					.finally(function() {
						setTimeout(EarlyPreviewService.activatePreview, 500);
					});
			};
		};

		//vm.hoverSubmitLookup = function hoverSubmitLookup () {
		//	console.log('lookup submit hovered');
		//};

		//vm.leaveSubmitLookup = function leaveSubmitLookup (){
		//	console.log('lookup submit left');
		//};

		//the lookup directive and the
		$scope.$watch(function(){
			return vm.state.playground.dataset;
		},
		function(newDataset){
			if(newDataset){
				DatasetLookupService.getLookupPossibleActions(vm.state.playground.dataset.id)
					.then(function(dsLookup){
						vm.potentialLookups = dsLookup.actionsFormat;
						vm.potentialTransformations = dsLookup.transformationFormat;
					})
					.then(function(){
						vm.loadSelectedLookupContent(vm.potentialTransformations[0]);
					});
			}
		});
	}

	angular.module('data-prep.lookup')
		.controller('lookupCtrl', LookupCtrl);
})();
