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
     * @name data-prep.datagrid
     * @description This module contains the controller and directives for the datagrid
     * @requires data-prep.datagrid-header
     * @requires 'data-prep.services.statistics'
     * @requires 'data-prep.services.state'
     * @requires 'data-prep.services.utils'
     */
    angular.module('data-prep.datagrid', [
        'data-prep.datagrid-header',
        'data-prep.services.statistics',
        'data-prep.services.state',
        'data-prep.services.utils'
    ]);
})();