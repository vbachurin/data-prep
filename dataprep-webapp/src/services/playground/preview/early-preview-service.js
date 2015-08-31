(function() {
	'use strict';

	/**
	 * @ngdoc service
	 * @name EarlyPreviewService
	 * @description launches a preview before the application of the transformation
	 * @requires data-prep.services.transformation.service:SuggestionService
	 * @requires data-prep.services.recipe.service:RecipeService
	 * @requires data-prep.services.playground.service:PreviewService
	 * @requires data-prep.services.playground.service:PlaygroundService
	 */
	function EarlyPreviewService($timeout, PlaygroundService, SuggestionService, RecipeService, PreviewService) {
		var self = this;
		self.previewDisabled = false;
		self.showDynamicModal = false;
		var previewTimeout;
		var previewCancelerTimeout;

		/**
		 * @ngdoc method
		 * @name deactivatePreview
		 * @methodOf data-prep.services.playground.service:EarlyPreviewService
		 * @description deactivates the preview
		 */
		self.deactivatePreview = function deactivatePreview (){
			self.previewDisabled = true;
		};

		/**
		 * @ngdoc method
		 * @name activatePreview
		 * @methodOf data-prep.services.playground.service:EarlyPreviewService
		 * @description activates the preview
		 */
		self.activatePreview = function activatePreview (){
			self.previewDisabled = false;
		};

		/**
		 * @ngdoc method
		 * @name deactivateDynamicModal
		 * @methodOf data-prep.services.playground.service:EarlyPreviewService
		 * @description hides the dynamic modal
		 */
		self.deactivateDynamicModal = function activatePreview (){
			self.showDynamicModal = false;
		};

		/**
		 * @ngdoc method
		 * @name cancelPendingPreview
		 * @methodOf data-prep.services.playground.service:EarlyPreviewService
		 * @description disables the pending previews
		 */
		self.cancelPendingPreview = function cancelPendingPreview() {
			$timeout.cancel(previewTimeout);
			$timeout.cancel(previewCancelerTimeout);
		};

		/**
		 * @ngdoc method
		 * @name earlyPreview
		 * @methodOf data-prep.services.playground.service:EarlyPreviewService
		 * @param {object} transformation The transformation
		 * @param {string} transfoScope The transformation scope
		 * @description Perform an early preview (preview before transformation application) after a 200ms delay
		 */
		self.earlyPreview = function earlyPreview(transformation, transfoScope) {
			/*jshint camelcase: false */
			var currentCol = SuggestionService.currentColumn;
			return function(params) {
				if(self.previewDisabled) {
					return;
				}

				self.cancelPendingPreview();

				previewTimeout = $timeout(function() {
					params.scope = transfoScope;
					params.column_id = currentCol.id;
					params.column_name = currentCol.name;

					var datasetId = PlaygroundService.currentMetadata.id;

					RecipeService.earlyPreview(currentCol, transformation, params);
					PreviewService.getPreviewAddRecords(datasetId, transformation.name, params);
				}, 300);
			};
		};

		/**
		 * @ngdoc method
		 * @name cancelEarlyPreview
		 * @methodOf data-prep.services.playground.service:EarlyPreviewService
		 * @description Cancel any current or pending early preview
		 */
		self.cancelEarlyPreview = function cancelEarlyPreview() {
			if(self.previewDisabled) {
				return;
			}

			self.cancelPendingPreview();

			previewCancelerTimeout = $timeout(function() {
				RecipeService.cancelEarlyPreview();
				PreviewService.cancelPreview();
			}, 100);
		};
	}

	angular.module('data-prep.services.playground')
		.service('EarlyPreviewService', EarlyPreviewService);
})();