/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

describe('Export directive', () => {
    'use strict';

    let scope;
    let element;
    let ctrl;
    let stateMock;

    const exportTypes = [
        {
            mimeType: 'text/csv',
            extension: '.csv',
            id: 'CSV',
            needParameters: 'true',
            defaultExport: 'false',
            parameters: [{
                name: 'csvSeparator',
                labelKey: 'CHOOSE_SEPARATOR',
                type: 'radio',
                defaultValue: { value: ';', labelKey: 'SEPARATOR_SEMI_COLON' },
                values: [
                    { value: '&#09;', labelKey: 'SEPARATOR_TAB' },
                    { value: ' ', labelKey: 'SEPARATOR_SPACE' },
                    { value: ',', labelKey: 'SEPARATOR_COMMA' },
                ],
            },],
        },
        {
            mimeType: 'application/tde',
            extension: '.tde',
            id: 'TABLEAU',
            needParameters: 'false',
            defaultExport: 'false',
            enabled: false,
            disableReason: 'Reason only valid in unit test',
        },
        {
            mimeType: 'application/vnd.ms-excel',
            extension: '.xls',
            id: 'XLS',
            needParameters: 'false',
            defaultExport: 'true',
        },
    ];
    const csvParameters = { exportType: 'CSV', 'exportParameters.csvSeparator': ';' };
    const csvType = exportTypes[0];

    beforeEach(angular.mock.module('data-prep.export', ($provide) => {
        stateMock = {
            playground: {
                exportParameters: {
                    exportType: 'CSV',
                    'exportParameters.csvSeparator': ';',
                    'exportParameters.fileName': 'prepname',
                },
                recipe: {
                    current: {
                        steps: [],
                    },
                },
            },
        };
        $provide.constant('state', stateMock);
    }));

    beforeEach(inject(($rootScope, $compile, $q, ExportService, StorageService) => {
        spyOn(ExportService, 'refreshTypes').and.returnValue($q.when(exportTypes));
        spyOn(ExportService, 'getType').and.returnValue(csvType);
        spyOn(StorageService, 'getExportParams').and.returnValue(csvParameters);

        scope = $rootScope.$new();
        element = angular.element('<export></export>');
        $compile(element)(scope);
        scope.$digest();

        ctrl = element.controller('export');
    }));

    afterEach(() => {
        scope.$destroy();
        element.remove();
    });

    describe('form', () => {
        it('should inject preparation id input', () => {
            //given
            const input = element.find('#exportForm').eq(0)[0].preparationId;
            expect(input.value).toBeFalsy();

            //when
            stateMock.playground.preparation = { id: '48da64513c43a548e678bc99' };
            scope.$digest();

            //then
            expect(input.value).toBe('48da64513c43a548e678bc99');
        });

        it('should inject step id input', () => {
            //given
            const input = element.find('#exportForm').eq(0)[0].stepId;
            expect(input.value).toBeFalsy();

            //when
            stateMock.playground.recipe.current.steps.push({
                transformation: {
                    stepId: '48da64513c43a548e678bc99',
                },
            });
            scope.$digest();

            //then
            expect(input.value).toBe('48da64513c43a548e678bc99');
        });

        it('should inject dataset id input', inject((state) => {
            //given
            const input = element.find('#exportForm').eq(0)[0].datasetId;
            expect(input.value).toBeFalsy();

            //when
            state.playground.dataset = { id: '48da64513c43a548e678bc99' };
            scope.$digest();

            //then
            expect(input.value).toBe('48da64513c43a548e678bc99');
        }));

        it('should inject export type input', () => {
            //given
            const input = element.find('#exportForm').eq(0)[0].exportType;

            //then
            expect(input.value).toBe('CSV');
        });

        it('should inject export type parameters in form', () => {
            //given
            const input = element.find('#exportForm').eq(0)[0]['exportParameters.csvSeparator'];

            //then
            expect(input.value).toBe(';');
        });

        it('should set form in controller', inject(() => {
            //then
            expect(ctrl.form).toBeDefined();
        }));
    });

    describe('disabled export', () => {
        it('should have disabled style', inject((ExportService) => {
            //given
            ExportService.exportTypes = exportTypes;

            //when
            scope.$digest();

            //then
            expect(element.find('.dropdown-menu').find('li').eq(1).hasClass('disabled')).toBe(true);
        }));

        it('should have disabled message', inject((ExportService) => {
            //given
            ExportService.exportTypes = exportTypes;

            //when
            scope.$digest();

            //then
            expect(element.find('.dropdown-menu').find('li').eq(1).text().trim()).toBe('TABLEAU - Reason only valid in unit test');
        }));
    });
});
