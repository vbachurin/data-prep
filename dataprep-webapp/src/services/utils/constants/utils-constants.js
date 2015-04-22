(function() {
    'use strict';

    angular.module('data-prep.services.utils')
        .constant('apiUrl', 'http://localhost:8083')
        //.constant('apiUrl', 'http://10.42.10.99:8888')
        .constant('disableDebug', false);
})();
