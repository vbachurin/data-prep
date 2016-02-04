import FeedbackCtrl from './feedback-controller';
import Feedback from './feedback-directive';

(() => {
    'use strict';

    angular.module('data-prep.feedback',
        [
            'talend.widget',
            'data-prep.services.utils',
            'data-prep.services.feedback',
            'data-prep.services.state'
        ])
        .controller('FeedbackCtrl', FeedbackCtrl)
        .directive('feedback', Feedback);
})();