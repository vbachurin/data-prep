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
     * @name data-prep.transformation-form
     * @description This module contains the controller and directives to manage transformation parameters
     * @requires data-prep.services.state
     * @requires data-prep.services.utils.service
     * @requires data-prep.validation
     */
    angular.module('data-prep.transformation-form', [
        'data-prep.services.state',
        'data-prep.services.utils',
        'data-prep.validation',
        'talend.widget'
    ]);
})();