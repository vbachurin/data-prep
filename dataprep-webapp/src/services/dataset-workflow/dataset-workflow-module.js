import UploadWorkflowService from './upload-workflow-service';
import UpdateWorkflowService from './update-workflow-service';

(() => {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.services.dataset-workflow
     * @description This module contains the services to manage the upload & update of datasets
     * @requires ui.router
     * @requires data-prep.services.dataset
     * @requires data-prep.services.state
     * @requires data-prep.services.utils
     */
    angular.module('data-prep.services.datasetWorkflowService',
        [
        'ui.router',
        'data-prep.services.dataset',
        'data-prep.services.state',
        'data-prep.services.utils'
    ])
        .service('UploadWorkflowService', UploadWorkflowService)
        .service('UpdateWorkflowService', UpdateWorkflowService);
})();