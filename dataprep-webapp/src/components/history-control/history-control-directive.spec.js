describe('History control directive', function() {
    'use strict';

    var createElement, element, scope, body;

    beforeEach(module('data-prep.history-control'));
    beforeEach(module('htmlTemplates'));

    beforeEach(inject(function($rootScope, $compile, HistoryService) {
        body = angular.element('body');
        scope = $rootScope.$new();

        createElement = function() {
            element = angular.element('<history-control></history-control>');
            body.append(element);
            $compile(element)(scope);
            scope.$digest();
        };

        spyOn(HistoryService, 'undo').and.returnValue();
        spyOn(HistoryService, 'redo').and.returnValue();
    }));

    afterEach(function() {
        scope.$destroy();
        element.remove();
    });

    it('should attach undo to Ctrl+Z shortcut', inject(function($document, HistoryService) {
        //given
        createElement();

        var event = angular.element.Event('keydown');
        event.keyCode = 90;
        event.ctrlKey = true;

        //when
        $document.trigger(event);

        //then
        expect(HistoryService.undo).toHaveBeenCalled();
    }));

    it('should attach redo to Ctrl+Y shortcut', inject(function($document, HistoryService) {
        //given
        createElement();

        var event = angular.element.Event('keydown');
        event.keyCode = 89;
        event.ctrlKey = true;

        //when
        $document.trigger(event);

        //then
        expect(HistoryService.redo).toHaveBeenCalled();
    }));
});