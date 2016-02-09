/*  ============================================================================

  Copyright (C) 2006-2016 Talend Inc. - www.talend.com

  This source code is available under agreement available at
  https://github.com/Talend/data-prep/blob/master/LICENSE

  You should have received a copy of the agreement
  along with this program; if not, write to Talend SA
  9 rue Pages 92150 Suresnes, France

  ============================================================================*/

(function() {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.services.dataset
     * @description This module contains the services to manipulate datasets
     * @requires data-prep.services.utils
     * @requires data-prep.services.preparation
     */
    angular.module('data-prep.services.dataset', [
        'data-prep.services.utils',
        'data-prep.services.preparation',
        'data-prep.services.state',
        'data-prep.services.folder',
        'angularFileUpload' //file upload with progress support
    ]);
})();