describe('Actions suggestions search filter', function () {
    'use strict';

    var transformations = [
        {
            'name': 'ceil_value',
            'label': 'Ceil value',
            'labelHtml': 'Ceil value'
        },
        {
            'name': 'floor_value',
            'label': 'Floor value',
            'labelHtml': 'Floor value'
        },
        {
            'name': 'round_value',
            'label': 'Round value',
            'labelHtml': 'Round value'
        }
    ];

    beforeEach(module('data-prep.actions-suggestions'));

    it('should filter action list', inject(function ($filter) {
        //given
        var actionSearchFilter = $filter('actionsSuggestionsSearchFilter');
        var result = [];

        //when
        result = actionSearchFilter(transformations, '');
        //then
        expect(result.length).toBe(3);
        expect(result[0].label).toBe('Ceil value');
        expect(result[1].label).toBe('Floor value');
        expect(result[2].label).toBe('Round value');

        //when
        result = actionSearchFilter(transformations, 'o');
        //then
        expect(result.length).toBe(2);
        expect(result[0].label).toBe('Floor value');
        expect(result[1].label).toBe('Round value');

        //when
        result = actionSearchFilter(transformations, 'oo');
        //then
        expect(result.length).toBe(1);
        expect(result[0].label).toBe('Floor value');

        //when
        result = actionSearchFilter(transformations, 'ooo');
        //then
        expect(result.length).toBe(0);

    }));
});
