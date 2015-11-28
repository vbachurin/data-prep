(function () {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.feedback.service:FeedbackRestService
     * @description Feedback service. This service provide the entry point to feedback
     */
    function FeedbackRestService($http, RestURLs) {
        return {
            sendFeedback: sendFeedback
        };

        /**
         * @ngdoc method
         * @name sendFeedback
         * @methodOf data-prep.services.feedback.service:FeedbackRestService
         * @param {string} feedback The feedback information
         * @description Send a feedback
         */
        function sendFeedback(feedback) {
            var request = {
                method: 'PUT',
                url: RestURLs.mailUrl,
                data: feedback
            };

            return $http(request);
        }

    }

    angular.module('data-prep.services.feedback')
        .service('FeedbackRestService', FeedbackRestService);
})();