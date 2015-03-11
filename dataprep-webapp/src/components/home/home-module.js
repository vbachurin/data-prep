(function() {
    'use strict';

    angular.module('data-prep.home', [
        'talend.widget',
        'data-prep.dataset-upload-list',
        'data-prep.dataset-list',
        'data-prep.datagrid',
        'data-prep.services.dataset',
        'data-prep.services.utils'
    ]);
})();