(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.export.service:ExportRestService
     * @description Dataset service. This service provide the entry point to the backend export REST api.<br/>
     * <b style="color: red;">WARNING : do NOT use this service directly.
     * {@link data-prep.services.dataset.service:ExportService ExportService} must be the only entry point for Export</b>
     */
    function ExportRestService($http, RestURLs) {
        var self = this;

        //--------------------------------------------------------------------------------------------------------------
        //---------------------------------------------------Dataset----------------------------------------------------
        //--------------------------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name exportTypes
         * @methodOf data-prep.services.dataset.service:DatasetRestService
         * @description returns the export types list
         * @returns {Promise} - the GET call promise
         */
        self.exportTypes = function() {
            return $http.get(RestURLs.exportUrl + '/types');
        };


    }

    angular.module('data-prep.services.export')
        .service('ExportRestService', ExportRestService);
})();