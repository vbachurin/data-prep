describe('Confirm widget service', function() {
    'use strict';

    beforeEach(module('talend.widget'));
    beforeEach(module('htmlTemplates'));

    it('should create scope and confirm element', inject(function(TalendConfirmService) {
        //given
        var text1 = 'text 1';
        var text2 = 'text 2';
        var body = angular.element('body');
        expect(TalendConfirmService.modalScope).toBeFalsy();
        expect(TalendConfirmService.element).toBeFalsy();
        expect(body.has('talend-confirm').length).toBe(0);

        //when
        TalendConfirmService.confirm(text1, text2);

        //then
        expect(TalendConfirmService.modalScope).toBeTruthy();
        expect(TalendConfirmService.modalScope.texts).toEqual([text1, text2]);
        expect(TalendConfirmService.element).toBeTruthy();
        expect(TalendConfirmService.element.scope()).toBe(TalendConfirmService.modalScope);

        expect(body.has('talend-confirm').length).toBe(1);
    }));

    describe('with existing confirm', function() {
        var promise, element, scope;

        beforeEach(inject(function(TalendConfirmService) {
            promise = TalendConfirmService.confirm();
            scope = TalendConfirmService.modalScope;
            element = TalendConfirmService.element;

            spyOn(element, 'remove').and.callThrough();
        }));

        it('should throw error on confirm create but another confirm modal is already created', inject(function(TalendConfirmService) {
            //when
            try {
                TalendConfirmService.confirm();
            }

                //then
            catch(error) {
                expect(error.message).toBe('A confirm popup is already created');
                return;
            }
            throw Error('should have thrown error on second confirm() call');
        }));

        it('should resolve promise and remove/destroy scope and element', inject(function($timeout, TalendConfirmService) {
            //given
            var resolved = false;
            promise.then(function() {
                resolved = true;
            });

            var scopeDestroyed = false;
            scope.$on('$destroy', function() {
                scopeDestroyed = true;
            });

            //when
            TalendConfirmService.resolve();
            $timeout.flush();

            //then
            expect(resolved).toBe(true);
            expect(scopeDestroyed).toBe(true);
            expect(element.remove).toHaveBeenCalled();
            expect(TalendConfirmService.modalScope).toBeFalsy();
            expect(TalendConfirmService.element).toBeFalsy();
        }));

        it('should reject promise and remove/destroy scope and element', inject(function($timeout, TalendConfirmService) {
            //given
            var cause = false;
            promise.catch(function(error) {
                cause = error;
            });

            var scopeDestroyed = false;
            scope.$on('$destroy', function() {
                scopeDestroyed = true;
            });

            //when
            TalendConfirmService.reject('dismiss');
            $timeout.flush();

            //then
            expect(cause).toBe('dismiss');
            expect(scopeDestroyed).toBe(true);
            expect(element.remove).toHaveBeenCalled();
            expect(TalendConfirmService.modalScope).toBeFalsy();
            expect(TalendConfirmService.element).toBeFalsy();
        }));
    });
});