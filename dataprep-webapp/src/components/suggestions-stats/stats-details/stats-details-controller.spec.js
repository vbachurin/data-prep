describe('Stats-details controller', function () {
    'use strict';

    var createController, scope;

    beforeEach(module('data-prep.stats-details'));

    beforeEach(inject(function ($rootScope, $controller) {
        scope = $rootScope.$new();

        createController = function () {
            var ctrl = $controller('StatsDetailsCtrl', {
                $scope: scope
            });
            return ctrl;
        };
    }));

    it('should bind statistics getter to ColumnSuggestionService.statistics', inject(function (StatisticsService) {
        //given
        var ctrl = createController();
        expect(ctrl.statistics).toBeFalsy();

        var stats = {};

        //when
        StatisticsService.statistics = stats;

        //then
        expect(ctrl.statistics).toBe(stats);
    }));

    it('should bind boxplotData getter to ColumnSuggestionService.statistics', inject(function (StatisticsService) {
        //given
        var ctrl = createController();
        expect(ctrl.statistics).toBeFalsy();

        var data = {};

        //when
        StatisticsService.boxplotData = data;

        //then
        expect(ctrl.boxplotData).toBe(data);
    }));

    it('should bind patternFrequencyTable getter to ColumnSuggestionService.currentColumn.patternFrequencyTable', inject(function (ColumnSuggestionService) {
        //given
        var ctrl = createController();
        expect(ctrl.patternFrequencyTable).toBeFalsy();

        var patternFrequencyTable = {};

        //when
        ColumnSuggestionService.currentColumn = {statistics: {patternFrequencyTable: patternFrequencyTable}};

        //then
        expect(ctrl.patternFrequencyTable).toBe(patternFrequencyTable);
    }));

});