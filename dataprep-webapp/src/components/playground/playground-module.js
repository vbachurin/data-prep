(function() {
    'use strict';

    angular.module('data-prep.playground', [
        'ui.router',
        'pascalprecht.translate',
        'talend.widget',
        'data-prep.datagrid',
        'data-prep.export',
        'data-prep.filter-search',
        'data-prep.filter-list',
        'data-prep.recipe',
        'data-prep.suggestions',
        'data-prep.services.preparation',
        'data-prep.services.playground'
    ]);
})();