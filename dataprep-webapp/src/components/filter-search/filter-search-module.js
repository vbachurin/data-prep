(function() {
    'use strict';

    angular.module('data-prep.filter-search', [
        'MassAutoComplete',
        'pascalprecht.translate',
        'data-prep.services.dataset',
        'data-prep.services.filter'
    ]);
})();