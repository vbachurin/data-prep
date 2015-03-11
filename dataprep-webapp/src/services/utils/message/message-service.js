(function() {
    'use strict';

    function MessageService($translate, toaster) {
        var pop = function(type, titleKey, contentKey, args, timeout) {
            $translate([titleKey, contentKey], args)
                .then(function(translations) {
                    toaster.pop(type, translations[titleKey], translations[contentKey], timeout);
                });
        };

        this.error = function(titleKey, contentKey, args) {
            pop('error', titleKey, contentKey, args, 0);
        };

        this.success = function(titleKey, contentKey, args) {
            pop('success', titleKey, contentKey, args, 5000);
        };

        this.warning = function(titleKey, contentKey, args) {
            pop('warning', titleKey, contentKey, args, 0);
        };
    }

    angular.module('data-prep.services.utils')
        .service('MessageService', MessageService);
})();