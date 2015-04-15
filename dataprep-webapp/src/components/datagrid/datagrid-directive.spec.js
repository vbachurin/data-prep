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

    beforeEach(inject(function($rootScope, $compile, $timeout, DatasetGridService) {
        scope = $rootScope.$new();
        element = angular.element('<datagrid></datagrid>');
        $compile(element)(scope);
        $timeout.flush();
        scope.$digest();

        angular.element('body').append(element);
        spyOn(DatasetGridService, 'setSelectedColumn').and.returnValue(null);
    }));

    afterEach(function() {
        element.remove();
    });

    it('should render dataset values', inject(function(DatasetGridService) {
        //when
        DatasetGridService.setDataset(metadata, data);
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

    it('should highlight cells containing clicked value', inject(function(DatasetGridService) {
        //given
        var colIndex = 2;

        DatasetGridService.setDataset(metadata, data);
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

    it('should set selected column on cell clicked', inject(function($timeout, DatasetGridService) {
        //given
        var colIndex = 2;

        DatasetGridService.setDataset(metadata, data);
        scope.$digest();

        //when
        var grid = new GridGetter(element);
        grid.row(0).cell(colIndex).element().click();
        scope.$digest();
        $timeout.flush();

        //then
        expect(DatasetGridService.setSelectedColumn).toHaveBeenCalledWith('State');
    }));

    it('should highlight empty cells only', inject(function(DatasetGridService) {
        //given
        var colIndex = 2;

        DatasetGridService.setDataset(metadata, dataWithEmptyCell);
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
});