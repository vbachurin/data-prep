describe('Dataset playground controller', function() {
    'use strict';

    var createController, scope, downloaded;
    var csv = {
        name: 'my dataset.csv',
        charset: 'utf-8',
        content: ''
    };

    beforeEach(module('data-prep.dataset-playground'));

    beforeEach(inject(function($rootScope, $controller, ExportService) {
        //mock
        downloaded = false;
        var initExportLink = function(computedCsv) {
            return {
                click : function() {
                    if(computedCsv === csv) {
                        downloaded = true;
                    }
                }
            };
        };

        scope = $rootScope.$new();

        createController = function() {
            var ctrl =  $controller('ExportCtrl', {
                $scope: scope
            });
            ctrl.initExportLink = initExportLink;
            return ctrl;
        };

        spyOn(ExportService, 'exportToCSV').and.returnValue(csv);
    }));

    it('should call ExportService, create the download link, and click it to download', inject(function(ExportService) {
        //given
        var ctrl = createController();
        ctrl.separator = '\t';

        //when
        ctrl.export();

        //then
        expect(ExportService.exportToCSV).toHaveBeenCalledWith('\t');
        expect(downloaded).toBe(true);
    }));
});
