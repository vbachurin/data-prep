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
     * @name data-prep.easter-eggs
     * @description This module contains data prep easter eggs
     * @requires 'data-prep.services.easter-eggs'
     * @requires 'data-prep.services.state'
     * @requires 'data-prep.services.utils'
     */
    angular.module('data-prep.easter-eggs', [
        'data-prep.services.state',
        'data-prep.services.easter-eggs',
        'data-prep.services.utils']);
})();