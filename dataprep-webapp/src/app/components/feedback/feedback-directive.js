/**
 * @ngdoc directive
 * @name data-prep.datagrid.directive:Feedback
 * @description This directive create a feedback form
 * @restrict E
 * @usage <feedback></feedback>
 */
export default function Feedback() {
    return {
        templateUrl: 'app/components/feedback/feedback.html',
        restrict: 'E',
        bindToController: true,
        controllerAs: 'feedbackCtrl',
        controller: 'FeedbackCtrl'
    };
}