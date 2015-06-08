(function () {
    'use strict';

    /**
     * @ngdoc directive
     * @name talend.widget.directive:TalendButtonDropdown
     * @description This directive create a split button dropdown element.<br/>
     * Key action :
     * <ul>
     *     <li>ESC : close the dropdown</li>
     * </ul>
     * @restrict EA
     * @usage
     <talend-button-dropdown close-on-select="false" on-open="onOpen()">
      <div class="button-dropdown-container">
           <div class="button-dropdown-action">
               <div class="button-dropdown-button">{{ column.id }}</div>
               <div>{{ column.type }}</div>
           </div>
           <ul class="button-dropdown-menu">
               <li><a href="#">Hide Column {{ column.id | uppercase }}</a></li>
               <li class="divider"></li>
               <li<a href="#">Split first Space</a></li>
               <li><a href="#">Uppercase</a></li>
           </ul>
      </div>
      </talend-button-dropdown>
     * @param {boolean} closeOnSelect Default `true`. If set to false, dropdown will not close on inner item click
     * @param {function} onOpen The callback to execute on dropdown open
     * @param {class} dropdown-action Action zone that trigger menu toggle
     * @param {class} dropdown-button Add a caret at the end off element
     * @param {class} dropdown-menu The menu to open
     * @param {class} divider `dropdown-menu > li.divider` : menu items divider
     */
    function TalendButtonDropdown($window) {
        return {
            restrict: 'EA',
            transclude: true,
            templateUrl: 'components/widgets/button-dropdown/button-dropdown.html',
            scope: {
                closeOnSelect: '=',
                onOpen: '&'
            },
            bindToController: true,
            controller: function() {},
            controllerAs: 'ctrl',
            link: {
                post: function (scope, iElement, iAttrs, ctrl) {
                    var body = angular.element('body');
                    var windowElement = angular.element($window);
                    var container = iElement.find('.dropdown-container');
                    var action = iElement.find('.dropdown-button');
                    var menu = iElement.find('.dropdown-menu');

                    /**
                     * Set the focus on a specific element
                     * @param element - the element to focus
                     */
                    var setFocusOn = function(element) {
                        setTimeout(function() {
                            element.focus();
                        }, 100);
                    };

                    /**
                     * Hide every dropdown in the page
                     */
                    var hideAllDropDowns = function () {
                        angular.element('.dropdown-menu').removeClass('show-menu');
                    };

                    /**
                     * Hide current dropdown menu
                     */
                    var hideMenu = function () {
                        menu.removeClass('show-menu');
                        windowElement.off('scroll', positionMenu);
                    };

                    /**
                     * Show current dropdown menu and set focus on it
                     */
                    var showMenu = function() {
                        menu.addClass('show-menu');
                        positionMenu();
                        ctrl.onOpen();
                        windowElement.on('scroll', positionMenu);

                        setFocusOn(menu);
                    };

                    /**
                     * Move the menu to the right place, depending on the window width and the dropdown position
                     */
                    var positionMenu = function() {
                        var position = container.length ? container[0].getBoundingClientRect() : action[0].getBoundingClientRect();
                        menu.css('top', position.bottom + 5);
                        menu.css('left', position.left);
                        menu.css('right', 'auto');
                        menu.removeClass('right');
                        var menuPosition = menu[0].getBoundingClientRect();
                        if(menuPosition.right > $window.innerWidth) {
                            var right = $window.innerWidth - position.right;
                            menu.css('left', 'auto');
                            menu.css('right', right > 0 ? right : 0);
                            menu.addClass('right');
                        }
                    };

                    //Click : Show/focus or hide menu on action zone click
                    action.click(function () {
                        var isVisible = menu.hasClass('show-menu');
                        hideAllDropDowns();

                        if (isVisible) {
                            hideMenu();
                        }
                        else {
                            showMenu();
                        }
                    });

                    //Click : hide menu on item select if 'closeOnSelect' is not false
                    menu.click(function (event) {
                        event.stopPropagation();
                        if (ctrl.closeOnSelect !== false) {
                            hideMenu();
                        }
                    });

                    //Mousedown : stop propagation not to hide dropdown
                    menu.mousedown(function(event) {
                        event.stopPropagation();
                    });

                    //ESC keydown : hide menu, set focus on dropdown action and stop propagation
                    menu.keydown(function(event) {
                        if(event.keyCode === 27) {
                            hideMenu();
                            event.stopPropagation();
                            setFocusOn(action);
                        }
                    });

                    //make action and menu focusable
                    action.attr('tabindex', '1');
                    menu.attr('tabindex', '2');

                    //hide menu on body mousedown
                    body.mousedown(hideMenu);

                    //on element destroy, we destroy the scope which unregister body mousedown and window scroll handlers
                    iElement.on('$destroy', function () {
                        scope.$destroy();
                    });
                    scope.$on('$destroy', function() {
                        body.off('mousedown', hideMenu);
                        windowElement.off('scroll', positionMenu);
                    });
                }
            }
        };
    }

    angular.module('talend.widget')
        .directive('talendButtonDropdown', TalendButtonDropdown);
})();
