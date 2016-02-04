import ExportRestService from './export-rest-service';
import ExportService from './export-service';

(() => {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.services.export
     * @description This module contains the services for export
     */
    angular.module('data-prep.services.export', ['data-prep.services.utils'])
        .service('ExportRestService', ExportRestService)
        .service('ExportService', ExportService);
})();