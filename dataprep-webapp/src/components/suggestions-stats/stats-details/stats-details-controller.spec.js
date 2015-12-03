describe('Stats-details controller', function () {
    'use strict';

    var createController, scope, stateMock;

    beforeEach(module('data-prep.stats-details', function($provide) {
        stateMock = {
            playground: {
                grid: {}
            }
        };
        $provide.constant('state', stateMock);
    }));

    beforeEach(inject(function ($rootScope, $controller) {
        scope = $rootScope.$new();

        createController = function () {
            return $controller('StatsDetailsCtrl', {
                $scope: scope
            });
        };
    }));

    it('should bind statistics getter to StatisticsService.statistics', inject(function (StatisticsService) {
        //given
        var ctrl = createController();
        expect(ctrl.statistics).toBeFalsy();

        var stats = {};

        //when
        StatisticsService.statistics = stats;

        //then
        expect(ctrl.statistics).toBe(stats);
    }));

    it('should bind boxplotData getter to StatisticsService.boxplotData', inject(function (StatisticsService) {
        //given
        var ctrl = createController();
        expect(ctrl.statistics).toBeFalsy();

        var data = {};

        //when
        StatisticsService.boxPlot = data;

        //then
        expect(ctrl.boxPlot).toBe(data);
    }));

    it('should bind patternFrequencyTable getter to state selected column patternFrequencyTable', function () {
        //given
        var ctrl = createController();
        expect(ctrl.patternFrequencyTable).toBeFalsy();

        var patternFrequencyTable = {};

        //when
        stateMock.playground.grid.selectedColumn = {statistics: {patternFrequencyTable: patternFrequencyTable}};

        //then
        expect(ctrl.patternFrequencyTable).toBe(patternFrequencyTable);
    });

});