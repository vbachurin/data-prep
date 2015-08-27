/*jshint camelcase: false */

describe('Transformation Application Service service', function () {
	'use strict';


	beforeEach(module('data-prep.services.transformationApplication'));

	beforeEach(inject(function ($q, PlaygroundService, ColumnSuggestionService, EarlyPreviewService) {
		spyOn(EarlyPreviewService, 'deactivatePreview').and.returnValue();
		spyOn(EarlyPreviewService, 'deactivateDynamicModal').and.returnValue();
		spyOn(PlaygroundService, 'appendStep').and.returnValue($q.when());
		//spyOn(PreparationService, 'getDetails').and.returnValue($q.when({
		//	data: preparationDetails()
		//}));
		//spyOn(TransformationService, 'resetParamValue').and.returnValue();
		//spyOn(TransformationService, 'initDynamicParameters').and.callFake(function (transformation) {
		//	transformation.cluster = initialCluster();
		//	return $q.when(transformation);
		//});
		//spyOn(TransformationService, 'initParamsValues').and.callThrough();
	}));

	describe('refresh', function () {
		it('should reset recipe item list when no preparation is loaded', inject(function (TransformationApplicationService, PlaygroundService, ColumnSuggestionService) {
			///given
			var transformation = {name: 'tolowercase'};
			var transfoScope = 'column';
			var params = {param: 'value'};
			var column = {id: '0001', name: 'firstname'};
			ColumnSuggestionService.currentColumn = {id:'0001', name:'firstname'};

			//when
			var closure = TransformationApplicationService.transformClosure(transformation, transfoScope);
			closure(params);

			//then
			var expectedParams = {
				param: 'value',
				scope: transfoScope,
				column_id: column.id,
				column_name: column.name
			};
			expect(PlaygroundService.appendStep).toHaveBeenCalledWith('tolowercase', expectedParams);

		}));

		it('should get recipe with no params when a preparation is loaded', inject(function ($rootScope, RecipeService, PreparationService) {
			//given


			//when


			//then

		}));
	});
});