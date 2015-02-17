describe('Dropdown directive', function () {
    'use strict';
    
    var scope, createElement;

    beforeEach(module('talend.widget'));
    beforeEach(module('htmlTemplates'));

    beforeEach(inject(function ($rootScope, $compile) {
        scope = $rootScope.$new();

        createElement = function(directiveScope) {
            var html = '<talend-modal fullscreen="fullscreen" state="state" close-button="closeButton"></talend-modal>';
            var element = $compile(html)(directiveScope);
            directiveScope.$digest();
            return element;
        };
        
        spyOn($rootScope, '$apply').and.callThrough();
    }));

    it('should add "normal" close button in DOM', function () {
        //given
        scope.fullscreen = false;
        scope.state = false;
        scope.closeButton = true;

        //when
        var element = createElement(scope);

        //then
        expect(element.find('.modal-close').length).toBe(1);
    });

    it('should not add "normal" close button in DOM', function () {
        //given
        scope.fullscreen = false;
        scope.state = false;
        scope.closeButton = false;

        //when
        var element = createElement(scope);

        //then
        expect(element.find('.modal-close').length).toBe(0);
    });

    it('should add "fullscreen" close button in DOM', function () {
        //given
        scope.fullscreen = true;
        scope.state = false;
        scope.closeButton = true;

        //when
        var element = createElement(scope);

        //then
        expect(element.find('.modal-header-close').length).toBe(1);
    });

    it('should not add "fullscreen" close button in DOM', function () {
        //given
        scope.fullscreen = true;
        scope.state = false;
        scope.closeButton = false;

        //when
        var element = createElement(scope);

        //then
        expect(element.find('.modal-header-close').length).toBe(0);
    });

    it('should add "modal-open" class to body when modal open state is true', function () {
        //given
        var body = angular.element('body');
        
        scope.fullscreen = false;
        scope.state = false;
        scope.closeButton = false;
        createElement(scope);
        
        //when
        scope.state = true;
        scope.$digest();

        //then
        expect(body.hasClass('modal-open')).toBe(true);
    });

    it('should remove "modal-open" class to body when modal open state is false', function () {
        //given
        var body = angular.element('body');
        body.addClass('modal-open');
        
        scope.fullscreen = false;
        scope.state = true;
        scope.closeButton = false;
        createElement(scope);

        //when
        scope.state = false;
        scope.$digest();

        //then
        expect(body.hasClass('modal-open')).toBe(false);
    });

    it('should hide modal on "modal-window" div click', inject(function ($rootScope, $timeout) {
        //given
        scope.fullscreen = false;
        scope.state = true;
        scope.closeButton = false;
        var element = createElement(scope);
        $timeout.flush();
        expect($rootScope.$apply.calls.count()).toBe(1);

        //when
        element.find('.modal-window').click();
        $timeout.flush();

        //then
        expect($rootScope.$apply.calls.count()).toBe(2);
        expect(scope.state).toBe(false);
    }));

    it('should hide modal on "modal-close" button click', inject(function ($rootScope, $timeout) {
        //given
        scope.fullscreen = false;
        scope.state = true;
        scope.closeButton = true;
        var element = createElement(scope);
        $timeout.flush();
        expect($rootScope.$apply.calls.count()).toBe(1);

        //when
        element.find('.modal-close').click();
        $timeout.flush();

        //then
        expect($rootScope.$apply.calls.count()).toBe(2);
        expect(scope.state).toBe(false);
    }));

    it('should hide modal on "modal-header-close" button click', inject(function ($rootScope, $timeout) {
        //given
        scope.fullscreen = true;
        scope.state = true;
        scope.closeButton = true;
        var element = createElement(scope);
        $timeout.flush();
        expect($rootScope.$apply.calls.count()).toBe(1);

        //when
        element.find('.modal-header-close').click();
        $timeout.flush();

        //then
        expect($rootScope.$apply.calls.count()).toBe(2);
        expect(scope.state).toBe(false);
    }));
    
    it('should not hide modal on "modal-inner" div click', inject(function ($rootScope, $timeout) {
        //given
        scope.fullscreen = false;
        scope.state = true;
        scope.closeButton = true;
        var element = createElement(scope);
        $timeout.flush();

        //when
        element.find('.modal-inner').click();

        //then
        expect(scope.state).toBe(true);
    }));

    it('should hide on esc keydown', inject(function ($rootScope, $timeout) {
        //given
        scope.fullscreen = false;
        scope.state = true;
        scope.closeButton = true;
        var element = createElement(scope);
        $timeout.flush();

        var event = angular.element.Event('keydown');
        event.keyCode = 27;

        //when
        element.find('.modal-inner').trigger(event);
        $timeout.flush();

        //then
        expect(scope.state).toBe(false);
    }));

    it('should not hide on not esc keydown', inject(function ($rootScope, $timeout) {
        //given
        scope.fullscreen = false;
        scope.state = true;
        scope.closeButton = true;
        var element = createElement(scope);
        $timeout.flush();

        var event = angular.element.Event('keydown');
        event.keyCode = 97;

        //when
        element.find('.modal-inner').trigger(event);
        try{
            $timeout.flush();
        }
        //then
        catch(error) {
            expect(scope.state).toBe(true);
            return;
        }

        //otherwise
        throw new Error('should have thrown error because no timeout is pending');
    }));

    it('should focus on "modal-inner" on module open', function () {
        //given
        scope.fullscreen = false;
        scope.state = false;
        scope.closeButton = false;
        var element = createElement(scope);

        var body = angular.element('body');
        body.append(element);
        expect(document.activeElement).not.toBe(element);

        //when
        scope.state = true;
        scope.$digest();

        //then
        expect(document.activeElement.className).toBe('modal-inner');
    });
});