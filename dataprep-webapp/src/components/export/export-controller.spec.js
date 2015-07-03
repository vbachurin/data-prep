describe('Export controller', function() {
    'use strict';

    var scope, createController, form;

    var exportTypes = [
        {
            'mimeType': 'text/csv',
            'extension': '.csv',
            'id': 'CSV',
            'needParameters': 'true',
            'defaultExport': 'false',
            'parameters': [{
                'name': 'csvSeparator',
                'labelKey': 'CHOOSE_SEPARATOR',
                'type': 'radio',
                'defaultValue': {'value': ';', 'labelKey': 'SEPARATOR_SEMI_COLON'},
                'values': [
                    {'value': '&#09;', 'labelKey': 'SEPARATOR_TAB'},
                    {'value': ' ', 'labelKey': 'SEPARATOR_SPACE'},
                    {'value': ',', 'labelKey': 'SEPARATOR_COMMA'}
                ]
            }]
        },
        {
            'mimeType': 'application/tde',
            'extension': '.tde',
            'id': 'TABLEAU',
            'needParameters': 'false',
            'defaultExport': 'false'
        },
        {
            'mimeType': 'application/vnd.ms-excel',
            'extension': '.xls',
            'id': 'XLS',
            'needParameters': 'false',
            'defaultExport': 'true'
        }
    ];
    var currentParameters = {exportType: 'XLS'};
    var currentType = exportTypes[2];

    beforeEach(module('data-prep.export'));

    beforeEach(inject(function ($rootScope, $controller, $q, ExportService) {
        form = {submit : function() {}};
        scope = $rootScope.$new();

        createController = function () {
            var ctrl = $controller('ExportCtrl', {
                $scope: scope
            });
            ctrl.form = form; //simulate init by directive
            return ctrl;
        };

        spyOn(ExportService, 'refreshTypes').and.callFake(function() {
            ExportService.exportTypes = exportTypes;
            return $q.when(exportTypes);
        });
        spyOn(ExportService, 'getParameters').and.returnValue(currentParameters);
        spyOn(ExportService, 'setParameters').and.returnValue();
        spyOn(form, 'submit').and.returnValue();
    }));

    describe('property binding', function() {
        it('should bind preparationId getter to PreparationService', inject(function(PreparationService) {
            //given
            var ctrl = createController();
            expect(ctrl.preparationId).toBeFalsy();

            //when
            PreparationService.currentPreparationId = '48da64513c43a548e678bc99';

            //then
            expect(ctrl.preparationId).toBe('48da64513c43a548e678bc99');
        }));

        it('should bind stepId getter to RecipeService', inject(function(RecipeService) {
            //given
            var ctrl = createController();
            expect(ctrl.stepId).toBeFalsy();

            //when
            RecipeService.getRecipe().push({
                transformation : {
                    stepId: '48da64513c43a548e678bc99'
                }
            });

            //then
            expect(ctrl.stepId).toBe('48da64513c43a548e678bc99');
        }));

        it('should bind datasetId getter to PlaygroundService', inject(function(PlaygroundService) {
            //given
            var ctrl = createController();
            expect(ctrl.datasetId).toBeFalsy();

            //when
            PlaygroundService.currentMetadata = {
                id: '48da64513c43a548e678bc99'
            };

            //then
            expect(ctrl.datasetId).toBe('48da64513c43a548e678bc99');
        }));

        it('should bind exportTypes getter to ExportService', inject(function(ExportService) {
            //given
            var ctrl = createController();
            expect(ctrl.exportTypes).toBe(exportTypes);

            var newTypes = [];

            //when
            ExportService.exportTypes = newTypes;

            //then
            expect(ctrl.exportTypes).toBe(newTypes);
        }));
    });

    describe('on creation', function() {
        it('should init export types', inject(function(ExportService) {
            //when
            createController();

            //then
            expect(ExportService.refreshTypes).toHaveBeenCalled();
        }));

        it('should init current export type and parameter', function() {
            //when
            var ctrl = createController();
            scope.$digest();

            //then
            expect(ctrl.currentExportParameters).toBe(currentParameters);
            expect(ctrl.currentExportType).toBe(currentType);
        });
    });

    it('should reset params with saved one and reset current export type accordingly', function() {
        //given
        var ctrl = createController();
        expect(ctrl.currentExportType).toBeFalsy();
        expect(ctrl.currentExportParameters).toBeFalsy();

        //when
        ctrl.resetCurrentParameters();

        //then
        expect(ctrl.currentExportParameters).toBe(currentParameters);
        expect(ctrl.currentExportType).toBe(currentType);
    });

    describe('on new type selection', function() {
        it('should init type with params', function() {
            //given
            var ctrl = createController();
            scope.$apply();

            var csvType = exportTypes[0];
            var csvParams = {exportType: 'CSV', 'exportParameters.csvSeparator': ';'}; //with separator default value

            expect(ctrl.currentExportType).not.toBe(csvType);
            expect(ctrl.currentExportParameters).not.toEqual(csvParams);

            //when
            ctrl.changeTypeAndExport(csvType);

            //then
            expect(ctrl.currentExportType).toBe(csvType);
            expect(ctrl.currentExportParameters).toEqual(csvParams);
        });

        it('should show modal on type with parameters', function() {
            //given
            var ctrl = createController();
            scope.$apply();
            expect(ctrl.showModal).toBeFalsy();

            var csvType = exportTypes[0];

            //when
            ctrl.changeTypeAndExport(csvType);

            //then
            expect(ctrl.showModal).toBe(true);
            expect(form.submit).not.toHaveBeenCalled();
        });

        it('should save new export type', function() {
            //given
            var ctrl = createController();
            scope.$apply();

            var tableauType = exportTypes[1];
            var tableauParams = {exportType: 'TABLEAU'};

            expect(ctrl.currentExportType).not.toBe(tableauType);
            expect(ctrl.currentExportParameters).not.toEqual(tableauParams);

            //when
            ctrl.changeTypeAndExport(tableauType);

            //then
            expect(ctrl.currentExportType).toBe(tableauType);
            expect(ctrl.currentExportParameters).toEqual(tableauParams);
        });

        it('should launch export on type with no parameters, after a $timeout', inject(function($timeout) {
            //given
            var ctrl = createController();
            scope.$apply();

            var tableauType = exportTypes[1];

            //when
            ctrl.changeTypeAndExport(tableauType);
            $timeout.flush(); //The $timeout is necessary to let angular init the form inputs in the next digest

            //then
            expect(form.submit).toHaveBeenCalled();
        }));
    });

    describe('on parameters validation', function() {
        it('should save current export parameters', inject(function(ExportService) {
            //given
            var ctrl = createController();
            var parameters = {exportType: 'CSV', 'exportParameters.csvSeparator': ','};
            ctrl.currentExportParameters = parameters;

            //when
            ctrl.saveEditionAndExport();

            //then
            expect(ExportService.setParameters).toHaveBeenCalledWith(parameters);
        }));

        it('should launch export after a $timeout', inject(function($timeout) {
            //given
            var ctrl = createController();
            var parameters = {exportType: 'CSV', 'exportParameters.csvSeparator': ','};
            ctrl.currentExportParameters = parameters;

            //when
            ctrl.saveEditionAndExport();
            $timeout.flush();

            //then
            expect(form.submit).toHaveBeenCalled();
        }));
    });

    describe('on export', function() {
        it('should set action in form', inject(function(RestURLs) {
            //given
            var ctrl = createController();

            //when
            ctrl.export();

            //then
            expect(form.action).toBe(RestURLs.exportUrl);
        }));

        it('should submit form', function() {
            //given
            var ctrl = createController();

            //when
            ctrl.export();

            //then
            expect(form.submit).toHaveBeenCalled();
        });
    });
});