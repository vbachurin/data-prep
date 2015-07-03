(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.export.service:ExportService
     * @description Export service. This service provide the entry point to the backend export REST api.
     * @requires data-prep.services.export.service:ExportRestService
     */
    function ExportService($window, ExportRestService) {
        var EXPORT_PARAMS_KEY = 'datarep.export.params';
        var self = this;

        this.exportTypes = [];

        /**
         * @ngdoc method
         * @name getParameters
         * @methodOf data-prep.services.export.service:ExportService
         * @description Get the saved export parameters from localStorage
         */
        this.getParameters = function getParameters() {
            var params = $window.localStorage.getItem(EXPORT_PARAMS_KEY);
            return params ? JSON.parse(params) : null;
        };

        /**
         * @ngdoc method
         * @name setParameters
         * @methodOf data-prep.services.export.service:ExportService
         * @description Save export parameters in localStorage
         */
        this.setParameters = function setParameters(params) {
            $window.localStorage.setItem(EXPORT_PARAMS_KEY, JSON.stringify(params));
        };

        /**
         * @ngdoc method
         * @name getType
         * @methodOf data-prep.services.export.service:ExportService
         * @description Get the type by id
         */
        this.getType = function getType(id) {
            return _.find(self.exportTypes, function(type) {
                return type.id === id;
            });
        };

        /**
         * @ngdoc method
         * @name saveDefaultExport
         * @methodOf data-prep.services.export.service:ExportService
         * @description [PRIVATE] Save the default export in localStorage
         */
        var saveDefaultExport = function saveDefaultExport() {
            var exportType = _.find(self.exportTypes, function(type) {
                return type.defaultExport === 'true';
            });
            self.setParameters({exportType: exportType.id});
        };

        /**
         * @ngdoc method
         * @name refreshTypes
         * @methodOf data-prep.services.export.service:ExportService
         * @description Refresh the export types list and save default if no parameters has been saved yet
         */
        this.refreshTypes = function refreshTypes() {
            return ExportRestService.exportTypes()
                .then(function (response) {
                    self.exportTypes = response.data;

                    // save default export if no parameter has been saved yet
                    if (!self.getParameters() && self.exportTypes.length) {
                        saveDefaultExport();
                    }

                    return self.exportTypes;
                });
        };
    }

    angular.module('data-prep.services.export')
        .service('ExportService', ExportService);
})();