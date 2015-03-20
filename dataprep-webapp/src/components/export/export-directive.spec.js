describe('Dataset playground directive', function() {
    'use strict';

    var scope, createElement, element;
    var csv = {
        name: 'my dataset.csv',
        charset: 'utf-8',
        content: 'content'
    };

    beforeEach(module('data-prep.export'));
    beforeEach(module('htmlTemplates'));

    beforeEach(inject(function($rootScope, $compile, ExportService) {
        scope = $rootScope.$new();
        createElement = function() {
            element = angular.element('<export></export>');
            angular.element('body').append(element);

            $compile(element)(scope);
            scope.$digest();
        };

        spyOn(ExportService, 'exportToCSV').and.returnValue(csv);
    }));

    afterEach(function() {
        scope.$destroy();
        element.remove();
    });

    it('should show modal on export button click', function() {
        //given
        createElement();
        expect(element.controller('export').showExport).toBeFalsy();

        //when
        element.find('.t-btn-primary').eq(0).click();

        //then
        expect(element.controller('export').showExport).toBeTruthy();
    });

    it('should init download link', inject(function() {
        //given
        createElement();
        var exportModals = angular.element('body').find('talend-modal');
        expect(exportModals.length).toBe(1);

        //when
        var link = element.controller('export').initExportLink(csv);

        //then
        expect(link.getAttribute('download')).toBe('my dataset.csv');
        expect(link.href).toBeDefined();
    }));
});