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

    const exportTypes = [
        {
            'mimeType': 'text/csv',
            'extension': '.csv',
            'id': 'CSV',
            'needParameters': 'true',
            'defaultExport': 'false',
            'enabled': true,
            'disableReason': '',
            'title': 'Export to CSV',
            'parameters': [
                {
                    'name': 'csvSeparator',
                    'type': 'select',
                    'implicit': false,
                    'canBeBlank': false,
                    'placeHolder': '',
                    'configuration': {
                        'values': [{ 'value': ';', 'label': 'Semi colon' }, {
                            'value': '\t',
                            'label': 'Tabulation'
                        }, { 'value': ' ', 'label': 'Space' }, { 'value': ',', 'label': 'Comma' }], 'multiple': false
                    },
                    'description': 'Select character to use as a delimiter',
                    'label': 'Select character to use as a delimiter',
                    'default': ';',
                },
                {
                    'name': 'fileName',
                    'type': 'string',
                    'implicit': false,
                    'canBeBlank': false,
                    'placeHolder': '',
                    'description': 'Name of the generated export file',
                    'label': 'Name of the generated export file',
                    'default': '',
                },
            ],
        },
        {
            'mimeType': 'application/tde',
            'extension': '.tde',
            'id': 'TABLEAU',
            'needParameters': 'false',
            'defaultExport': 'false',
            'parameters': [
                {
                    'name': 'fileName',
                    'type': 'string',
                    'implicit': false,
                    'canBeBlank': false,
                    'placeHolder': '',
                    'description': 'Name of the generated export file',
                    'label': 'Name of the generated export file',
                    'default': '',
                }
            ],
        },
        {
            'mimeType': 'application/vnd.ms-excel',
            'extension': '.xlsx',
            'id': 'XLSX',
            'needParameters': 'true',
            'defaultExport': 'true',
            'enabled': true,
            'disableReason': '',
            'title': 'Export to XLSX',
            'parameters': [
                {
                    'name': 'fileName',
                    'type': 'string',
                    'implicit': false,
                    'canBeBlank': false,
                    'placeHolder': '',
                    'description': 'Name of the generated export file',
                    'label': 'Name of the generated export file',
                    'default': '',
                }
            ],
        },
    ];

    beforeEach(angular.mock.module('data-prep.services.export'));

    beforeEach(inject(($q, ExportRestService, TransformationService, StorageService) => {
        spyOn(ExportRestService, 'exportTypes').and.returnValue($q.when(exportTypes));
        spyOn(TransformationService, 'resetParamValue').and.returnValue();
        spyOn(StorageService, 'saveExportParams').and.returnValue();
    }));

    it('should return type with provided id', inject((ExportService) => {
        // given
        ExportService.exportTypes = exportTypes;
        const xlsType = exportTypes[2];

        // when
        const type = ExportService.getType('XLSX');

        // then
        expect(type).toBe(xlsType);
    }));

    it('should refresh export types list from REST call', inject(($rootScope, ExportService) => {
        // given
        ExportService.exportTypes = [];

        // when
        ExportService.refreshTypes();
        $rootScope.$digest();

        // then
        expect(ExportService.exportTypes).toBe(exportTypes);
    }));

    it('should save default type parameters in localStorage when there are no saved params yet', inject(($rootScope, StorageService, ExportService) => {
        // given
        spyOn(StorageService, 'getExportParams').and.returnValue();
        expect(StorageService.saveExportParams).not.toHaveBeenCalled();

        // when
        ExportService.refreshTypes();
        $rootScope.$digest();

        // then
        expect(StorageService.saveExportParams).toHaveBeenCalledWith({ exportType: 'XLSX' });
    }));

    it('should NOT save default type parameters in localStorage when there are already saved params', inject(($rootScope, StorageService, ExportService) => {
        // given
        spyOn(StorageService, 'getExportParams').and.returnValue({});
        expect(StorageService.saveExportParams).not.toHaveBeenCalled();

        // when
        ExportService.refreshTypes();
        $rootScope.$digest();

        // then
        expect(StorageService.saveExportParams).not.toHaveBeenCalled();
    }));

    it('should reset parameters', inject((ExportService, TransformationService) => {
        // given
        ExportService.exportTypes = exportTypes;
        expect(TransformationService.resetParamValue).not.toHaveBeenCalled();

        // when
        ExportService.reset();

        // then
        expect(TransformationService.resetParamValue).toHaveBeenCalledWith(exportTypes[0].parameters);
        expect(TransformationService.resetParamValue).toHaveBeenCalledWith(exportTypes[1].parameters);
        expect(TransformationService.resetParamValue).toHaveBeenCalledWith(exportTypes[2].parameters);
    }));
});