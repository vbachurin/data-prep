describe('Export Service', function () {
    'use strict';

    beforeEach(module('data-prep.services.export'));

    it('should create CSV string from simple data with provided separator', inject(function (DatagridService, ExportService) {
        //given
        var separator = ';';
        DatagridService.metadata = {name: 'my dataset'};
        DatagridService.data = {
            columns: [
                {id: 'col1', type: 'string'},
                {id: 'col2', type: 'string'},
                {id: 'col3', type: 'string'},
                {id: 'col4', type: 'string'},
                {id: 'col5', type: 'integer'}
            ],
            records: [
                {col1: 'Je',    col2: 'suis',   col3: 'la', col4: 'ligne', col5: '1'},
                {col1: 'Tu',    col2: 'es',     col3: 'la', col4: 'ligne', col5: '2'},
                {col1: 'Il',    col2: 'est',    col3: 'la', col4: 'ligne', col5: '3'},
                {col1: 'Nous',  col2: 'sommes', col3: 'la', col4: 'ligne', col5: '4'},
                {col1: 'Vous',  col2: 'etes',   col3: 'la', col4: 'ligne', col5: '5'}
            ]
        };

        //when
        var csv = ExportService.exportToCSV(separator);

        //then
        expect(csv.name).toBe('my dataset.csv');
        expect(csv.charset).toBe('utf-8');
        expect(csv.content).toBe(
            'col1;col2;col3;col4;col5\r\n' +
            'Je;suis;la;ligne;1\r\n' +
            'Tu;es;la;ligne;2\r\n' +
            'Il;est;la;ligne;3\r\n' +
            'Nous;sommes;la;ligne;4\r\n' +
            'Vous;etes;la;ligne;5'
        );
    }));

    it('should escape double-quotes in column name and surround it with double-quotes', inject(function (DatagridService, ExportService) {
        //given
        var separator = ';';
        DatagridService.metadata = {name: 'my dataset'};
        DatagridService.data = {
            columns: [
                {id: 'co"l1', type: 'string'},
                {id: 'col2', type: 'string'},
                {id: 'col3', type: 'string'},
                {id: 'col4', type: 'string'},
                {id: 'col5', type: 'integer'}
            ],
            records: [
                {'co"l1': 'Je',    col2: 'suis',   col3: 'la', col4: 'ligne', col5: '1'},
                {'co"l1': 'Tu',    col2: 'es',     col3: 'la', col4: 'ligne', col5: '2'},
                {'co"l1': 'Il',    col2: 'est',    col3: 'la', col4: 'ligne', col5: '3'},
                {'co"l1': 'Nous',  col2: 'sommes', col3: 'la', col4: 'ligne', col5: '4'},
                {'co"l1': 'Vous',  col2: 'etes',   col3: 'la', col4: 'ligne', col5: '5'}
            ]
        };

        //when
        var csv = ExportService.exportToCSV(separator);

        //then
        expect(csv.name).toBe('my dataset.csv');
        expect(csv.charset).toBe('utf-8');
        expect(csv.content).toBe(
            '"co""l1";col2;col3;col4;col5\r\n' +
            'Je;suis;la;ligne;1\r\n' +
            'Tu;es;la;ligne;2\r\n' +
            'Il;est;la;ligne;3\r\n' +
            'Nous;sommes;la;ligne;4\r\n' +
            'Vous;etes;la;ligne;5'
        );
    }));

    it('should escape double-quotes in row value and surround it with double-quotes', inject(function (DatagridService, ExportService) {
        //given
        var separator = ';';
        DatagridService.metadata = {name: 'my dataset'};
        DatagridService.data = {
            columns: [
                {id: 'col1', type: 'string'},
                {id: 'col2', type: 'string'},
                {id: 'col3', type: 'string'},
                {id: 'col4', type: 'string'},
                {id: 'col5', type: 'integer'}
            ],
            records: [
                {col1: 'J"e',    col2: 'suis',   col3: 'la', col4: 'ligne', col5: '1'},
                {col1: 'Tu',    col2: 'es',     col3: 'la', col4: 'ligne', col5: '2'},
                {col1: 'Il',    col2: 'est',    col3: 'la', col4: 'ligne', col5: '3'},
                {col1: 'Nous',  col2: 'sommes', col3: 'la', col4: 'ligne', col5: '4'},
                {col1: 'Vous',  col2: 'etes',   col3: 'la', col4: 'ligne', col5: '5'}
            ]
        };

        //when
        var csv = ExportService.exportToCSV(separator);

        //then
        expect(csv.name).toBe('my dataset.csv');
        expect(csv.charset).toBe('utf-8');
        expect(csv.content).toBe(
            'col1;col2;col3;col4;col5\r\n' +
            '"J""e"' +
            ';suis;la;ligne;1\r\n' +
            'Tu;es;la;ligne;2\r\n' +
            'Il;est;la;ligne;3\r\n' +
            'Nous;sommes;la;ligne;4\r\n' +
            'Vous;etes;la;ligne;5'
        );
    }));

    it('should surround column name with double-quotes if it contains a comma', inject(function (DatagridService, ExportService) {
        //given
        var separator = ';';
        DatagridService.metadata = {name: 'my dataset'};
        DatagridService.data = {
            columns: [
                {id: 'co,l1', type: 'string'},
                {id: 'col2', type: 'string'},
                {id: 'col3', type: 'string'},
                {id: 'col4', type: 'string'},
                {id: 'col5', type: 'integer'}
            ],
            records: [
                {'co,l1': 'Je',    col2: 'suis',   col3: 'la', col4: 'ligne', col5: '1'},
                {'co,l1': 'Tu',    col2: 'es',     col3: 'la', col4: 'ligne', col5: '2'},
                {'co,l1': 'Il',    col2: 'est',    col3: 'la', col4: 'ligne', col5: '3'},
                {'co,l1': 'Nous',  col2: 'sommes', col3: 'la', col4: 'ligne', col5: '4'},
                {'co,l1': 'Vous',  col2: 'etes',   col3: 'la', col4: 'ligne', col5: '5'}
            ]
        };

        //when
        var csv = ExportService.exportToCSV(separator);

        //then
        expect(csv.name).toBe('my dataset.csv');
        expect(csv.charset).toBe('utf-8');
        expect(csv.content).toBe(
            '"co,l1";col2;col3;col4;col5\r\n' +
            'Je;suis;la;ligne;1\r\n' +
            'Tu;es;la;ligne;2\r\n' +
            'Il;est;la;ligne;3\r\n' +
            'Nous;sommes;la;ligne;4\r\n' +
            'Vous;etes;la;ligne;5'
        );
    }));

    it('should surround value with double-quotes if it contains a comma', inject(function (DatagridService, ExportService) {
        //given
        var separator = ';';
        DatagridService.metadata = {name: 'my dataset'};
        DatagridService.data = {
            columns: [
                {id: 'col1', type: 'string'},
                {id: 'col2', type: 'string'},
                {id: 'col3', type: 'string'},
                {id: 'col4', type: 'string'},
                {id: 'col5', type: 'integer'}
            ],
            records: [
                {col1: 'Je',    col2: 'suis',   col3: 'la', col4: 'ligne', col5: '1'},
                {col1: 'Tu',    col2: 'es',     col3: 'la', col4: 'ligne', col5: '2'},
                {col1: 'I,l',    col2: 'est',    col3: 'la', col4: 'ligne', col5: '3'},
                {col1: 'Nous',  col2: 'sommes', col3: 'la', col4: 'ligne', col5: '4'},
                {col1: 'Vous',  col2: 'etes',   col3: 'la', col4: 'ligne', col5: '5'}
            ]
        };

        //when
        var csv = ExportService.exportToCSV(separator);

        //then
        expect(csv.name).toBe('my dataset.csv');
        expect(csv.charset).toBe('utf-8');
        expect(csv.content).toBe(
            'col1;col2;col3;col4;col5\r\n' +
            'Je;suis;la;ligne;1\r\n' +
            'Tu;es;la;ligne;2\r\n' +
            '"I,l";est;la;ligne;3\r\n' +
            'Nous;sommes;la;ligne;4\r\n' +
            'Vous;etes;la;ligne;5'
        );
    }));

    it('should surround column name with double-quotes if it contains a tab', inject(function (DatagridService, ExportService) {
        //given
        var separator = ';';
        DatagridService.metadata = {name: 'my dataset'};
        DatagridService.data = {
            columns: [
                {id: 'co\tl1', type: 'string'},
                {id: 'col2', type: 'string'},
                {id: 'col3', type: 'string'},
                {id: 'col4', type: 'string'},
                {id: 'col5', type: 'integer'}
            ],
            records: [
                {'co\tl1': 'Je',    col2: 'suis',   col3: 'la', col4: 'ligne', col5: '1'},
                {'co\tl1': 'Tu',    col2: 'es',     col3: 'la', col4: 'ligne', col5: '2'},
                {'co\tl1': 'Il',    col2: 'est',    col3: 'la', col4: 'ligne', col5: '3'},
                {'co\tl1': 'Nous',  col2: 'sommes', col3: 'la', col4: 'ligne', col5: '4'},
                {'co\tl1': 'Vous',  col2: 'etes',   col3: 'la', col4: 'ligne', col5: '5'}
            ]
        };

        //when
        var csv = ExportService.exportToCSV(separator);

        //then
        expect(csv.name).toBe('my dataset.csv');
        expect(csv.charset).toBe('utf-8');
        expect(csv.content).toBe(
            '"co\tl1";col2;col3;col4;col5\r\n' +
            'Je;suis;la;ligne;1\r\n' +
            'Tu;es;la;ligne;2\r\n' +
            'Il;est;la;ligne;3\r\n' +
            'Nous;sommes;la;ligne;4\r\n' +
            'Vous;etes;la;ligne;5'
        );
    }));

    it('should surround value with double-quotes if it contains a tab', inject(function (DatagridService, ExportService) {
        //given
        var separator = ';';
        DatagridService.metadata = {name: 'my dataset'};
        DatagridService.data = {
            columns: [
                {id: 'col1', type: 'string'},
                {id: 'col2', type: 'string'},
                {id: 'col3', type: 'string'},
                {id: 'col4', type: 'string'},
                {id: 'col5', type: 'integer'}
            ],
            records: [
                {col1: 'Je',    col2: 'suis',   col3: 'la', col4: 'ligne', col5: '1'},
                {col1: 'Tu',    col2: 'es',     col3: 'la', col4: 'lig\tne', col5: '2'},
                {col1: 'Il',    col2: 'est',    col3: 'la', col4: 'ligne', col5: '3'},
                {col1: 'Nous',  col2: 'sommes', col3: 'la', col4: 'ligne', col5: '4'},
                {col1: 'Vous',  col2: 'etes',   col3: 'la', col4: 'ligne', col5: '5'}
            ]
        };

        //when
        var csv = ExportService.exportToCSV(separator);

        //then
        expect(csv.name).toBe('my dataset.csv');
        expect(csv.charset).toBe('utf-8');
        expect(csv.content).toBe(
            'col1;col2;col3;col4;col5\r\n' +
            'Je;suis;la;ligne;1\r\n' +
            'Tu;es;la;"lig\tne";2\r\n' +
            'Il;est;la;ligne;3\r\n' +
            'Nous;sommes;la;ligne;4\r\n' +
            'Vous;etes;la;ligne;5'
        );
    }));

    it('should surround column name with double-quotes if it contains a semi-colon', inject(function (DatagridService, ExportService) {
        //given
        var separator = ';';
        DatagridService.metadata = {name: 'my dataset'};
        DatagridService.data = {
            columns: [
                {id: 'co;l1', type: 'string'},
                {id: 'col2', type: 'string'},
                {id: 'col3', type: 'string'},
                {id: 'col4', type: 'string'},
                {id: 'col5', type: 'integer'}
            ],
            records: [
                {'co;l1': 'Je',    col2: 'suis',   col3: 'la', col4: 'ligne', col5: '1'},
                {'co;l1': 'Tu',    col2: 'es',     col3: 'la', col4: 'ligne', col5: '2'},
                {'co;l1': 'Il',    col2: 'est',    col3: 'la', col4: 'ligne', col5: '3'},
                {'co;l1': 'Nous',  col2: 'sommes', col3: 'la', col4: 'ligne', col5: '4'},
                {'co;l1': 'Vous',  col2: 'etes',   col3: 'la', col4: 'ligne', col5: '5'}
            ]
        };

        //when
        var csv = ExportService.exportToCSV(separator);

        //then
        expect(csv.name).toBe('my dataset.csv');
        expect(csv.charset).toBe('utf-8');
        expect(csv.content).toBe(
            '"co;l1";col2;col3;col4;col5\r\n' +
            'Je;suis;la;ligne;1\r\n' +
            'Tu;es;la;ligne;2\r\n' +
            'Il;est;la;ligne;3\r\n' +
            'Nous;sommes;la;ligne;4\r\n' +
            'Vous;etes;la;ligne;5'
        );
    }));

    it('should surround value with double-quotes if it contains a semi-colon', inject(function (DatagridService, ExportService) {
        //given
        var separator = ';';
        DatagridService.metadata = {name: 'my dataset'};
        DatagridService.data = {
            columns: [
                {id: 'col1', type: 'string'},
                {id: 'col2', type: 'string'},
                {id: 'col3', type: 'string'},
                {id: 'col4', type: 'string'},
                {id: 'col5', type: 'integer'}
            ],
            records: [
                {col1: 'Je',    col2: 'suis',   col3: 'la', col4: 'ligne', col5: '1'},
                {col1: 'Tu',    col2: 'es',     col3: 'la', col4: 'ligne', col5: '2'},
                {col1: 'Il',    col2: 'est',    col3: 'la', col4: 'ligne', col5: '3'},
                {col1: 'Nous',  col2: 'sommes', col3: 'la', col4: 'ligne', col5: '4'},
                {col1: 'Vous',  col2: 'et;es',   col3: 'la', col4: 'ligne', col5: '5'}
            ]
        };

        //when
        var csv = ExportService.exportToCSV(separator);

        //then
        expect(csv.name).toBe('my dataset.csv');
        expect(csv.charset).toBe('utf-8');
        expect(csv.content).toBe(
            'col1;col2;col3;col4;col5\r\n' +
            'Je;suis;la;ligne;1\r\n' +
            'Tu;es;la;ligne;2\r\n' +
            'Il;est;la;ligne;3\r\n' +
            'Nous;sommes;la;ligne;4\r\n' +
            'Vous;"et;es";la;ligne;5'
        );
    }));

    it('should surround column name with double-quotes if it contains a line break', inject(function (DatagridService, ExportService) {
        //given
        var separator = ';';
        DatagridService.metadata = {name: 'my dataset'};
        DatagridService.data = {
            columns: [
                {id: 'co\nl1', type: 'string'},
                {id: 'col2', type: 'string'},
                {id: 'col3', type: 'string'},
                {id: 'col4', type: 'string'},
                {id: 'col5', type: 'integer'}
            ],
            records: [
                {'co\nl1': 'Je',    col2: 'suis',   col3: 'la', col4: 'ligne', col5: '1'},
                {'co\nl1': 'Tu',    col2: 'es',     col3: 'la', col4: 'ligne', col5: '2'},
                {'co\nl1': 'Il',    col2: 'est',    col3: 'la', col4: 'ligne', col5: '3'},
                {'co\nl1': 'Nous',  col2: 'sommes', col3: 'la', col4: 'ligne', col5: '4'},
                {'co\nl1': 'Vous',  col2: 'etes',   col3: 'la', col4: 'ligne', col5: '5'}
            ]
        };

        //when
        var csv = ExportService.exportToCSV(separator);

        //then
        expect(csv.name).toBe('my dataset.csv');
        expect(csv.charset).toBe('utf-8');
        expect(csv.content).toBe(
            '"co\nl1";col2;col3;col4;col5\r\n' +
            'Je;suis;la;ligne;1\r\n' +
            'Tu;es;la;ligne;2\r\n' +
            'Il;est;la;ligne;3\r\n' +
            'Nous;sommes;la;ligne;4\r\n' +
            'Vous;etes;la;ligne;5'
        );
    }));

    it('should surround value with double-quotes if it contains a line break', inject(function (DatagridService, ExportService) {
        //given
        var separator = ';';
        DatagridService.metadata = {name: 'my dataset'};
        DatagridService.data = {
            columns: [
                {id: 'col1', type: 'string'},
                {id: 'col2', type: 'string'},
                {id: 'col3', type: 'string'},
                {id: 'col4', type: 'string'},
                {id: 'col5', type: 'integer'}
            ],
            records: [
                {col1: 'Je',    col2: 'suis',   col3: 'la', col4: 'ligne', col5: '1'},
                {col1: 'Tu',    col2: 'es',     col3: 'la', col4: 'ligne', col5: '2'},
                {col1: 'Il',    col2: 'est',    col3: 'la', col4: 'ligne', col5: '3'},
                {col1: 'Nous',  col2: 'sommes', col3: 'l\na', col4: 'ligne', col5: '4'},
                {col1: 'Vous',  col2: 'etes',   col3: 'la', col4: 'ligne', col5: '5'}
            ]
        };

        //when
        var csv = ExportService.exportToCSV(separator);

        //then
        expect(csv.name).toBe('my dataset.csv');
        expect(csv.charset).toBe('utf-8');
        expect(csv.content).toBe(
            'col1;col2;col3;col4;col5\r\n' +
            'Je;suis;la;ligne;1\r\n' +
            'Tu;es;la;ligne;2\r\n' +
            'Il;est;la;ligne;3\r\n' +
            'Nous;sommes;"l\na";ligne;4\r\n' +
            'Vous;etes;la;ligne;5'
        );
    }));
});