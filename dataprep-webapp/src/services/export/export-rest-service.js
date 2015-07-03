(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.export.service:ExportRestService
     * @description Export service. This service provide the entry point to the backend export REST api.<br/>
     * <b style="color: red;">WARNING : do NOT use this service directly.
     * {@link data-prep.services.export.service:ExportService ExportService} must be the only entry point for Export</b>
     */
    function ExportRestService($http, RestURLs) {
        var self = this;

        //--------------------------------------------------------------------------------------------------------------
        //---------------------------------------------------Dataset----------------------------------------------------
        //--------------------------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name exportTypes
         * @methodOf data-prep.services.export.service:ExportRestService
         * @description Fetch the available export types
         * @returns {Promise}  The GET call promise
         */
        self.exportTypes = function() {
            return $http.get(RestURLs.exportUrl + '/types');
        };


    }

    angular.module('data-prep.services.export')
        .service('ExportRestService', ExportRestService);
})();