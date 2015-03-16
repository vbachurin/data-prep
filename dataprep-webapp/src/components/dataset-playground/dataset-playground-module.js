(function() {
    'use strict';

    angular.module('data-prep.dataset-playground', [
        'pascalprecht.translate',
        'talend.widget',
        'data-prep.datagrid',
        'data-prep.datagrid-header',
        'data-prep.filter-search',
        'data-prep.filter-list',
        'data-prep.recipe',
        'data-prep.transformation-list',
        'data-prep.services.dataset'
    ]);
})();