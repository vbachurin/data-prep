(function() {
    'use strict';

    function MessageService($translate, toaster) {
        var pop = function(type, titleKey, contentKey, args) {
            $translate([titleKey, contentKey], args)
                .then(function(translations) {
                    toaster.pop(type, translations[titleKey], translations[contentKey]);
                });
        };

        this.error = function(titleKey, contentKey, args) {
            pop('error', titleKey, contentKey, args);
        };

        this.success = function(titleKey, contentKey, args) {
            pop('success', titleKey, contentKey, args);
        };

        this.warning = function(titleKey, contentKey, args) {
            pop('warning', titleKey, contentKey, args);
        };
    }

    angular.module('data-prep-utils')
        .service('MessageService', MessageService);
})();