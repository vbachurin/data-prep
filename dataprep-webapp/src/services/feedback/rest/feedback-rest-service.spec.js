describe('Feedback Rest Service', function () {
    'use strict';

    var $httpBackend;

    beforeEach(module('data-prep.services.feedback'));

    beforeEach(inject(function ($rootScope, $injector, RestURLs) {
        RestURLs.setServerUrl('');
        $httpBackend = $injector.get('$httpBackend');

        spyOn($rootScope, '$emit').and.returnValue();
    }));

    it('should send feed back by http', inject(function ($rootScope, FeedbackRestService, RestURLs) {
        //given
        var feedbackOjb = { title: ''};

        $httpBackend
            .expectPUT(RestURLs.mailUrl, feedbackOjb)
            .respond(200);

        //when
        FeedbackRestService.sendFeedback(feedbackOjb);
        $httpBackend.flush();
        $rootScope.$digest();

    }));

});