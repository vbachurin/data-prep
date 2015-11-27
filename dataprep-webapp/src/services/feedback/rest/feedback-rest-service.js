(function () {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.feedback.service:FeedbackRestService
     * @description feedback service. This service provide the entry point to feedback
     */
    function FeedbackRestService($http, RestURLs) {
        var service = {
            sendFeedback: sendFeedback
        };
        return service;

        function sendFeedback(feedbackOjb) {
            var request = {
                method: 'PUT',
                url: RestURLs.mailUrl,
                headers: {
                    'Content-Type': 'application/json'
                },
                data: feedbackOjb
            };

            return $http(request);
        }

    }

    angular.module('data-prep.services.feedback')
        .service('FeedbackRestService', FeedbackRestService);
})();