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
            },
            {
                'id': '3',
                'Postal': 'AL',
                'State': 'My Alabama 3',
                'Capital': 'Montgomery',
                'MostPopulousCity': 'Alabama city'
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

    it('should create sorted suggestions based on case insensitive typed word and current data from service', inject(function(DatasetGridService) {
        //given
        DatasetGridService.setDataset(metadata, data);
        var ctrl = createController();

        //when
        var suggestions = ctrl.filterSuggestOptions.suggest('ala');

        //then
        expect(suggestions.length).toBe(2);
        expect(suggestions[0]).toEqual({
            label: 'ala in <b>MostPopulousCity</b>',
            value: 'ala',
            columnId: 'MostPopulousCity'
        });
        expect(suggestions[1]).toEqual({
            label: 'ala in <b>State</b>',
            value: 'ala',
            columnId: 'State'
        });
    }));

    it('should create sorted suggestions based on typed word with wildcard', inject(function(DatasetGridService) {
        //given
        DatasetGridService.setDataset(metadata, data);
        var ctrl = createController();

        //when
        var suggestions = ctrl.filterSuggestOptions.suggest('ala*ma');

        //then
        expect(suggestions.length).toBe(2);
        expect(suggestions[0]).toEqual({
            label: 'ala*ma in <b>MostPopulousCity</b>',
            value: 'ala*ma',
            columnId: 'MostPopulousCity'
        });
        expect(suggestions[1]).toEqual({
            label: 'ala*ma in <b>State</b>',
            value: 'ala*ma',
            columnId: 'State'
        });
    }));

    it('should return empty array if typed string is empty', inject(function(DatasetGridService) {
        //given
        DatasetGridService.setDataset(metadata, data);
        var ctrl = createController();

        //when
        var suggestions = ctrl.filterSuggestOptions.suggest('');

        //then
        expect(suggestions.length).toBe(0);
    }));

    it('should reset input search on item select', inject(function(DatasetGridService) {
        //given
        DatasetGridService.setDataset(metadata, data);
        var ctrl = createController();
        ctrl.filterSearch = 'ala';

        //when
        /*jshint camelcase: false */
        ctrl.filterSuggestOptions.on_select({
            label: 'ala in <b>State</b>',
            value: 'ala',
            columnId: 'State'
        });

        //then
        expect(ctrl.filterSearch).toBe('');
    }));

});
