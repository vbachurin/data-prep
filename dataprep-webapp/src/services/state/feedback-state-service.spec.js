describe('Feedback', function () {
    'use strict';

    beforeEach(module('data-prep.services.state'));

    describe('state service', function() {

        it('should enable an feedback', inject(function (feedbackState, FeedbackStateService) {
            //given
            //when
            FeedbackStateService.enableFeedback();

            //then
            expect(feedbackState.displayFeedback).toBe(true);
        }));

        it('should disable an feedback', inject(function (feedbackState, FeedbackStateService) {
            //given
            //when
            FeedbackStateService.disableFeedback();

            //then
            expect(feedbackState.displayFeedback).toBe(false);
        }));
    });
});
