/*  ============================================================================

  Copyright (C) 2006-2016 Talend Inc. - www.talend.com

  This source code is available under agreement available at
  https://github.com/Talend/data-prep/blob/master/LICENSE

  You should have received a copy of the agreement
  along with this program; if not, write to Talend SA
  9 rue Pages 92150 Suresnes, France

  ============================================================================*/

(function () {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.services.dataset-workflow
     * @description This module contains the services to manage the upload & update of datasets
     * @requires data-prep.services.dataset
     * @requires data-prep.services.utils
     * @requires ui.router
     * @requires data-prep.services.dataset
     */
    angular.module('data-prep.services.datasetWorkflowService', [
        'data-prep.services.dataset',
        'data-prep.services.utils',
        'ui.router',
        'data-prep.services.state'
    ]);
})();