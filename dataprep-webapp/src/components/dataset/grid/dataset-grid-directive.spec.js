describe('Dataset grid directive', function() {
    'use strict';
    var scope, element;

    beforeEach(module('data-prep-dataset'));
    beforeEach(module('htmlTemplates'));

    beforeEach(inject(function($rootScope, $compile) {
        scope = $rootScope.$new();
        element = angular.element('<dataset-grid></dataset-grid>');
        $compile(element)(scope);
        scope.$digest();

        angular.element('body').append(element);
    }));

    afterEach(function() {
        element.remove();
    });

    it('should render dataset values', inject(function(DatasetGridService) {
        //given
        var metadata = {
            'id': '12ce6c32-bf80-41c8-92e5-66d70f22ec1f',
            'name': 'US States',
            'author': 'anonymousUser',
            'created': '02-03-2015 14:52'
        };
        var data = {
            'columns': [
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
                    'State': 'Alabama',
                    'Capital': 'Montgomery',
                    'MostPopulousCity': 'Birmingham city'
                },
                {
                    'id': '2',
                    'Postal': 'AK',
                    'State': 'Alaska',
                    'Capital': 'Juneau',
                    'MostPopulousCity': 'Anchorage'
                }
            ]
        };

        //when
        DatasetGridService.setDataset(metadata, data);
        scope.$digest();

        //then
        var firstRowCells = element.find('.slick-row').eq(0).find('.slick-cell');
        var secondRowCells = element.find('.slick-row').eq(1).find('.slick-cell');
        
        expect(firstRowCells.length).toBe(4);
        expect(firstRowCells.eq(0).text()).toBe('AL');
        expect(firstRowCells.eq(1).text()).toBe('Alabama');
        expect(firstRowCells.eq(2).text()).toBe('Montgomery');
        expect(firstRowCells.eq(3).text()).toBe('Birmingham city');
        
        expect(secondRowCells.length).toBe(4);
        expect(secondRowCells.eq(0).text()).toBe('AK');
        expect(secondRowCells.eq(1).text()).toBe('Alaska');
        expect(secondRowCells.eq(2).text()).toBe('Juneau');
        expect(secondRowCells.eq(3).text()).toBe('Anchorage');
    }));
    
});