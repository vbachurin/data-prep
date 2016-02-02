/**
 * @ngdoc service
 * @name data-prep.services.feedback.service:FeedbackRestService
 * @description Feedback service. This service provide the entry point to feedback
 */
export default class FeedbackRestService {
    constructor($http, RestURLs) {
        'ngInject';
        this.$http = $http;
        this.url = RestURLs.mailUrl;
    }

    /**
     * @ngdoc method
     * @name sendFeedback
     * @methodOf data-prep.services.feedback.service:FeedbackRestService
     * @param {string} feedback The feedback information
     * @description Send a feedback
     */
    sendFeedback(feedback) {
        var request = {
            method: 'PUT',
            url: this.url,
            data: feedback
        };

        return this.$http(request);
    }
}