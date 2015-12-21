describe('horizontalBarchart directive', function () {
    'use strict';

    var createElement, createElementWithSecondaryValues, element, scope, statsData;

    beforeEach(module('talend.widget'));
    beforeEach(inject(function ($rootScope, $compile) {
        statsData = [
            {'data': 'Johnson', 'occurrences': 9, 'filteredOccurrences': 4},
            {'data': 'Roosevelt', 'occurrences': 8, 'filteredOccurrences': 4},
            {'data': 'Pierce', 'occurrences': 6, 'filteredOccurrences': 4},
            {'data': 'Wilson', 'occurrences': 5, 'filteredOccurrences': 4},
            {'data': 'Adams', 'occurrences': 4, 'filteredOccurrences': 4},
            {'data': 'Quincy', 'occurrences': 4, 'filteredOccurrences': 4},
            {'data': 'Clinton', 'occurrences': 4, 'filteredOccurrences': 4},
            {'data': 'Harrison', 'occurrences': 4, 'filteredOccurrences': 4}
        ];

        scope = $rootScope.$new();
        scope.onclick = jasmine.createSpy('click');

        createElement = function () {
            var html = '<horizontal-barchart ' +
                'id="barChart" ' +
                'width="250" ' +
                'height="400"' +
                'on-click="onclick"' +
                'visu-data="visData"' +
                'key-field="data"' +
                'value-field="occurrences"' +
                '></horizontal-barchart>';

            element = angular.element(html);
            angular.element('body').append(element);
            $compile(element)(scope);
            scope.$digest();
        };

        createElementWithSecondaryValues = function () {
            var html = '<horizontal-barchart ' +
                    'id="barChart" ' +
                    'width="250" ' +
                    'height="400"' +
                    'on-click="onclick"' +
                    'visu-data="visData"' +
                    'key-field="data"' +
                    'value-field="occurrences"' +
                    'value-field-2="filteredOccurrences"' +
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

    it('should render all bars without secondary values after a 100ms delay', function () {
        //given
        createElement();

        //when
        scope.visData = statsData;
        scope.$digest();
        jasmine.clock().tick(100);

        //then
        expect(element.find('rect.bar').length).toBe(statsData.length);
        expect(element.find('rect.blueBar').length).toBe(0);
        expect(element.find('.label').length).toBe(statsData.length);
        expect(element.find('.bg-rect').length).toBe(statsData.length);
    });

    it('should render all bars with secondary values after a 100ms delay', function () {
        //given
        createElementWithSecondaryValues();

        //when
        scope.visData = statsData;
        scope.$digest();
        jasmine.clock().tick(100);

        //then
        expect(element.find('rect.bar').length).toBe(statsData.length);
        expect(element.find('rect.blueBar').length).toBe(statsData.length);
        expect(element.find('.label').length).toBe(statsData.length);
        expect(element.find('.bg-rect').length).toBe(statsData.length);
    });

    //waiting for a solution for this issue PhantomJs + svg :
    // https://github.com/ariya/phantomjs/issues/13293
    // it('should call addFilter function on click', inject(function () {
    //	//given
    //	createElement();
    //	scope.visData = statsData;
    //	scope.$digest();
    //	var event = new angular.element.Event('click');
    //
    //	//when
    //	 element.find('.bg-rect').eq(5).trigger(event);
    //
    //	console.log(element.find('.bg-rect').eq(5));
    //	//then
    //}));
});