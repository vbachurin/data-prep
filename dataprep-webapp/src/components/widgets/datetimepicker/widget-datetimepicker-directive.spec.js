'use strict';

describe('Datetimepicker directive', function () {
    var scope, element, html;

    beforeEach(module('talend.widget'));
    beforeEach(module('htmlTemplates'));

    var clickCalendarInput = function (elm) {
        elm = elm || element;
        elm.find('.datetimepicker').eq(0).click();
    };

    beforeEach(function () {
        jasmine.clock().install();
    });
    afterEach(function () {
        jasmine.clock().uninstall();

        scope.$destroy();
        element.remove();
    });

    describe('datetimepicker widget', function () {

        beforeEach(inject(function ($rootScope, $compile) {

            scope = $rootScope.$new();

            scope.param =
            {
                'name': 'param1',
                'label': 'Param 1',
                'type': 'date',
                'value': '02/01/2012 10:00:12'
            };


            html = '<talend-datetime-picker/>';//+
                //'format="DD/MM/YYYY hh:mm:ss" ' +
                //'formatTime="hh:mm:ss" ' +
                //'formatDate="DD/MM/YYY">' +
                //'</talend-datetime-picker>';

            element = $compile(html)(scope);
            scope.$digest();

        }));

        it('should show datetimepicker on click', function () {

            console.log('html:"'+element.html()+'---');
            var picker = element.find('.datetimepicker');
            expect(picker.length).toBe(1);

            //when
            clickCalendarInput();
            jasmine.clock().tick(250);

            //then
            expect(picker.hasClass('xdsoft_datetimepicker')).toBe(true);
        });

    });
});