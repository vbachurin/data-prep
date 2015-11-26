(function () {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.feedback.service:FeedbackService
     * @description feedback service. This service provide the entry point to feedback
     * @requires data-prep.services.feedback.service:FeedbackRestService
     */
    function FeedbackService(FeedbackRestService) {
        var service = {
            sendFeedback: sendFeedback
        };
        return service;

        function sendFeedback(feedbackOjb) {
            FeedbackRestService.sendFeedback(feedbackOjb);
        }

    }

    angular.module('data-prep.services.feedback')
        .service('FeedbackService', FeedbackService);
})();