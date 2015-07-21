describe('Moment filter', function () {
    'use strict';

    beforeEach(module('data-prep.services.utils'));

    it('should transform timestamp to human readable moment', inject(function ($filter) {
        // given
        var date = new Date();
        date.setDate(date.getDate() - 1);

        // when
        var result = $filter('TDPMoment')(date.getTime());

        // then
        expect(result).toEqual('a day ago');
    }));

    it('should transform date to human readable moment', inject(function ($filter) {
        // given
        var date = new Date();
        date.setDate(date.getDate() - 1);
        var dateAsString = date.getFullYear() + '/' +
            (date.getMonth() < 10 ? '0' : '') + (date.getMonth() + 1) + '/' +
            (date.getDate() < 10 ? '0' : '') + date.getDate();

        // when
        var result = $filter('TDPMoment')(dateAsString, 'YYYY/MM/DD');

        // then
        expect(result).toEqual('a day ago');
    }));
});