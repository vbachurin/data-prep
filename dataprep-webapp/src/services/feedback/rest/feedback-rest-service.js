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
            //var url = RestURLs.folderUrl + '/datasets';
            //return $http.get(url);
        }

    }

    angular.module('data-prep.services.feedback')
        .service('FeedbackRestService', FeedbackRestService);
})();