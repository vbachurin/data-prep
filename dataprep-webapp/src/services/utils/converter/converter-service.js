(function() {
    'use strict';

    function ConverterService() {
        this.toInputType = function(type) {
            switch (type) {
                case 'numeric':
                case 'integer':
                case 'double':
                case 'float':
                    return 'number';
                default:
                    return 'text';
            }
        };
    }

    angular.module('data-prep.services.utils')
        .service('ConverterService', ConverterService);
})();