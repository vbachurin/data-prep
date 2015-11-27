describe('Feedback service', function () {
    'use strict';

    beforeEach(module('data-prep.services.feedback'));

    describe('Feedback', function() {
        it('should send feedback', inject(function ($q, FeedbackService, FeedbackRestService) {
            //given
            var feedbackOjb = { title: ''};
            spyOn(FeedbackRestService, 'sendFeedback').and.returnValue($q.when(true));

            //when
            FeedbackService.sendFeedback(feedbackOjb);

            //then
            expect(FeedbackRestService.sendFeedback).toHaveBeenCalledWith(feedbackOjb);

        }));
    });
});