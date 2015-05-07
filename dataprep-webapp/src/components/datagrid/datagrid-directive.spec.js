describe('Datagrid directive', function() {
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
                'id': 'id',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'number'
            },
            {
                'id': 'Postal',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string'
            },
            {
                'id': 'State',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string'
            },
            {
                'id': 'Capital',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string'
            },
            {
                'id': 'MostPopulousCity',
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
                'id': '1',
                'Postal': 'AL',
                'State': 'My Alabama',
                'Capital': 'Montgomery',
                'MostPopulousCity': 'Birmingham city'
            },
            {
                'id': '2',
                'Postal': 'AK',
                'State': 'Alaska',
                'Capital': 'Juneau',
                'MostPopulousCity': 'Anchorage'
            },
            {
                'id': '3',
                'Postal': 'AL',
                'State': 'My Alabama 2',
                'Capital': 'Montgomery',
                'MostPopulousCity': 'Birmingham city'
            }
        ]
    };
    var dataWithEmptyCell = {
        'columns': [
            {
                'id': 'id',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'number'
            },
            {
                'id': 'Postal',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string'
            },
            {
                'id': 'State',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string'
            },
            {
                'id': 'Capital',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string'
            },
            {
                'id': 'MostPopulousCity',
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
                'id': '5',
                'Postal': 'AK',
                'State': 'Alaska',
                'Capital': 'Juneau',
                'MostPopulousCity': 'Anchorage'
            },
            {
                'id': '6',
                'Postal': '--',
                'State': '',
                'Capital': '--',
                'MostPopulousCity': '--'
            },
            {
                'id': '7',
                'Postal': '--',
                'State': '',
                'Capital': '--',
                'MostPopulousCity': '--'
            }
        ]
    };

    var newData = {
        'columns': [
            {
                'id': 'id',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'number'
            },
            {
                'id': 'Postal',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string'
            },
            {
                'id': 'State',
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
                'id': '1',
                'Postal': '',
                'State': 'My Alabama'
            }
        ]
    };

    var deleteData = {
        'columns': [
            {
                'id': 'id',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'number'
            },
            {
                'id': 'Postal',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string'
            },
            {
                'id': 'State',
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
                'id': '1',
                'Postal': '',
                'State': 'My Alabama'
            }
        ]
    };

    var updateData = {
        'columns': [
            {
                'id': 'id',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'number'
            },
            {
                'id': 'Postal',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string'
            },
            {
                'id': 'State',
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
                '__tdpDiff': {State: 'update'},
                'id': '1',
                'Postal': 'AL',
                'State': 'My Alabama'
            }
        ]
    };
    function GridGetter(element) {
        this.row = function(index) {
            return new GridGetter(element.find('.slick-row').eq(index));
        };
        this.cell = function(index) {
            return new GridGetter(element.find('.slick-cell').eq(index));
        };
        this.text = function() {
            return element.text();
        };
        this.nbCell = function() {
            return element.find('.slick-cell').length;
        };

        this.element = function() {
            return element;
        };
    }

    beforeEach(module('data-prep.datagrid'));
    beforeEach(module('htmlTemplates'));

    beforeEach(inject(function($rootScope, $compile, $timeout, DatagridService) {
        scope = $rootScope.$new();
        element = angular.element('<datagrid></datagrid>');
        $compile(element)(scope);
        $timeout.flush();
        scope.$digest();

        angular.element('body').append(element);
        spyOn(DatagridService, 'setSelectedColumn').and.returnValue(null);
    }));

    afterEach(function() {
        element.remove();
    });

    it('should render dataset values', inject(function(DatagridService) {
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

    it('should highlight cells containing clicked value', inject(function(DatagridService) {
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

    it('should set column background on cell clicked', inject(function(DatagridService) {
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

    it('should not change column background on click on a selected column cell', inject(function(DatagridService) {
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

    it('should set selected column on cell clicked', inject(function($timeout, DatagridService) {
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
        expect(DatagridService.setSelectedColumn).toHaveBeenCalledWith('State');
    }));

    it('should highlight empty cells only', inject(function(DatagridService) {
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

    it('should change col cells background on column header click', inject(function(DatagridService) {
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

    it('should change selected column on column header click', inject(function($timeout, DatagridService) {
        //given
        DatagridService.setDataset(metadata, dataWithEmptyCell);
        scope.$digest();

        //when
        element.find('#datagrid-header-1').eq(0).click();
        $timeout.flush();

        //then
        expect(DatagridService.setSelectedColumn).toHaveBeenCalledWith('Postal');
    }));

    it('should do nothing on already selected column header click', inject(function($timeout, DatagridService) {
        //given
        var grid = new GridGetter(element);
        DatagridService.setDataset(metadata, data);
        scope.$digest();

        element.find('#datagrid-header-1').eq(0).click();
        $timeout.flush();
        expect(DatagridService.setSelectedColumn.calls.count()).toBe(1);
        expect(grid.row(0).cell(1).element().hasClass('selected')).toBe(true);
        expect(grid.row(1).cell(1).element().hasClass('selected')).toBe(true);

        //when
        element.find('#datagrid-header-1').eq(0).click();
        try {
            $timeout.flush();
        }

        //then
        catch (e) {
            expect(DatagridService.setSelectedColumn.calls.count()).toBe(1);
            expect(grid.row(0).cell(1).element().hasClass('selected')).toBe(true);
            expect(grid.row(1).cell(1).element().hasClass('selected')).toBe(true);
            return;
        }

        throw Error('should have thrown exception because no deferred task to flush');
    }));

    it('should reset line style and reset active cell, but keep column selection when filter change', inject(function(FilterService, DatagridService) {
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
        FilterService.addFilter('contains', 'State', {phrase: 'AL'});
        scope.$digest();

        //then
        expect(grid.row(0).cell(0).element().hasClass('highlight')).toBe(false);
        expect(grid.row(0).element().hasClass('active')).toBe(false);
        expect(grid.row(0).cell(0).element().hasClass('selected')).toBe(true);
        expect(grid.row(1).cell(0).element().hasClass('selected')).toBe(true);
    }));

    it('should add a "new cell" class and fill content with a space if empty', inject(function(FilterService, DatagridService) {
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

    it('should add a "delete cell" class and fill content with a space if empty', inject(function(FilterService, DatagridService) {
        //when
        DatagridService.setDataset(metadata, deleteData);
        scope.$digest();

        //then
        var grid = new GridGetter(element);
        expect(grid.row(0).cell(0).element().find('> div').eq(0).hasClass('cellDeletedValue')).toBe(true);
        expect(grid.row(0).cell(1).element().find('> div').eq(0).hasClass('cellDeletedValue')).toBe(true);
        expect(grid.row(0).cell(1).element().find('> div').eq(0).text()).toBe(' ');
        expect(grid.row(0).cell(2).element().find('> div').eq(0).hasClass('cellDeletedValue')).toBe(true);
    }));

    it('should add an "update cell" class', inject(function(FilterService, DatagridService) {
        //when
        DatagridService.setDataset(metadata, updateData);
        scope.$digest();

        //then
        var grid = new GridGetter(element);
        expect(grid.row(0).cell(0).element().find('> div').eq(0).hasClass('cellUpdateValue')).toBe(false);
        expect(grid.row(0).cell(1).element().find('> div').eq(0).hasClass('cellUpdateValue')).toBe(false);
        expect(grid.row(0).cell(2).element().find('> div').eq(0).hasClass('cellUpdateValue')).toBe(true);
    }));
});