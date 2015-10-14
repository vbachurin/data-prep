(function() {
    'use strict';

    function DataPrepApp() {
        return {
            restrict: 'E',
            templateUrl: 'components/app/app.html'
        };
    }

    angular.module('data-prep.app')
        .directive('dataprepApp', DataPrepApp);
})();