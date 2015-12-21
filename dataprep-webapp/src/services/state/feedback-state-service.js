(function() {
    'use strict';

    var feedbackState = {
        displayFeedback: false
    };

    /**
     * @ngdoc service
     * @name data-prep.services.state.service:FeedbackStateService
     * @description Manage the state of the feedback
     */
    function FeedbackStateService() {

        return {
            enableFeedback: enableFeedback,
            disableFeedback: disableFeedback
        };

        /**
         * @ngdoc method
         * @name enableFeedback
         * @methodOf data-prep.services.state.service:FeedbackStateService
         * @description enable the feedback to display
         */
        function enableFeedback() {
            feedbackState.displayFeedback = true;
        }

        /**
         * @ngdoc method
         * @name disableFeedback
         * @methodOf data-prep.services.state.service:FeedbackStateService
         * @description disable the feedback to display
         */
        function disableFeedback () {
            feedbackState.displayFeedback = false;
        }
    }

    angular.module('data-prep.services.state')
        .service('FeedbackStateService', FeedbackStateService)
        .constant('feedbackState', feedbackState);
})();