/*jshint camelcase: false */

describe('Transformation Application Service service', function () {
	'use strict';


	beforeEach(module('data-prep.services.transformationApplication'));

	beforeEach(inject(function ($q, PlaygroundService, ColumnSuggestionService, EarlyPreviewService, PreviewService) {
		spyOn(EarlyPreviewService, 'deactivatePreview').and.returnValue();
		spyOn(EarlyPreviewService, 'deactivateDynamicModal').and.callThrough();
		spyOn(PlaygroundService, 'appendStep').and.returnValue($q.when());
		ColumnSuggestionService.currentColumn = {id:'0001', name:'firstname'};
		spyOn(PreviewService, 'getPreviewAddRecords').and.returnValue();
		spyOn(PreviewService, 'cancelPreview').and.returnValue();
	}));

	describe('refresh', function () {
		it('should call appendStep function on transform closure execution', inject(function (TransformationApplicationService, PlaygroundService, ColumnSuggestionService) {
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
			expect(PlaygroundService.appendStep).toHaveBeenCalledWith('tolowercase', expectedParams);

		}));

		it('should hide modal after step append', inject(function ($rootScope, TransformationApplicationService, EarlyPreviewService) {
			//given
			var transformation = {name: 'tolowercase'};
			var transfoScope = 'column';
			var params = {param: 'value'};
			EarlyPreviewService.showDynamicModal = true;

			//when
			var closure = TransformationApplicationService.transformClosure(transformation, transfoScope);
			closure(params);
			$rootScope.$digest();

			//then
			expect(EarlyPreviewService.deactivateDynamicModal).toHaveBeenCalled();
			expect(EarlyPreviewService.showDynamicModal).toBe(false);
		}));

		it('should cancel pending early preview on step append', inject(function ($timeout, PreviewService, EarlyPreviewService, TransformationApplicationService, PlaygroundService) {
			//given
			var transformation = {name: 'tolowercase'};
			var transfoScope = 'column';
			var params = {param: 'value'};
			PlaygroundService.currentMetadata = {id : '0004'}

			EarlyPreviewService.earlyPreview(transformation, transfoScope)(params);

			//when
			var closure = TransformationApplicationService.transformClosure(transformation, transfoScope);
			closure(params);

			//then : preview should be disabled
			$timeout.flush();
			expect(PreviewService.getPreviewAddRecords).not.toHaveBeenCalled();
		}));

	});
});