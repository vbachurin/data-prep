(function() {
    'use strict';

    angular.module('data-prep.suggestions', [
        'talend.widget',
        'data-prep.table-suggestions',
        'data-prep.column-suggestions',
        'data-prep.column-profile',
        'data-prep.services.playground',
        'data-prep.services.transformation',
        'data-prep.services.preparation'
    ]);
})();