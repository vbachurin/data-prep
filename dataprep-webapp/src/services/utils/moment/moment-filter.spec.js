describe('Moment filter', function () {
    'use strict';

    beforeEach(module('data-prep.services.utils'));

    it('should transform timestamp to human readable moment', inject(function ($filter) {
        // given
        var date = new Date();
        date.setDate(date.getDate() - 7);

        // when
        var result = $filter('TDPMoment')(date.getTime());

        // then
        expect(result).toEqual('7 days ago');
    }));

    it('should transform date to human readable moment', inject(function ($filter) {
        // given
        var date = new Date();
        date.setDate(date.getDate() - 7);
        var dateAsString = date.getFullYear() + '/' +
            (date.getMonth() < 9 ? '0' : '') + (date.getMonth() + 1) + '/' +
            (date.getDate() < 10 ? '0' : '') + date.getDate() +
            ' ' + date.getHours() + ':' + date.getMinutes();

        // when
        var result = $filter('TDPMoment')(dateAsString, 'YYYY/MM/DD HH:mm');

        // then
        expect(result).toEqual('7 days ago');
    }));
});