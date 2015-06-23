(function() {
    'use strict';

    angular.module('data-prep.services.utils')
        /**
         * @ngdoc object
         * @name data-prep.services.utils.service:apiUrl
         * @description The REST api base url
         */
        .constant('apiUrl', 'http://10.42.10.99:8888')  /* VM1 */
        // .constant('apiUrl', 'http://10.42.40.91:8888')  /* VINCENT */
        //.constant('apiUrl', 'http://192.168.40.134:8888')  /* MARC VM DOCKER  */
        /**
         * @ngdoc object
         * @name data-prep.services.utils.service:disableDebug
         * @description Application option. Disable debug mode (ex: in production) for performance
         */
        .constant('disableDebug', false);
})();
