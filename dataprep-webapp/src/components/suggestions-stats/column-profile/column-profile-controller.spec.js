describe('ColumnProfile controller', function () {
    'use strict';

    var createController, scope;

	beforeEach(module('data-prep.column-profile'));
	beforeEach(module('data-prep.suggestions-stats'));

    beforeEach(inject(function ($rootScope, $controller) {
        scope = $rootScope.$new();

        createController = function () {
            var ctrl = $controller('ColumnProfileCtrl', {
                $scope: scope
            });
            return ctrl;
        };
    }));

    it('should call addFilter Function of the StatisticsService', inject(function (StatisticsService) {
        //given
        spyOn(StatisticsService, 'addFilter').and.returnValue();
        var ctrl = createController();
        var obj = {'data': 'Ulysse', 'occurrences': 5};

        //when
        ctrl.barchartClickFn(obj);

        //then
        expect(StatisticsService.addFilter).toHaveBeenCalledWith(obj.data);
    }));

    it('should bind processedData getter to StatisticsService.data', inject(function (StatisticsService) {
        //given
        var data = {};
        var ctrl = createController();

        //when
        StatisticsService.data = data;

        //then
        expect(ctrl.processedData).toBe(data);
    }));

    it('should update charts using aggregation by defaut', inject(function (StatisticsService, SuggestionsStatsAggregationsService) {
        //given
        spyOn(SuggestionsStatsAggregationsService,'updateAggregationsChanges').and.returnValue();
        spyOn(StatisticsService,'processVisuDataAggregation').and.returnValue();
        spyOn(StatisticsService,'processNonMapData').and.returnValue();

        var ctrl = createController();

        ctrl.datasetAggregationsService.columnSelected = {'id':'000', 'data':'Ulysse', 'occurrences':5};
        //when
        ctrl.updateCharts(null, null);

        //then
        expect(SuggestionsStatsAggregationsService.updateAggregationsChanges).toHaveBeenCalledWith(null,null);
        expect(StatisticsService.processVisuDataAggregation).not.toHaveBeenCalled();
        expect(StatisticsService.processNonMapData).toHaveBeenCalledWith(ctrl.datasetAggregationsService.columnSelected);
    }));

    it('should update charts using aggregation selected', inject(function (StatisticsService, SuggestionsStatsAggregationsService, PlaygroundService) {
        //given
        spyOn(SuggestionsStatsAggregationsService,'updateAggregationsChanges').and.returnValue();
        spyOn(StatisticsService,'processVisuDataAggregation').and.returnValue();
        spyOn(StatisticsService,'processNonMapData').and.returnValue();

        var ctrl = createController();

        ctrl.datasetAggregationsService.columnSelected = {'id':'001', 'name':'city'};
        PlaygroundService.currentMetadata = {'id':'abcd-abcf', 'name':'city'};
        var aggregationCalculation = {id: 'max', name: 'MAX'};
        var aggregationColumn = {'id':'001', 'name':'Ulysse'};
        //when
        ctrl.updateCharts(aggregationColumn, aggregationCalculation);

        //then
        expect(SuggestionsStatsAggregationsService.updateAggregationsChanges).toHaveBeenCalledWith(aggregationColumn,aggregationCalculation);
        expect(StatisticsService.processVisuDataAggregation).toHaveBeenCalledWith(
            PlaygroundService.currentMetadata.id,
            ctrl.datasetAggregationsService.columnSelected,
            aggregationColumn,
            aggregationCalculation);
        expect(StatisticsService.processNonMapData).not.toHaveBeenCalled();
    }));
});