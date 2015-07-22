(function () {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.utils.filter:MessageService
     * @description Display message toasts
     */
    function TDPMoment() {
        return function (dateString, format) {
            return moment(dateString, format ? format : 'x').fromNow();
        };
    }

    angular.module('data-prep.services.utils')
        .filter('TDPMoment', TDPMoment);
})();