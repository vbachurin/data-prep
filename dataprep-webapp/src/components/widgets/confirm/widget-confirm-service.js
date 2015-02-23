(function() {
    'use strict';

    function TalendConfirmService($rootScope, $compile, $document, $q, $timeout) {
        var body = $document.find('body').eq(0);
        var self = this;

        this.resolve = function() {
            self.confirmResolve();
            self.close();
        };

        this.reject = function(cause) {
            self.confirmReject(cause);
            self.close();
        };

        this.close = function() {
            self.element.remove();
            $timeout(function() {
                if(self.modalScope) {
                    self.modalScope.$destroy();
                }
            });
        };

        this.confirm = function(text) {
            self.modalScope = $rootScope.$new();
            self.modalScope.text = text;
            self.element = angular.element('<talend-confirm></talend-confirm>');

            $compile(self.element)(self.modalScope);

            body.append(self.element);

            return $q(function(resolve, reject) {
                self.confirmResolve = resolve;
                self.confirmReject = reject;
            });
        }
    }

    angular.module('talend.widget')
        .service('TalendConfirmService', TalendConfirmService);
})();