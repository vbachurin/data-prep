describe('Datagrid directive', function () {
    'use strict';
    var scope, element;
    var metadata = {
        'id': '12ce6c32-bf80-41c8-92e5-66d70f22ec1f',
        'name': 'US States',
        'author': 'anonymousUser',
        'created': '02-03-2015 14:52'
    };
    var data = {
        'columns': [
            {
                'id': '0000',
                'name': 'id',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'number',
                'domain': 'STATE_CODE_'
            },
            {
                'id': '0001',
                'name': 'Postal',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string',
                'domain': 'STATE_CODE_'
            },
            {
                'id': '0002',
		        'name': 'State',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string',
                'domain': 'STATE_CODE_'
            },
            {
                'id': '0003', 
				'name': 'Capital',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string',
                'domain': 'STATE_CODE_'
            },
            {
                'id': '0004', 
				'name': 'MostPopulousCity',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string',
                'domain': 'STATE_CODE_'
            }
        ],
        'records': [
            {
                '0000': '1',
                '0001': 'AL',
                '0002': 'My Alabama',
                '0003': 'Montgomery',
                '0004': 'Birmingham city'
            },
            {
                '0000': '2',
                '0001': 'AK',
                '0002': 'Alaska',
                '0003': 'Juneau',
                '0004': 'Anchorage'
            },
            {
                '0000': '3',
                '0001': 'AL',
                '0002': 'My Alabama 2',
                '0003': 'Montgomery',
                '0004': 'Birmingham city'
            }
        ]
    };
    var dataWithEmptyCell = {
        'columns': [
            {
                'id': '0000',
                'name': 'id',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'number'
            },
            {
                'id': '0001',
                'name': 'Postal',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string'
            },
            {
                'id': '0002',
				'name': 'State',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string'
            },
            {
                'id': '0003', 
				'name': 'Capital',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string'
            },
            {
                'id': '0004', 
				'name': 'MostPopulousCity',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string'
            }
        ],
        'records': [
            {
                '0000': '5',
                '0001': 'AK',
                '0002': 'Alaska',
                '0003': 'Juneau',
                '0004': 'Anchorage'
            },
            {
                '0000': '6',
                '0001': '--',
                '0002': '',
                '0003': '--',
                '0004': '--'
            },
            {
                '0000': '7',
                '0001': '--',
                '0002': '',
                '0003': '--',
                '0004': '--'
            }
        ]
    };

    var newData = {
        'columns': [
            {
                'id': '0000',
                'name': 'id',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'number'
            },
            {
                'id': '0001',
                'name': 'Postal',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string'
            },
            {
                'id': '0002', 
				'name': 'State',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string'
            }
        ],
        'records': [
            {
                '__tdpRowDiff': 'new',
                '0000': '1',
                '0001': '',
                '0002': 'My Alabama'
            }
        ]
    };

    var deletedRowData = {
        'columns': [
            {
                'id': '0000',
                'name': 'id',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'number'
            },
            {
                'id': '0001',
                'name': 'Postal',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string'
            },
            {
                'id': '0002', 
				'name': 'State',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string'
            }
        ],
        'records': [
            {
                '__tdpRowDiff': 'delete',
                '0000': '1',
                '0001': '',
                '0002': 'My Alabama'
            }
        ]
    };

    var deletedColumnData = {
        'preview' : true,
        'columns': [
            {
                'id': '0000',
                'name': 'id',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'number'
            },
            {
                'id': '0001',
                'name': 'Postal',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string',
                '__tdpColumnDiff': 'delete'
            },
            {
                'id': '0002', 
				'name': 'State',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string'
            }
        ],
        'records': [
            {
                '0000': '1',
                '0001': '',
                '0002': 'My Alabama',
                '__tdpDiff': {'0001': 'delete'}
            }
        ]
    };

    var newColumnData = {
        'preview' : true,
        'columns': [
            {
                'id': '0000',
                'name': 'id',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'number'
            },
            {
                'id': '0001',
                'name': 'Postal',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string',
                '__tdpColumnDiff': 'new'
            },
            {
                'id': '0002', 
				'name': 'State',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string'
            }
        ],
        'records': [
            {
                '0000': '1',
                '0001': '90210',
                '0002': 'My Alabama',
                '__tdpDiff': {'0001': 'new'}
            }
        ]
    };

    var updateData = {
        'columns': [
            {
                'id': '0000',
                'name': 'id',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'number'
            },
            {
                'id': '0001',
                'name': 'Postal',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string'
            },
            {
                'id': '0002', 
				'name': 'State',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string'
            }
        ],
        'records': [
            {
                '__tdpDiff': {'0002': 'update'},
                '0000': '1',
                '0001': 'AL',
                '0002': 'My Alabama'
            }
        ]
    };

    var hiddenCharsData = {
        'columns': [
            {
                'id': 'col1',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string'
            },
            {
                'id': 'col2',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string'
            },
            {
                'id': 'col3',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string'
            },
            {
                'id': 'col4',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string'
            },
            {
                'id': 'col5',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string'
            }
        ],
        'records': [
            {
                'col1': 'AL',
                'col2': ' AL',
                'col3': 'AL ',
                'col4': ' AL '
            },
            {
                'col1': '  AL',
                'col2': 'AL  ',
                'col3': '  AL  ',
                'col4': '\tAL\n'
            }
        ]
    };


    function GridGetter(element) {
        this.row = function (index) {
            return new GridGetter(element.find('.slick-row').eq(index));
        };
        this.column = function (index) {
            return new GridGetter(element.find('.grid-header').eq(index));
        };
        this.cell = function (index) {
            return new GridGetter(element.find('.slick-cell').eq(index));
        };
        this.text = function () {
            return element.text();
        };
        this.nbCell = function () {
            return element.find('.slick-cell').length;
        };

        this.element = function () {
            return element;
        };
    }

    //return the text of the element without the included node text
    function getDirectText(element) {
        return element.contents().filter(function () {
            return this.nodeType === 3;
        })[0].nodeValue;
    }

    beforeEach(module('data-prep.datagrid'));
    beforeEach(module('htmlTemplates'));

    beforeEach(inject(function ($rootScope, $compile, $timeout, DatagridService, ColumnSuggestionService, StatisticsService) {
        scope = $rootScope.$new();
        element = angular.element('<datagrid></datagrid>');
        $compile(element)(scope);
        $timeout.flush();
        scope.$digest();

        angular.element('body').append(element);
        spyOn(DatagridService, 'setSelectedColumn').and.returnValue(null);
        spyOn(ColumnSuggestionService, 'setColumn').and.returnValue(null);
        spyOn(StatisticsService, 'processVisuData').and.returnValue();
    }));

    afterEach(inject(function ($window) {
        element.remove();
        $window.localStorage.removeItem('col_size_12ce6c32-bf80-41c8-92e5-66d70f22ec1f');
    }));

    it('should render dataset values', inject(function (DatagridService) {
        //when
        DatagridService.setDataset(metadata, data);
        scope.$digest();

        //then
        var grid = new GridGetter(element);
        var firstRow = grid.row(0);
        var secondRow = grid.row(1);

        expect(firstRow.nbCell()).toBe(5);
        expect(firstRow.cell(0).text()).toBe('1');
        expect(firstRow.cell(1).text()).toBe('AL');
        expect(firstRow.cell(2).text()).toBe('My Alabama');
        expect(firstRow.cell(3).text()).toBe('Montgomery');

        expect(secondRow.nbCell()).toBe(5);
        expect(secondRow.cell(0).text()).toBe('2');
        expect(secondRow.cell(1).text()).toBe('AK');
        expect(secondRow.cell(2).text()).toBe('Alaska');
        expect(secondRow.cell(3).text()).toBe('Juneau');
    }));

    it('should highlight cells containing clicked value', inject(function (DatagridService) {
        //given
        var colIndex = 2;

        DatagridService.setDataset(metadata, data);
        scope.$digest();

        //when
        var grid = new GridGetter(element);
        grid.row(0).cell(colIndex).element().click();
        scope.$digest();

        //then
        expect(grid.row(0).cell(colIndex).element().hasClass('highlight')).toBe(true);
        expect(grid.row(1).cell(colIndex).element().hasClass('highlight')).toBe(false);
        expect(grid.row(2).cell(colIndex).element().hasClass('highlight')).toBe(true);
    }));

    it('should set column background on cell clicked', inject(function (DatagridService) {
        //given
        var colIndex = 2;
        var grid = new GridGetter(element);

        DatagridService.setDataset(metadata, data);
        scope.$digest();

        //when
        grid.row(0).cell(colIndex).element().click();
        scope.$digest();

        //then
        expect(grid.row(0).cell(colIndex).element().hasClass('selected')).toBe(true);
        expect(grid.row(1).cell(colIndex).element().hasClass('selected')).toBe(true);
    }));

    it('should not change column background on click on a selected column cell', inject(function (DatagridService) {
        //given
        var colIndex = 2;
        var grid = new GridGetter(element);

        DatagridService.setDataset(metadata, data);
        scope.$digest();

        grid.row(0).cell(colIndex).element().click();
        scope.$digest();

        expect(grid.row(0).cell(colIndex).element().hasClass('selected')).toBe(true);
        expect(grid.row(1).cell(colIndex).element().hasClass('selected')).toBe(true);

        //when
        grid.row(1).cell(colIndex).element().click();
        scope.$digest();

        //then
        expect(grid.row(0).cell(colIndex).element().hasClass('selected')).toBe(true);
        expect(grid.row(1).cell(colIndex).element().hasClass('selected')).toBe(true);
    }));

    it('should set selected column on cell clicked', inject(function (DatagridService) {
        //given
        var colIndex = 2;

        DatagridService.setDataset(metadata, data);
        scope.$digest();

        //when
        var grid = new GridGetter(element);
        grid.row(0).cell(colIndex).element().click();
        scope.$digest();

        //then
        expect(DatagridService.setSelectedColumn).toHaveBeenCalledWith('0002');
    }));

    it('should trigger chart rendering according to the column domain', inject(function ($timeout, DatagridService, StatisticsService) {
        //given
        var colIndex = 2;

        DatagridService.setDataset(metadata, data);
        scope.$digest();

        //when
        var grid = new GridGetter(element);
        grid.row(0).cell(colIndex).element().click();
        scope.$digest();
        $timeout.flush();

        //then
        expect(StatisticsService.processVisuData).toHaveBeenCalledWith(data.columns[colIndex]);
    }));

    it('should set column in transfromation suggestion service on cell clicked', inject(function (DatagridService, ColumnSuggestionService) {
        //given
        var colIndex = 2;

        DatagridService.setDataset(metadata, data);
        scope.$digest();

        //when
        var grid = new GridGetter(element);
        grid.row(0).cell(colIndex).element().click();
        scope.$digest();

        //then
        expect(ColumnSuggestionService.setColumn).toHaveBeenCalledWith(data.columns[colIndex]);
    }));

    it('should highlight empty cells only', inject(function (DatagridService) {
        //given
        var colIndex = 2;

        DatagridService.setDataset(metadata, dataWithEmptyCell);
        scope.$digest();

        //when
        var grid = new GridGetter(element);
        grid.row(1).cell(colIndex).element().click();
        scope.$digest();

        //then
        expect(grid.row(0).cell(colIndex).element().hasClass('highlight')).toBe(false);
        expect(grid.row(1).cell(colIndex).element().hasClass('highlight')).toBe(true);
        expect(grid.row(2).cell(colIndex).element().hasClass('highlight')).toBe(true);
    }));

    it('should change col cells background on column header click', inject(function (DatagridService) {
        //given
        DatagridService.setDataset(metadata, dataWithEmptyCell);
        scope.$digest();

        //when
        element.find('#datagrid-header-1').eq(0).click();

        //then
        var grid = new GridGetter(element);
        expect(grid.row(0).cell(1).element().hasClass('selected')).toBe(true);
        expect(grid.row(1).cell(1).element().hasClass('selected')).toBe(true);
    }));

    it('should change selected column on column header click', inject(function (DatagridService) {
        //given
        DatagridService.setDataset(metadata, dataWithEmptyCell);
        scope.$digest();

        //when
        element.find('#datagrid-header-1').eq(0).click();

        //then
        expect(DatagridService.setSelectedColumn).toHaveBeenCalledWith('0001');
    }));

    it('should change selected column in transformation suggestion service on column header click', inject(function (DatagridService, ColumnSuggestionService) {
        //given
        DatagridService.setDataset(metadata, dataWithEmptyCell);
        scope.$digest();

        //when
        element.find('#datagrid-header-1').eq(0).click();

        //then
        expect(ColumnSuggestionService.setColumn).toHaveBeenCalledWith(dataWithEmptyCell.columns[1]);
    }));

    it('should do nothing on already selected column header click', inject(function ($timeout, DatagridService) {
        //given
        var grid = new GridGetter(element);
        DatagridService.setDataset(metadata, data);
        scope.$digest();

        element.find('#datagrid-header-1').eq(0).click();
        expect(DatagridService.setSelectedColumn.calls.count()).toBe(1);
        expect(grid.row(0).cell(1).element().hasClass('selected')).toBe(true);
        expect(grid.row(1).cell(1).element().hasClass('selected')).toBe(true);

        //when
        element.find('#datagrid-header-1').eq(0).click();
        $timeout.flush();

        //then
        expect(DatagridService.setSelectedColumn.calls.count()).toBe(1);
        expect(grid.row(0).cell(1).element().hasClass('selected')).toBe(true);
        expect(grid.row(1).cell(1).element().hasClass('selected')).toBe(true);
    }));

    it('should reset line style and reset active cell, but keep column selection when filter change', inject(function (FilterService, DatagridService) {
        //given
        DatagridService.setDataset(metadata, data);
        scope.$digest();

        var grid = new GridGetter(element);
        grid.row(0).cell(0).element().click();
        scope.$digest();

        expect(grid.row(0).cell(0).element().hasClass('highlight')).toBe(true);
        expect(grid.row(0).element().hasClass('active')).toBe(true);
        expect(grid.row(0).cell(0).element().hasClass('selected')).toBe(true);
        expect(grid.row(1).cell(0).element().hasClass('selected')).toBe(true);

        //when
        FilterService.addFilter('contains', '0002', 'State', {phrase: 'AL'});
        scope.$digest();

        //then
        expect(grid.row(0).cell(0).element().hasClass('highlight')).toBe(false);
        expect(grid.row(0).element().hasClass('active')).toBe(false);
        expect(grid.row(0).cell(0).element().hasClass('selected')).toBe(true);
        expect(grid.row(1).cell(0).element().hasClass('selected')).toBe(true);
    }));

    it('should add a "new cell" class and fill content with a space if empty', inject(function (FilterService, DatagridService) {
        //when
        DatagridService.setDataset(metadata, newData);
        scope.$digest();

        //then
        var grid = new GridGetter(element);
        expect(grid.row(0).cell(0).element().find('> div').eq(0).hasClass('cellNewValue')).toBe(true);
        expect(grid.row(0).cell(1).element().find('> div').eq(0).hasClass('cellNewValue')).toBe(true);
        expect(grid.row(0).cell(1).element().find('> div').eq(0).text()).toBe(' ');
        expect(grid.row(0).cell(2).element().find('> div').eq(0).hasClass('cellNewValue')).toBe(true);
    }));

    it('should add a "delete cell" class and fill content with a space if empty', inject(function (FilterService, DatagridService) {
        //when
        DatagridService.setDataset(metadata, deletedRowData);
        scope.$digest();

        //then
        var grid = new GridGetter(element);
        expect(grid.row(0).cell(0).element().find('> div').eq(0).hasClass('cellDeletedValue')).toBe(true);
        expect(grid.row(0).cell(1).element().find('> div').eq(0).hasClass('cellDeletedValue')).toBe(true);
        expect(grid.row(0).cell(1).element().find('> div').eq(0).text()).toBe(' ');
        expect(grid.row(0).cell(2).element().find('> div').eq(0).hasClass('cellDeletedValue')).toBe(true);
    }));

    it('should add a "delete cell" when column is deleted', inject(function (FilterService, DatagridService) {
        //when
        DatagridService.setDataset(metadata, deletedColumnData);
        scope.$digest();

        //then
        var grid = new GridGetter(element);
        expect(grid.row(0).cell(0).element().find('> div').eq(0).hasClass('cellDeletedValue')).toBe(false);
        expect(grid.row(0).cell(1).element().find('> div').eq(0).hasClass('cellDeletedValue')).toBe(true);
        expect(grid.row(0).cell(1).element().find('> div').eq(0).text()).toBe(' ');
        expect(grid.row(0).cell(2).element().find('> div').eq(0).hasClass('cellDeletedValue')).toBe(false);
    }));

    it('should add a "delete column" when column is deleted', inject(function (FilterService, DatagridService) {
        //when
        DatagridService.setDataset(metadata, deletedColumnData);
        scope.$digest();

        //then
        var grid = new GridGetter(element);
        expect(grid.column(0).element().hasClass('deletedColumn')).toBe(false);
        expect(grid.column(1).element().hasClass('deletedColumn')).toBe(true);
        expect(grid.column(2).element().hasClass('deletedColumn')).toBe(false);

    }));


    it('should add a "new cell" when column is new', inject(function (FilterService, DatagridService) {
        //when
        DatagridService.setDataset(metadata, newColumnData);
        scope.$digest();

        //then
        var grid = new GridGetter(element);
        expect(grid.row(0).cell(0).element().find('> div').eq(0).hasClass('cellNewValue')).toBe(false);
        expect(grid.row(0).cell(1).element().find('> div').eq(0).hasClass('cellNewValue')).toBe(true);
        expect(grid.row(0).cell(1).element().find('> div').eq(0).text()).toBe('90210');
        expect(grid.row(0).cell(2).element().find('> div').eq(0).hasClass('cellNewValue')).toBe(false);
    }));

    it('should add a "new column" when column is new', inject(function (FilterService, DatagridService) {
        //when
        DatagridService.setDataset(metadata, newColumnData);
        scope.$digest();

        //then
        var grid = new GridGetter(element);
        expect(grid.column(0).element().hasClass('newColumn')).toBe(false);
        expect(grid.column(1).element().hasClass('newColumn')).toBe(true);
        expect(grid.column(2).element().hasClass('newColumn')).toBe(false);

    }));

    it('should add an "update cell" class', inject(function (FilterService, DatagridService) {
        //when
        DatagridService.setDataset(metadata, updateData);
        scope.$digest();

        //then
        var grid = new GridGetter(element);
        expect(grid.row(0).cell(0).element().find('> div').eq(0).hasClass('cellUpdateValue')).toBe(false);
        expect(grid.row(0).cell(1).element().find('> div').eq(0).hasClass('cellUpdateValue')).toBe(false);
        expect(grid.row(0).cell(2).element().find('> div').eq(0).hasClass('cellUpdateValue')).toBe(true);
    }));

    it('should add "hiddenChars class when leading or trailing invisible characters are encountered"', inject(function (FilterService, DatagridService) {
        //when
        DatagridService.setDataset(metadata, hiddenCharsData);
        scope.$digest();

        //then
        //ROW 0
        var grid = new GridGetter(element);
        //'AL'
        expect(grid.row(0).cell(0).element().find('> span').length).toBe(0);
        expect(grid.row(0).cell(0).text()).toBe('AL');
        //' AL'
        expect(grid.row(0).cell(1).element().find('> span').length).toBe(1);
        expect(grid.row(0).cell(1).element().find('> span').eq(0).hasClass('hiddenChars')).toBe(true);
        expect(grid.row(0).cell(1).element().find('> span').eq(0).hasClass('hiddenChars')).toBe(true);
        expect(grid.row(0).cell(1).element().find('> span').eq(0).text()).toBe(' ');
        expect(getDirectText(grid.row(0).cell(1).element())).toBe('AL');
        //'AL '
        expect(grid.row(0).cell(2).element().find('> span').length).toBe(1);
        expect(grid.row(0).cell(2).element().find('> span').eq(0).hasClass('hiddenChars')).toBe(true);
        expect(grid.row(0).cell(2).element().find('> span').eq(0).text()).toBe(' ');
        expect(getDirectText(grid.row(0).cell(2).element())).toBe('AL');
        //' AL '
        expect(grid.row(0).cell(3).element().find('> span').length).toBe(2);
        expect(grid.row(0).cell(3).element().find('> span').eq(0).hasClass('hiddenChars')).toBe(true);
        expect(grid.row(0).cell(3).element().find('> span').eq(0).text()).toBe(' ');
        expect(grid.row(0).cell(3).element().find('> span').eq(1).hasClass('hiddenChars')).toBe(true);
        expect(grid.row(0).cell(3).element().find('> span').eq(1).text()).toBe(' ');
        expect(getDirectText(grid.row(0).cell(3).element())).toBe('AL');


        //ROW 1
        //'  AL'
        expect(grid.row(1).cell(0).element().find('> span').length).toBe(1);
        expect(grid.row(1).cell(0).element().find('> span').eq(0).hasClass('hiddenChars')).toBe(true);
        expect(grid.row(1).cell(0).element().find('> span').eq(0).text()).toBe('  ');
        expect(getDirectText(grid.row(1).cell(0).element())).toBe('AL');
        //'AL  '
        expect(grid.row(1).cell(1).element().find('> span').length).toBe(1);
        expect(grid.row(1).cell(1).element().find('> span').eq(0).hasClass('hiddenChars')).toBe(true);
        expect(grid.row(1).cell(1).element().find('> span').eq(0).text()).toBe('  ');
        expect(getDirectText(grid.row(1).cell(1).element())).toBe('AL');
        //'  AL  '
        expect(grid.row(1).cell(2).element().find('> span').length).toBe(2);
        expect(grid.row(1).cell(2).element().find('> span').eq(0).hasClass('hiddenChars')).toBe(true);
        expect(grid.row(1).cell(2).element().find('> span').eq(0).text()).toBe('  ');
        expect(grid.row(1).cell(2).element().find('> span').eq(1).hasClass('hiddenChars')).toBe(true);
        expect(grid.row(1).cell(2).element().find('> span').eq(1).text()).toBe('  ');
        expect(getDirectText(grid.row(1).cell(2).element())).toBe('AL');
        //'\tAL\n'
        expect(grid.row(1).cell(3).element().find('> span').length).toBe(2);
        expect(grid.row(1).cell(3).element().find('> span').eq(0).hasClass('hiddenChars')).toBe(true);
        expect(grid.row(1).cell(3).element().find('> span').eq(0).text()).toBe('\t');
        expect(grid.row(1).cell(3).element().find('> span').eq(1).hasClass('hiddenChars')).toBe(true);
        expect(grid.row(1).cell(3).element().find('> span').eq(1).text()).toBe('\n');
        expect(getDirectText(grid.row(1).cell(3).element())).toBe('AL');
    }));

    it('should save columns sizes in local storage', inject(function ($window, DatagridService) {
        //when
        DatagridService.setDataset(metadata, updateData);
        scope.$digest();

        //then
        var sizes = JSON.parse($window.localStorage.getItem('col_size_12ce6c32-bf80-41c8-92e5-66d70f22ec1f'));
        expect('0000' in sizes).toBe(true);
        expect('0001' in sizes).toBe(true);
        expect('0002' in sizes).toBe(true);
    }));
});