describe('Export REST Service', function () {
    'use strict';

    var $httpBackend;
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

    beforeEach(module('data-prep.services.export'));

    beforeEach(inject(function ($injector) {
        $httpBackend = $injector.get('$httpBackend');
    }));

    it('should get all export types', inject(function($rootScope, RestURLs, ExportRestService) {
        //given
        var types = null;
        $httpBackend
            .expectGET(RestURLs.exportUrl+ '/formats')
            .respond(200, exportTypes);

        //when
        ExportRestService.exportTypes()
            .then(function(response) {
                types = response.data;
            });
        $httpBackend.flush();
        $rootScope.$digest();

        //then
        expect(types).toEqual(exportTypes);
    }));
});