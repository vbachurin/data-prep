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
						TransformationApplicationService, PlaygroundService, $timeout) {
		var vm = this;
		vm.state = state;
		vm.cancelEarlyPreview = EarlyPreviewService.cancelEarlyPreview;
		vm.loadFromAction= LookupService.loadFromAction;
		vm.addLookupDatasetModal = false;


		/**
		 * @ngdoc property
		 * @name sortList
		 * @propertyOf data-prep.lookup.controller:LookupCtrl
		 * @description The sort list
		 * @type {array}
		 */
		vm.sortList = [
			{id: 'name', name: 'NAME_SORT'},
			{id: 'created', name: 'DATE_SORT'}
		];

		/**
		 * @ngdoc property
		 * @name orderList
		 * @propertyOf data-prep.lookup.controller:LookupCtrl
		 * @description The sort order list
		 * @type {string}
		 */
		vm.orderList = [
			{id: 'asc', name: 'ASC_ORDER'},
			{id: 'desc', name: 'DESC_ORDER'}
		];

		/**
		 * @ngdoc property
		 * @name sortSelected
		 * @propertyOf data-prep.dataset-list.controller:DatasetListCtrl
		 * @description Selected sort.
		 * @type {object}
		 */
		vm.sortSelected = vm.sortList[0];

		/**
		 * @ngdoc property
		 * @name sortOrderSelected
		 * @propertyOf data-prep.dataset-list.controller:DatasetListCtrl
		 * @description Selected sort order.
		 * @type {object}
		 */
		vm.sortOrderSelected = vm.orderList[0];

		/**
		 * @ngdoc method
		 * @name hoverSubmitBtn
		 * @methodOf data-prep.lookup.controller:LookupCtrl
		 * @description trigger a preview mode on the main dataset to show the lookup action effet
		 */
		vm.hoverSubmitBtn = function hoverSubmitBtn(){
			if (state.playground.lookup.step) {
				PlaygroundService.updatePreview(state.playground.lookup.step, getParams());
			} else {
				var previewClosure = EarlyPreviewService.earlyPreview(state.playground.lookup.dataset, 'dataset');
				previewClosure(getParams());
			}
		};

		/**
		 * @ngdoc method
		 * @name getDsName
		 * @methodOf data-prep.lookup.controller:LookupCtrl
		 * @param {object} item dataset lookup action
		 * @returns {String} the name of th dataset to be shown in the list
		 * @description loops over the dataset lookup action parameters to collect the dataset name
		 */
		vm.getDsName = function getDsName (item){
			return _.find(item.parameters, {name:'lookup_ds_name'}).default;
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
			var params = extractLookupParams(state.playground.lookup.dataset);
			/*jshint camelcase: false */
			params.column_id = state.playground.grid.selectedColumn.id;
			params.column_name = state.playground.grid.selectedColumn.name;
			params.lookup_join_on = state.playground.lookup.selectedColumn.id;
			params.lookup_join_on_name = state.playground.lookup.selectedColumn.name;
			params.lookup_selected_cols = state.playground.lookup.columnsToAdd;
			return params;
		}

		/**
		 * @ngdoc method
		 * @name submit
		 * @methodOf data-prep.lookup.controller:LookupCtrl
		 * @description submits the lookup action
		 */
		vm.submit = function submit() {
			EarlyPreviewService.deactivatePreview();
			EarlyPreviewService.cancelPendingPreview();
			var promise;
			var lookupStep = vm.state.playground.lookup.step;

			if (lookupStep) {
				promise = PlaygroundService.updateStep(lookupStep, getParams());
			}
			else {
				promise = TransformationApplicationService.append(state.playground.lookup.dataset, 'dataset', getParams());
			}

			promise.then(StateService.setLookupVisibility.bind(null, false))
				.finally(function () {
					setTimeout(EarlyPreviewService.activatePreview, 500);
				});
		};

		/**
		 * @ngdoc method
		 * @name openAddLookupDatasetModal
		 * @methodOf data-prep.lookup.controller:LookupCtrl
		 * @description Open the add lookup dataset modal
		 */
		vm.openAddLookupDatasetModal = function openAddLookupDatasetModal() {

			LookupService.disableDatasetsUsedInRecipe();

			vm.addLookupDatasetModal = true;
		};

		/**
		 * @ngdoc method
		 * @name addLookupDatasets
		 * @methodOf data-prep.lookup.controller:LookupCtrl
		 * @description Add datasets to the lookup
		 */
		vm.addLookupDatasets = function addLookupDatasets() {

			LookupService.updateLookupDatasets();

			vm.addLookupDatasetModal = false;

			//refresh lookup panel by selecting the first action
			if(state.playground.lookup.addedActions.length > 0) {
				LookupService.loadFromAction(state.playground.lookup.addedActions[0]);
			}
		};

		/**
		 * @ngdoc method
		 * @name toogleSelect
		 * @methodOf data-prep.lookup.controller:LookupCtrl
		 * @description Select/Deselect a dataset
		 */
		vm.toogleSelect = function toogleSelect(dataset) {
			if(dataset.enableToAddToLookup) {
				dataset.addedToLookup = !dataset.addedToLookup;
			}
		};

		/**
		 * @ngdoc method
		 * @name sortDatasetsList
		 * @methodOf data-prep.lookup.controller:LookupCtrl
		 * @description sort datasets list
		 */
		function sortDatasetsList() {
			state.playground.lookup.datasets =_.sortBy(state.playground.lookup.datasets, vm.sortSelected.id);
			if(vm.sortOrderSelected.id === 'desc'){
				state.playground.lookup.datasets = state.playground.lookup.datasets.reverse();
			}
		}

		/**
		 * @ngdoc method
		 * @name sort
		 * @methodOf data-prep.lookup.controller:LookupCtrl
		 * @description sort dataset by sortType by calling refreshDatasets from DatasetService
		 * @param {object} sortType Criteria to sort
		 */
		vm.updateSortBy = function updateSortBy (sortType) {
			$timeout(function(){
				if (vm.sortSelected === sortType) {
					return;
				}
				vm.sortSelected = sortType;
				sortDatasetsList();
			});
		};

		/**
		 * @ngdoc method
		 * @name sort
		 * @methodOf data-prep.lookup.controller:LookupCtrl
		 * @description sort dataset in order (ASC or DESC) by calling refreshDatasets from DatasetService
		 * @param {object} order Sort order ASC(ascending) or DESC(descending)
		 */
		vm.updateSortOrder = function updateSortOrder (order) {
			$timeout(function(){
				if (vm.sortOrderSelected === order) {
					return;
				}
				vm.sortOrderSelected = order;
				sortDatasetsList()
			});
		};

	}

	angular.module('data-prep.lookup')
		.controller('LookupCtrl', LookupCtrl);
})();
