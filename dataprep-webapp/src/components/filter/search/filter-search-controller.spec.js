describe('filter search controller', function() {
    'use strict';

    var createController, scope;

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
                '0000': '1',
                '0001': 'AL',
                '0002': 'My Alabama',
                '0003': 'Montgomery',
                '0004': 'Birmingham city',
                tdpId: 0
            },
            {
                '0000': '2',
                '0001': 'AK',
                '0002': 'Alaska',
                '0003': 'Juneau',
                '0004': 'Anchorage',
                tdpId: 1
            },
            {
                '0000': '3',
                '0001': 'AL',
                '0002': 'My Alabama 2',
                '0003': 'Montgomery',
                '0004': 'Birmingham city',
                tdpId: 2
            },
            {
                '0000': '3',
                '0001': 'AL',
                '0002': 'My Alabama 3',
                '0003': 'Montgomery',
                '0004': 'Alabama city',
                tdpId: 3
            }
        ]
    };

    beforeEach(module('data-prep.filter-search'));

    beforeEach(inject(function($rootScope, $controller) {
        scope = $rootScope.$new();

        createController = function() {
            var ctrl =  $controller('FilterSearchCtrl', {
                $scope: scope
            });
            return ctrl;
        };
    }));

    it('should create sorted suggestions based on case insensitive typed word and current data from service', inject(function(DatagridService) {
        //given
        DatagridService.setDataset(metadata, data);
        var ctrl = createController();

        //when
        var suggestions = ctrl.filterSuggestOptions.suggest('ala');

        //then
        expect(suggestions.length).toBe(2);
        expect(suggestions[0]).toEqual({
            label: 'ala in <b>MostPopulousCity</b>',
            value: 'ala',
            columnId: '0004',
            columnName: 'MostPopulousCity'
        });
        expect(suggestions[1]).toEqual({
            label: 'ala in <b>State</b>',
            value: 'ala',
            columnId: '0002',
            columnName: 'State'
        });
    }));

    it('should create sorted suggestions based on typed word with wildcard', inject(function(DatagridService) {
        //given
        DatagridService.setDataset(metadata, data);
        var ctrl = createController();

        //when
        var suggestions = ctrl.filterSuggestOptions.suggest('ala*ma');

        //then
        expect(suggestions.length).toBe(2);
        expect(suggestions[0]).toEqual({
            label: 'ala*ma in <b>MostPopulousCity</b>',
            value: 'ala*ma',
            columnId: '0004',
            columnName: 'MostPopulousCity'

        });
        expect(suggestions[1]).toEqual({
            label: 'ala*ma in <b>State</b>',
            value: 'ala*ma',
            columnId: '0002',
            columnName: 'State'
        });
    }));

    it('should return empty array if typed string is empty', inject(function(DatagridService) {
        //given
        DatagridService.setDataset(metadata, data);
        var ctrl = createController();

        //when
        var suggestions = ctrl.filterSuggestOptions.suggest('');

        //then
        expect(suggestions.length).toBe(0);
    }));

    it('should reset input search on item select', inject(function(DatagridService) {
        //given
        DatagridService.setDataset(metadata, data);
        var ctrl = createController();
        ctrl.filterSearch = 'ala';

        //when
        /*jshint camelcase: false */
        ctrl.filterSuggestOptions.on_select({
            label: 'ala in <b>State</b>',
            value: 'ala',
            columnName: 'State',
            columnId: '0002'
        });

        //then
        expect(ctrl.filterSearch).toBe('');
    }));

});
