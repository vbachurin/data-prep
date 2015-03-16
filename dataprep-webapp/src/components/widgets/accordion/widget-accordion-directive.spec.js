describe('Accordion directive', function () {
    'use strict';

    var scope, createElement;

    var clickOnTriggerElement = function(element, index) {
        element.find('.talend-accordion-trigger').eq(index).click();
    };

    var expectElementToBeVisible = function(element) {
        expect(element.css('display')).toBe('block');
    };
    var expectElementToBeHidden = function(element) {
        expect(element.css('display')).not.toBe(true);
    };

    beforeEach(module('talend.widget'));
    beforeEach(module('htmlTemplates'));

    afterEach(function () {
        scope.$destroy();
        scope.$digest();
    });

    describe('without open callback', function() {

        beforeEach(inject(function ($rootScope, $compile) {
            scope = $rootScope.$new();

            createElement = function () {
                var template = '<ul class="talend-accordion">' +
                    '   <li>Uppercase on column LASTNAME</li>' +
                    '   <li>' +
                    '      <a class="talend-accordion-trigger">2. Replace empty in column STATE</a>' +
                    '       <ul class="submenu">' +
                    '           <li>' +
                    '               Value : <input type="text" value="--" />' +
                    '              <button class="t-btn-primary">Ok</button>' +
                    '           </li>' +
                    '      </ul>' +
                    '   </li>' +
                    '   <li>' +
                    '      <a class="talend-accordion-trigger">2bis. Replace empty in column STATE</a>' +
                    '       <ul class="submenu">' +
                    '           <li>' +
                    '               Value : <input type="text" value="N/A" />' +
                    '              <button class="t-btn-primary">Ok</button>' +
                    '           </li>' +
                    '      </ul>' +
                    '   </li>' +
                    '   <li>Lowercase on column Firstname</li>' +
                    '</ul>';
                var element = $compile(template)(scope);
                scope.$digest();
                return element;
            };
        }));


        it('should open the second sub-menu on click on the corresponding trigger', function () {
            //given
            var element = createElement(scope);
            var firstSubmenu = element.find('.submenu').eq(0);
            var secondSubmenu = element.find('.submenu').eq(1);
            expectElementToBeHidden(firstSubmenu);
            expectElementToBeHidden(secondSubmenu);

            //when
            clickOnTriggerElement(element, 1);

            //then
            expectElementToBeHidden(firstSubmenu);
            expectElementToBeVisible(secondSubmenu);
        });

        it('should close the second sub-menu on click on the corresponding trigger', function () {
            //given
            var element = createElement(scope);
            var firstSubmenu = element.find('.submenu').eq(0);
            var secondSubmenu = element.find('.submenu').eq(1);

            clickOnTriggerElement(element, 1);
            expectElementToBeHidden(firstSubmenu);
            expectElementToBeVisible(secondSubmenu);

            //when
            clickOnTriggerElement(element, 1);

            //then
            expectElementToBeHidden(firstSubmenu);
            expectElementToBeHidden(secondSubmenu);
        });

        it('should close the other sub-menus on click on a trigger', function () {
            //given
            var element = createElement(scope);
            var firstSubmenu = element.find('.submenu').eq(0);
            var secondSubmenu = element.find('.submenu').eq(1);

            clickOnTriggerElement(element, 1);
            expectElementToBeHidden(firstSubmenu);
            expectElementToBeVisible(secondSubmenu);

            //when
            clickOnTriggerElement(element, 0);

            //then
            expectElementToBeVisible(firstSubmenu);
            expectElementToBeHidden(secondSubmenu);
        });
    });

    describe('with open callback', function() {
        beforeEach(inject(function ($rootScope, $compile) {
            scope = $rootScope.$new();
            scope.onOpen = function() {};

            createElement = function () {
                var template = '<ul class="talend-accordion">' +
                    '   <li>Uppercase on column LASTNAME</li>' +
                    '   <li>' +
                    '      <a class="talend-accordion-trigger" on-open="onOpen()">2. Replace empty in column STATE</a>' +
                    '       <ul class="submenu">' +
                    '           <li>' +
                    '               Value : <input type="text" value="--" />' +
                    '              <button class="t-btn-primary">Ok</button>' +
                    '           </li>' +
                    '      </ul>' +
                    '   </li>' +
                    '</ul>';
                var element = $compile(template)(scope);
                scope.$digest();
                return element;
            };

            spyOn(scope, 'onOpen').and.returnValue(true);
        }));


        it('should open the second sub-menu on click on the corresponding trigger', inject(function($timeout) {
            //given
            var element = createElement(scope);

            //when
            clickOnTriggerElement(element, 0);
            $timeout.flush();

            //then
            expect(scope.onOpen).toHaveBeenCalled();
        }));
    });
});