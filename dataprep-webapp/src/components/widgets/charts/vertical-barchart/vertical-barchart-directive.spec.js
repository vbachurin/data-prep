describe('verticalBarchart directive', function () {
    'use strict';

    var createElement, element, scope, statsData, secondaryStatsData, isolateScope;
    var flushAllD3Transitions = function () {
        var now = Date.now;
        Date.now = function () {
            return Infinity;
        };
        d3.timer.flush();
        Date.now = now;
    };

    beforeEach(module('talend.widget'));
    beforeEach(inject(function ($rootScope, $compile) {
        statsData = [
            {'data': {min: 0, max: 5}, 'occurrences': 9},
            {'data': {min: 5, max: 10}, 'occurrences': 8},
            {'data': {min: 10, max: 15}, 'occurrences': 6},
            {'data': {min: 15, max: 20}, 'occurrences': 5}
        ];
        secondaryStatsData = [
            {'data': {min: 0, max: 5}, 'filteredOccurrences': 9},
            {'data': {min: 5, max: 10}, 'filteredOccurrences': 8},
            {'data': {min: 10, max: 15}, 'filteredOccurrences': 6},
            {'data': {min: 15, max: 20}, 'filteredOccurrences': 5}
        ];

        createElement = function () {

            scope = $rootScope.$new();
            scope.visData = null;
            scope.existingFilter = null;
            scope.onClick = jasmine.createSpy('onClick');

            element = angular.element('<vertical-barchart id="barChart" width="250" height="400"' +
                'on-click="onClick(interval)"' +
                'visu-data="visData"' +
                'visu-data-2="visData2"' +
                'key-field="data"' +
                'active-limits="existingFilter"' +
                'value-field="occurrences"' +
                'value-field-2="filteredOccurrences"' +
                '></vertical-barchart>');

            angular.element('body').append(element);
            $compile(element)(scope);
            scope.$digest();

            isolateScope = element.isolateScope();
        };
    }));

    beforeEach(function () {
        jasmine.clock().install();
    });
    afterEach(function () {
        jasmine.clock().uninstall();

        scope.$destroy();
        element.remove();
    });

    it('should render all bars after a 100ms delay', function () {
        //given
        createElement();

        //when
        scope.visData = statsData;
        scope.visData2 = secondaryStatsData;
        scope.$digest();
        jasmine.clock().tick(100);

        //then
        expect(element.find('.grid').length).toBe(1);
        expect(element.find('rect').length).toBe(statsData.length * 3); // 3 chart columns * (main + secondary + hover)

        expect(element.find('g.bar').length).toBe(1);
        expect(element.find('rect.bar').length).toBe(statsData.length);

        expect(element.find('g.secondaryBar').length).toBe(1);
        expect(element.find('rect.secondaryBar').length).toBe(secondaryStatsData.length);

        expect(element.find('.bg-rect').length).toBe(statsData.length);
    });

    it('should render main bars after a 100ms delay', function () {
        //given
        createElement();

        //when
        scope.visData = statsData;
        scope.visData2 = null;
        scope.$digest();
        jasmine.clock().tick(100);

        //then
        expect(element.find('.grid').length).toBe(1);
        expect(element.find('rect').length).toBe(statsData.length * 2); // 3 chart columns * (main + hover)

        expect(element.find('g.bar').length).toBe(1);
        expect(element.find('rect.bar').length).toBe(statsData.length);

        expect(element.find('g.secondaryBar').length).toBe(0);
        expect(element.find('rect.secondaryBar').length).toBe(0);

        expect(element.find('.bg-rect').length).toBe(statsData.length);
    });

    it('should render secondary bars after a 100ms delay', function () {
        //given
        createElement();

        scope.visData = statsData;
        scope.visData2 = null;
        scope.$digest();
        jasmine.clock().tick(100);

        expect(element.find('g.secondaryBar').length).toBe(0);
        expect(element.find('rect.secondaryBar').length).toBe(0);

        //when
        scope.visData2 = secondaryStatsData;
        scope.$digest();
        jasmine.clock().tick(100);

        //then
        expect(element.find('g.secondaryBar').length).toBe(1);
        expect(element.find('rect.secondaryBar').length).toBe(secondaryStatsData.length);
    });

    it('should check the initial bars opacity', function () {
        //given
        createElement();

        //when
        scope.visData = statsData;
        scope.$digest();
        jasmine.clock().tick(100);

        //then
        _.each(isolateScope.buckets[0], function (bucket) {
            expect(d3.select(bucket).style('opacity')).toBe('1');
        });
    });

    it('should update the bars opacity after applying a filter outside the bars ranges', function () {
        //given
        createElement();

        scope.visData = statsData;
        scope.$digest();
        jasmine.clock().tick(100);
        flushAllD3Transitions();

        //when
        scope.existingFilter = [105, 200];
        scope.$digest();
        jasmine.clock().tick(600);

        flushAllD3Transitions();

        //then
        _.each(isolateScope.buckets[0], function (bucket) {
            var opac = +(d3.select(bucket).style('opacity'));
            expect(opac.toFixed(1)).toBe('0.4');
        });
    });

    it('should update the bars opacity after applying a filter intersecting with 1 bar range', function () {
        //given
        createElement();

        scope.visData = statsData;
        scope.$digest();
        jasmine.clock().tick(100);
        flushAllD3Transitions();

        //when
        scope.existingFilter = [15, 20];
        scope.$digest();
        jasmine.clock().tick(600);

        flushAllD3Transitions();

        //then
        var opacities = [0.4, 0.4, 0.4, 1];

        _.each(isolateScope.buckets[0], function (bucket, index) {
            var opac = +(d3.select(bucket).style('opacity'));
            opac = +opac.toFixed(1);
            expect(opac).toBe(opacities[index]);
        });
    });

    it('should update the bars opacity after applying a filter intersecting with to 2 bar range', function () {
        //given
        createElement();

        scope.visData = statsData;
        scope.$digest();
        jasmine.clock().tick(100);
        flushAllD3Transitions();

        //when
        scope.existingFilter = [13, 20];
        scope.$digest();
        jasmine.clock().tick(600);

        flushAllD3Transitions();

        //then
        var opacities = [0.4, 0.4, 1, 1];

        _.each(isolateScope.buckets[0], function (bucket, index) {
            var opac = +(d3.select(bucket).style('opacity'));
            opac = +opac.toFixed(1);
            expect(opac).toBe(opacities[index]);
        });
    });
});