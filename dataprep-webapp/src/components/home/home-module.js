(function() {
    'use strict';

    angular.module('data-prep.home', [
        'talend.widget',
        'data-prep.dataset-upload-list',
        'data-prep.dataset-list',
        'data-prep.playground',
        'data-prep.preparation-list',
        'data-prep.services.dataset',
        'data-prep.services.utils'
    ]);
})();