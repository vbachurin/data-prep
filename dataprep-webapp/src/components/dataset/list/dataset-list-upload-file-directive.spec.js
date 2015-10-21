describe('Upload File directive', function() {
    'use strict';

    var element, createElement;
    var body = angular.element('body');
    beforeEach(module('data-prep.dataset-list'));

    beforeEach(inject(function($rootScope, $compile) {
        createElement = function() {
            element = angular.element('<div upload-file="updateDatasetFile_0"></div><input id="updateDatasetFile_0">');
            body.append(element);
            $compile(element)($rootScope.$new());
        };
    }));

    it('should trigger click', function() {
        //given
        createElement();
        var event = angular.element.Event('click');
        var spyEvent = spyOnEvent($('#updateDatasetFile_0')[0], 'click'); // jshint ignore:line
        //when
        $('div').eq(0).trigger(event);

        //then
        expect(spyEvent).toHaveBeenTriggered();
    });
});