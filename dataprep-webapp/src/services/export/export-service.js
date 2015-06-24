(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.export.service:ExportService
     * @description Export general service. This service manage the operations that touches the export
     * @requires data-prep.services.export.service:ExportRestService
     */
    function ExportService($q, ExportRestService) {
        var self = this;

        //--------------------------------------------------------------------------------------------------------------
        //---------------------------------------------------Export----------------------------------------------------
        //--------------------------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name exportTypes
         * @methodOf data-prep.services.export.service:ExportService
         * @description return all available export types
         * @returns {promise} The pending GET or resolved promise
         */
        self.exportTypes = function() {
            return ExportRestService.exportTypes();
        };

    }

    angular.module('data-prep.services.export')
        .service('ExportService', ExportService);
})();