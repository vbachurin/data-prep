/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

describe('Export service', () => {
    'use strict';

    let stateMock;

    const exportTypes = [
        {
            mimeType: 'text/csv',
            extension: '.csv',
            id: 'CSV',
            needParameters: 'true',
            defaultExport: 'false',
            enabled: true,
            disableReason: '',
            title: 'Export to CSV',
            parameters: [
                {
                    'name': 'csvSeparator',
                    'type': 'select',
                    'implicit': false,
                    'canBeBlank': false,
                    'placeHolder': '',
                    'configuration': {
                        'values': [{'value': ';', 'label': 'Semi colon'}, {
                            'value': '\t',
                            'label': 'Tabulation'
                        }, {'value': ' ', 'label': 'Space'}, {'value': ',', 'label': 'Comma'}], 'multiple': false
                    },
                    description: 'Select character to use as a delimiter',
                    label: 'Select character to use as a delimiter',
                    default: ';',
                },
                {
                    name: 'fileName',
                    type: 'string',
                    implicit: false,
                    canBeBlank: false,
                    placeHolder: '',
                    description: 'Name of the generated export file',
                    label: 'Name of the generated export file',
                    default: '',
                },
            ],
        },
        {
            mimeType: 'application/tde',
            extension: '.tde',
            id: 'TABLEAU',
            needParameters: 'false',
            defaultExport: 'false',
            parameters: [
                {
                    name: 'fileName',
                    type: 'string',
                    implicit: false,
                    canBeBlank: false,
                    placeHolder: '',
                    description: 'Name of the generated export file',
                    label: 'Name of the generated export file',
                    default: '',
                },
            ],
        },
        {
            mimeType: 'application/vnd.ms-excel',
            extension: '.xlsx',
            id: 'XLSX',
            needParameters: 'true',
            defaultExport: 'true',
            enabled: true,
            disableReason: '',
            title: 'Export to XLSX',
            parameters: [
                {
                    name: 'fileName',
                    type: 'string',
                    implicit: false,
                    canBeBlank: false,
                    placeHolder: '',
                    description: 'Name of the generated export file',
                    label: 'Name of the generated export file',
                    default: '',
                },
            ],
        },
    ];

    beforeEach(angular.mock.module('data-prep.services.export', ($provide) => {
        stateMock = {
            export: {
                exportTypes: exportTypes,
                defaultExportType: {
                    exportType: 'XLSX'
                }
            }
        };
        $provide.constant('state', stateMock);
    }));

    beforeEach(inject(($q, ExportRestService, ParametersService, StateService) => {
        spyOn(ExportRestService, 'exportTypes').and.returnValue($q.when(exportTypes));
        spyOn(ParametersService, 'resetParamValue').and.returnValue();
        spyOn(StateService, 'setDefaultExportType').and.returnValue();
        spyOn(StateService, 'setExportTypes').and.returnValue();
    }));

    it('should return type with provided id', inject((ExportService) => {
        // given
        const xlsType = exportTypes[2];

        // when
        const type = ExportService.getType('XLSX');

        // then
        expect(type).toBe(xlsType);
    }));

    it('should refresh export types list from REST call', inject(($rootScope, ExportService, StateService) => {
        // when
        ExportService.refreshTypes();
        $rootScope.$digest();

        // then
        expect(StateService.setExportTypes).toHaveBeenCalledWith(exportTypes);
        expect(StateService.setDefaultExportType).toHaveBeenCalledWith({ exportType: exportTypes[2].id });
    }));

    it('should save default type parameters the state when default type exist', inject((ExportService, StateService) => {
        // when
        ExportService._saveDefaultExport();

        // then
        expect(StateService.setDefaultExportType).toHaveBeenCalledWith({ exportType: exportTypes[2].id });
    }));

    it('should save default type parameters the state when default type DOES NOT exist', inject((ExportService, StateService) => {
        //given
        stateMock.export.exportTypes[2].defaultExport = false;


        // when
        ExportService._saveDefaultExport();

        // then
        expect(StateService.setDefaultExportType).toHaveBeenCalledWith({ exportType: exportTypes[0].id });
    }));

    it('should reset parameters', inject((ExportService, ParametersService) => {
        // given
        expect(ParametersService.resetParamValue).not.toHaveBeenCalled();

        // when
        ExportService.reset();

        // then
        expect(ParametersService.resetParamValue).toHaveBeenCalledWith(exportTypes[0].parameters);
        expect(ParametersService.resetParamValue).toHaveBeenCalledWith(exportTypes[1].parameters);
        expect(ParametersService.resetParamValue).toHaveBeenCalledWith(exportTypes[2].parameters);
    }));
});
