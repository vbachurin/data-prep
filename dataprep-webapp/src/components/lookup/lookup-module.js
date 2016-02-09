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
     * @name data-prep.lookup
     * @description This module contains the dataset lookup
     * @requires talend.widget
     * @requires data-prep.lookup-datagrid-header
     * @requires 'data-prep.services.statistics'
     * @requires 'data-prep.services.state'
     * @requires 'data-prep.services.utils'
     */
    angular.module('data-prep.lookup', [
        'talend.widget',
        'data-prep.services.state',
        'data-prep.services.dataset',
        'data-prep.services.transformation',
        'data-prep.lookup-datagrid-header',
        'data-prep.services.statistics',
        'data-prep.services.utils',
        'data-prep.services.lookup'
    ]);
})();