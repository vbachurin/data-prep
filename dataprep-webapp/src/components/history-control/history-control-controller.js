(function() {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.history-control.controller:HistoryControlCtrl
     * @description History control controller.<br/>
     * This controller expose the {@link data-prep.services.history.service:HistoryService HistoryService} functions
     * @requires data-prep.services.history.service:HistoryService
     */
    function HistoryControlCtrl(HistoryService) {
        var vm = this;
        vm.service = HistoryService;
    }

    angular.module('data-prep.history-control')
        .controller('HistoryControlCtrl', HistoryControlCtrl);
})();