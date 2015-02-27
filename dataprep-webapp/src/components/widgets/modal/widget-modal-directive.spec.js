describe('Dropdown directive', function () {
    'use strict';

    var scope, createElement, createFormElement, createNestedElement, createButtonElement;

    beforeEach(module('talend.widget'));
    beforeEach(module('htmlTemplates'));

    afterEach(function () {
        scope.$destroy();
        scope.$digest();
    });

    beforeEach(inject(function ($rootScope, $compile, $timeout) {
        scope = $rootScope.$new();

        createElement = function (directiveScope) {
            var html = '<talend-modal fullscreen="fullscreen" state="state" close-button="closeButton"></talend-modal>';
            var element = $compile(html)(directiveScope);
            directiveScope.$digest();
            $timeout.flush();
            return element;
        };

        createFormElement = function (directiveScope) {
            var html = '<talend-modal fullscreen="fullscreen" state="state" close-button="closeButton">' +
                '   <input type="text" id="firstInput" />' +
                '   <input type="text" id="secondInput" />' +
                '</talend-modal>';
            var element = $compile(html)(directiveScope);
            directiveScope.$digest();
            $timeout.flush();
            return element;
        };

        createNestedElement = function (directiveScope) {
            var html = '<talend-modal id="outerModal" fullscreen="fullscreen" state="state" close-button="closeButton">' +
                '   <talend-modal id="innerModal" fullscreen="innerfullscreen" state="innerState" close-button="innerCloseButton"></talend-modal>' +
                '</talend-modal>';
            var element = $compile(html)(directiveScope);
            directiveScope.$digest();
            $timeout.flush();
            return element;
        };

        createButtonElement = function (directiveScope) {
            var html = '<talend-modal fullscreen="fullscreen" state="state" close-button="closeButton" disable-enter="disableEnter">' +
                '   <button class="modal-primary-button" ng-click="click()"/>' +
                '</talend-modal>';
            directiveScope.click = function() {
                directiveScope.primaryButtonClicked = true;
            };
            var element = $compile(html)(directiveScope);
            directiveScope.$digest();
            $timeout.flush();
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

        //when
        element.find('.modal-inner').click();
        try {
            $timeout.flush();
        }
        catch (error) {
            $rootScope.$apply();

            //then
            expect(scope.state).toBe(true);
            return;
        }
        throw new Error('Should have thrown error on timeout flush because hide should not be called on click in modal-inner div');


    }));

    it('should attach popup to body', function () {
        //when
        createElement(scope);

        //then
        expect(angular.element('body').find('talend-modal').length).toBe(1);
    });

    it('should remove element on scope destroy', function () {
        //given
        createElement(scope);

        //when
        scope.$destroy();
        scope.$digest();

        //then
        expect(angular.element('body').find('talend-modal').length).toBe(0);
    });

    it('should hide on ESC keydown', inject(function ($rootScope, $timeout) {
        //given
        scope.fullscreen = false;
        scope.state = true;
        scope.closeButton = true;
        var element = createElement(scope);

        var event = angular.element.Event('keydown');
        event.keyCode = 27;

        //when
        element.find('.modal-inner').trigger(event);
        $timeout.flush();

        //then
        expect(scope.state).toBe(false);
    }));

    it('should not hide on not ESC keydown', inject(function ($rootScope, $timeout) {
        //given
        scope.fullscreen = false;
        scope.state = true;
        scope.closeButton = true;
        var element = createElement(scope);

        var event = angular.element.Event('keydown');
        event.keyCode = 97;

        //when
        element.find('.modal-inner').trigger(event);
        try {
            $timeout.flush();
        }
            //then
        catch (error) {
            expect(scope.state).toBe(true);
            return;
        }

        //otherwise
        throw new Error('should have thrown error because no timeout is pending');
    }));

    it('should hit primary button on ENTER keydown', inject(function () {
        //given
        scope.fullscreen = false;
        scope.state = true;
        scope.closeButton = true;
        var element = createButtonElement(scope);

        expect(scope.primaryButtonClicked).toBeFalsy();
        var event = angular.element.Event('keydown');
        event.keyCode = 13;

        //when
        element.find('.modal-inner').trigger(event);
        scope.$digest();

        //then
        expect(scope.primaryButtonClicked).toBe(true);
    }));

    it('should not hit primary button on ENTER keydown when disable-enter attribute is true', inject(function () {
        //given
        scope.fullscreen = false;
        scope.state = true;
        scope.closeButton = true;
        scope.disableEnter = true;
        var element = createButtonElement(scope);

        expect(scope.primaryButtonClicked).toBeFalsy();
        var event = angular.element.Event('keydown');
        event.keyCode = 13;

        //when
        element.find('.modal-inner').trigger(event);
        scope.$digest();

        //then
        expect(scope.primaryButtonClicked).toBeFalsy();
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

        //finally
        element.remove();
    });

    it('should focus on first input on show', function () {
        //given
        scope.fullscreen = false;
        scope.state = false;
        scope.closeButton = false;
        var element = createFormElement(scope);

        var body = angular.element('body');
        body.append(element);
        expect(document.activeElement).not.toBe(element);

        //when
        scope.state = true;
        scope.$digest();

        //then
        expect(document.activeElement.id).toBe('firstInput');

        //finally
        element.remove();
    });

    it('should focus on next last shown modal on focused modal close', function () {
        //given : init
        scope.fullscreen = false;
        scope.state = false;
        scope.closeButton = false;
        scope.innerFullscreen = false;
        scope.innerState = false;
        scope.innerCloseButton = false;
        var element = createNestedElement(scope);

        var body = angular.element('body');
        body.append(element);
        expect(document.activeElement).not.toBe(element);

        //given : show outer modal
        scope.state = true;
        scope.$digest();
        var outerModal = body.find('#outerModal').eq(0).find('.modal-inner').eq(0)[0];
        expect(document.activeElement).toBe(outerModal);

        //given : show inner modal
        scope.innerState = true;
        scope.$digest();
        var innerModal = body.find('#innerModal').eq(0).find('.modal-inner').eq(0)[0];
        expect(document.activeElement).toBe(innerModal);

        //when
        scope.innerState = false;
        scope.$digest();

        //then
        expect(document.activeElement).toBe(outerModal);

        //finally
        element.remove();
    });
});