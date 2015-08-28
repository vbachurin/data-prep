/*jshint camelcase: false */

describe('Transformation Application Service', function () {
	'use strict';


	beforeEach(module('data-prep.services.transformationApplication'));

	beforeEach(inject(function ($q, PlaygroundService, ColumnSuggestionService, EarlyPreviewService, PreviewService, RecipeService) {
		spyOn(EarlyPreviewService, 'deactivatePreview').and.returnValue();
		spyOn(EarlyPreviewService, 'deactivateDynamicModal').and.returnValue();
		spyOn(EarlyPreviewService, 'cancelPendingPreview').and.returnValue();
		spyOn(EarlyPreviewService, 'activatePreview').and.returnValue();

		spyOn(PlaygroundService, 'appendStep').and.returnValue($q.when());
		ColumnSuggestionService.currentColumn = {id:'0001', name:'firstname'};
	}));

	describe('Appending Steps', function () {
		it('should call appendStep function on transform closure execution', inject(function (TransformationApplicationService, PlaygroundService, EarlyPreviewService) {
			///given
			var transformation = {name: 'tolowercase'};
			var transfoScope = 'column';
			var params = {param: 'value'};
			var column = {id: '0001', name: 'firstname'};


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
			expect(EarlyPreviewService.deactivatePreview).toHaveBeenCalled();
			expect(EarlyPreviewService.cancelPendingPreview).toHaveBeenCalledWith();
			expect(PlaygroundService.appendStep).toHaveBeenCalledWith('tolowercase', expectedParams);

		}));

		it('should hide modal after step append', inject(function ($rootScope, TransformationApplicationService, EarlyPreviewService) {
			//given
			var transformation = {name: 'tolowercase'};
			var transfoScope = 'column';
			var params = {param: 'value'};
			jasmine.clock().install();

			//when
			var closure = TransformationApplicationService.transformClosure(transformation, transfoScope);
			closure(params);
			$rootScope.$digest();
			jasmine.clock().tick(500);

			//then
			expect(EarlyPreviewService.deactivateDynamicModal).toHaveBeenCalled();
			expect(EarlyPreviewService.activatePreview).toHaveBeenCalled();
			jasmine.clock().uninstall();
		}));
	});
});