describe('horizontalBarchart directive', function () {
    'use strict';

    var createElement, element, scope, statsData, filteredStatsData;

    beforeEach(module('talend.widget'));

    beforeEach(inject(function ($rootScope, $compile) {
        statsData = [
            {'data': 'Johnson', 'occurrences': 9},
            {'data': 'Roosevelt', 'occurrences': 8},
            {'data': 'Pierce', 'occurrences': 6},
            {'data': 'Wilson', 'occurrences': 5},
            {'data': 'Adams', 'occurrences': 4},
            {'data': 'Quincy', 'occurrences': 4},
            {'data': 'Clinton', 'occurrences': 4},
            {'data': 'Harrison', 'occurrences': 4}
        ];
        filteredStatsData = [
            {'data': 'Johnson', 'filteredOccurrences': 4},
            {'data': 'Roosevelt', filteredOccurrences: 4},
            {'data': 'Pierce', 'filteredOccurrences': 4},
            {'data': 'Wilson', 'filteredOccurrences': 4},
            {'data': 'Adams', 'filteredOccurrences': 4},
            {'data': 'Quincy', 'filteredOccurrences': 4},
            {'data': 'Clinton', 'filteredOccurrences': 4},
            {'data': 'Harrison', 'filteredOccurrences': 4}
        ];

        scope = $rootScope.$new();
        scope.onClick = jasmine.createSpy('on bar click fn');

        createElement = function () {
            var html = '<horizontal-barchart ' +
                'id="barChart" ' +
                'width="250" ' +
                'height="400"' +
                'on-click="onClick(item)"' +
                'key-field="data"' +
                'primary-data="primaryData"' +
                'primary-value-field="occurrences"' +
                'primary-bar-class="{{primaryBarClass}}"' +
                'secondary-data="secondaryData"' +
                'secondary-value-field="filteredOccurrences"' +
                'secondary-bar-class="{{secondaryBarClass}}"' +
                '></horizontal-barchart>';

            element = angular.element(html);
            angular.element('body').append(element);
            $compile(element)(scope);
            scope.$digest();
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

    describe('render', function () {
        it('should render labels after a 100ms delay', function () {
            //given
            createElement();

            //when
            scope.primaryData = statsData;
            scope.$digest();
            jasmine.clock().tick(100);

            //then
            expect(element.find('.label').length).toBe(statsData.length);
            expect(element.find('.label').eq(0).text()).toBe('Johnson');
            expect(element.find('.label').eq(1).text()).toBe('Roosevelt');
            expect(element.find('.label').eq(2).text()).toBe('Pierce');
            expect(element.find('.label').eq(3).text()).toBe('Wilson');
            expect(element.find('.label').eq(4).text()).toBe('Adams');
            expect(element.find('.label').eq(5).text()).toBe('Quincy');
            expect(element.find('.label').eq(6).text()).toBe('Clinton');
            expect(element.find('.label').eq(7).text()).toBe('Harrison');
        });

        it('should render hover rect after a 100ms delay', function () {
            //given
            createElement();

            //when
            scope.primaryData = statsData;
            scope.$digest();
            jasmine.clock().tick(100);

            //then
            expect(element.find('.bg-rect').length).toBe(statsData.length);
        });

        it('should render grid after a 100ms delay', function () {
            //given
            createElement();

            //when
            scope.primaryData = statsData;
            scope.$digest();
            jasmine.clock().tick(100);

            //then
            expect(element.find('.grid text').length).toBe(5);
            expect(element.find('.grid text').eq(0).text()).toBe('0');
            expect(element.find('.grid text').eq(1).text()).toBe('2');
            expect(element.find('.grid text').eq(2).text()).toBe('4');
            expect(element.find('.grid text').eq(3).text()).toBe('6');
            expect(element.find('.grid text').eq(4).text()).toBe('8');
        });

        it('should render primary data after a 100ms delay', function () {
            //given
            createElement();

            //when
            scope.primaryData = statsData;
            scope.$digest();
            jasmine.clock().tick(100);

            //then
            expect(element.find('.primaryBar > rect').length).toBe(statsData.length);
            expect(element.find('.secondaryBar > rect').length).toBe(0);
        });

        it('should render primary and secondary data after a 100ms delay', function () {
            //given
            createElement();

            //when
            scope.primaryData = statsData;
            scope.secondaryData = filteredStatsData;
            scope.$digest();
            jasmine.clock().tick(100);

            //then
            expect(element.find('.primaryBar > rect').length).toBe(statsData.length);
            expect(element.find('.secondaryBar > rect').length).toBe(statsData.length);
        });

        it('should render secondary data after a 100ms delay', function () {
            //given
            createElement();
            scope.primaryData = statsData;
            scope.$digest();
            jasmine.clock().tick(100);

            expect(element.find('.secondaryBar > rect').length).toBe(0);

            //when
            scope.secondaryData = filteredStatsData;
            scope.$digest();
            jasmine.clock().tick(100);

            //then
            expect(element.find('.secondaryBar > rect').length).toBe(statsData.length);
        });
    });

    describe('bar class name', function () {
        it('should set "transparentBar" class as primary bars default class name', function () {
            //given
            createElement();

            //when
            scope.primaryData = statsData;
            scope.$digest();
            jasmine.clock().tick(100);

            //then
            expect(element.find('.primaryBar > rect.transparentBar').length).toBe(statsData.length);
        });

        it('should set "blueBar" class as secondary bars default class name', function () {
            //given
            createElement();

            //when
            scope.primaryData = statsData;
            scope.secondaryData = filteredStatsData;
            scope.$digest();
            jasmine.clock().tick(100);

            //then
            expect(element.find('.secondaryBar > rect.blueBar').length).toBe(statsData.length);
        });

        it('should set custom secondary bars class name', function () {
            //given
            createElement();

            //when
            scope.primaryData = statsData;
            scope.primaryBarClass = 'blueBar';
            scope.secondaryData = filteredStatsData;
            scope.secondaryBarClass = 'brownBar';
            scope.$digest();
            jasmine.clock().tick(100);

            //then
            expect(element.find('.primaryBar > rect.blueBar').length).toBe(statsData.length);
            expect(element.find('.secondaryBar > rect.brownBar').length).toBe(statsData.length);
        });
    });
});