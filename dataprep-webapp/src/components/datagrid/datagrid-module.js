(function() {
    'use strict';

    angular.module('data-prep.datagrid', [
        'talend.widget',
        'data-prep.datagrid-header',
        'data-prep.filter-list',
        'data-prep.recipe',
        'data-prep.transformation-list',
        'data-prep.services.dataset'
    ]);
})();