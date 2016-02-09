/*  ============================================================================

  Copyright (C) 2006-2016 Talend Inc. - www.talend.com

  This source code is available under agreement available at
  https://github.com/Talend/data-prep/blob/master/LICENSE

  You should have received a copy of the agreement
  along with this program; if not, write to Talend SA
  9 rue Pages 92150 Suresnes, France

  ============================================================================*/

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