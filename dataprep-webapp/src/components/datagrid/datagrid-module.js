(function() {
    'use strict';

    angular.module('data-prep.datagrid', [
        'data-prep.datagrid-tooltip',
        'data-prep.datagrid-header',
        'data-prep.services.dataset',
        'data-prep.services.filter'
    ]);
})();