(function() {
    'use strict';

    function TalendConfirmService($rootScope, $compile, $document, $q, $timeout, $translate) {
        var body = $document.find('body').eq(0);
        var self = this;

        /**
         * Translate the texts to display
         * @param textIds - text ids for translation
         * @param textArgs - text translation args
         * @returns Promise
         */
        var translateTexts = function(textIds, textArgs) {
            return $translate(textIds, textArgs)
                .then(function(translations) {
                    return _.map(textIds, function(id) {
                        return translations[id];
                    });
                });
        };

        /**
         * Create confirm modal isolated scope
         * @param options - modal options
         * @param textIds - text ids for translation
         * @param textArgs - text translation args
         */
        var createScope = function(options, textIds, textArgs) {
            if(self.modalScope) {
                throw new Error('A confirm popup is already created');
            }
            self.modalScope = $rootScope.$new(true);
            translateTexts(textIds, textArgs)
                .then(function(translatedTexts) {
                    self.modalScope.texts = translatedTexts;
                });

            if(options) {
                self.modalScope.disableEnter = options.disableEnter;
            }
        };

        /**
         * Destroy the modal scope
         */
        var removeScope = function() {
            self.modalScope.$destroy();
            self.modalScope = null;
        };

        /**
         * Create the confirm modal element and attach it to the body
         */
        var createElement = function() {
            self.element = angular.element('<talend-confirm disable-enter="disableEnter" texts="texts"></talend-confirm>');
            $compile(self.element)(self.modalScope);
            body.append(self.element);
        };

        /**
         * Remove the the element
         */
        var removeElement = function() {
            self.element.remove();
            self.element = null;
        };

        /**
         * Remove the modal and reset everything
         */
        var close = function() {
            removeScope();
            removeElement();

            self.confirmResolve = null;
            self.confirmReject = null;
        };

        /**
         * Resolve the modal promise and destroy the modal
         */
        this.resolve = function() {
            self.confirmResolve();
            $timeout(close);
        };

        /**
         * Reject the modal promise and destroy the modal
         * @param cause - 'dismiss' if the modal is closed without clicking on a button
         */
        this.reject = function(cause) {
            self.confirmReject(cause);
            $timeout(close);
        };

        /**
         * Create the confirm modal element and return a promise that will be resolve on button click or modal dismiss
         * Example : TalendConfirmService.confirm({disableEnter: true}, 'First text', 'Second text')
         *
         * @param options - {disableEnter: boolean}
         * @param textIds - Array containing the texts ids to display
         * @param textArgs - Text translation args
         * @returns Promise
         */
        this.confirm = function(options, textIds, textArgs) {
            createScope(options, textIds, textArgs);
            createElement();

            return $q(function(resolve, reject) {
                self.confirmResolve = resolve;
                self.confirmReject = reject;
            });
        };
    }

    angular.module('talend.widget')
        .service('TalendConfirmService', TalendConfirmService);
})();