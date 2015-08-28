(function() {
	'use strict';

	/**
	 * @ngdoc service
	 * @name TransformationApplicationService
	 * @description applies a transformation on the given data
	 * @requires data-prep.services.playground.service: PlaygroundService
	 * @requires data-prep.services.transformation.service: ColumnSuggestionService
	 * @requires data-prep.services.playground.service: EarlyPreviewService
	 */
	function TransformationApplicationService(PlaygroundService, ColumnSuggestionService, EarlyPreviewService) {
		/**
		 * @ngdoc method
		 * @name transformClosure
		 * @methodOf data-prep.services.transformationApplication.service:TransformationApplicationService
		 * @description Transformation application closure. It take the transformation to build the closure.
		 * The closure then takes the parameters and append the new step in the current preparation
		 */
		this.transformClosure = function transformClosure(transfo, transfoScope) {
			/*jshint camelcase: false */
			var currentCol = ColumnSuggestionService.currentColumn;
			return function(params) {
				EarlyPreviewService.deactivatePreview();
				EarlyPreviewService.cancelPendingPreview();

				params = params || {};
				params.scope = transfoScope;
				params.column_id = currentCol.id;
				params.column_name = currentCol.name;

				PlaygroundService.appendStep(transfo.name, params)
					.then(EarlyPreviewService.deactivateDynamicModal)
					.finally(function() {
						setTimeout(EarlyPreviewService.activatePreview, 500);
					});
			};
		};

	}

	angular.module('data-prep.services.transformationApplication')
		.service('TransformationApplicationService', TransformationApplicationService);
})();