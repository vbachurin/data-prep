describe('Dataset playground directive', function() {
    'use strict';

    var scope, createElement, element;

    var metadata = {
        'id': '12ce6c32-bf80-41c8-92e5-66d70f22ec1f',
        'name': 'US States',
        'author': 'anonymousUser',
        'created': '02-03-2015 14:52',
        records: '3'
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

    beforeEach(module('data-prep.dataset-playground'));
    beforeEach(module('htmlTemplates'));
    beforeEach(module('pascalprecht.translate', function ($translateProvider) {
        $translateProvider.translations('en', {
            'FILE_DETAILS': 'File: {{name}} ({{records}} lines)'
        });
        $translateProvider.preferredLanguage('en');
    }));

    beforeEach(inject(function($rootScope, $compile) {
        scope = $rootScope.$new();
        createElement = function() {
            element = angular.element('<dataset-playground></dataset-playground>');
            angular.element('body').append(element);

            $compile(element)(scope);
            scope.$digest();
        };
    }));

    afterEach(function() {
        scope.$destroy();
        element.remove();
    });

    it('should render dataset playground elements', inject(function(DatasetGridService) {
        //given
        DatasetGridService.setDataset(metadata, data);

        //when
        createElement();

        //then
        var playground = angular.element('body').find('.dataset-playground').eq(0);

        //check header is present and contains description and search filter
        expect(playground.find('.modal-header').length).toBe(1);
        expect(playground.find('.modal-header').eq(0).find('li').eq(0).text().trim()).toBe('File: US States (3 lines)');

        //check body is present
        expect(playground.find('.modal-body').length).toBe(1);

        //check left slidable is hidden recipe with left slide action
        expect(playground.find('.modal-body').eq(0).find('.slidable').eq(0).hasClass('recipe')).toBe(true);
        expect(playground.find('.modal-body').eq(0).find('.slidable').eq(0).hasClass('slide-hide')).toBe(true);
        expect(playground.find('.modal-body').eq(0).find('.slidable').eq(0).find('.action').eq(0).hasClass('right')).toBe(false);

        //check right slidable is displayed transformations with right slide action
        expect(playground.find('.modal-body').eq(0).find('.slidable').eq(1).hasClass('transformations')).toBe(true);
        expect(playground.find('.modal-body').eq(0).find('.slidable').eq(1).hasClass('slide-hide')).toBe(false);
        expect(playground.find('.modal-body').eq(0).find('.slidable').eq(1).find('.action').eq(0).hasClass('right')).toBe(true);

        //check datagrid and filters are present
        expect(playground.find('.modal-body').eq(0).find('.filter-list').length).toBe(1);
        expect(playground.find('.modal-body').eq(0).find('.filter-list').find('.filter-search').length).toBe(1);
        expect(playground.find('.modal-body').eq(0).find('datagrid').length).toBe(1);
    }));
});