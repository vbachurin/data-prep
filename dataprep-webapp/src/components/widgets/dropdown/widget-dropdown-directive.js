(function () {
    'use strict';

    /**
     * Dropdown directive
     *
     * Example :
     * <talend-dropdown close-on-select="false" on-open="onOpen()">
     * <div class="dropdown-container grid-header">
     *      <div class="dropdown-action">
     *          <div class="grid-header-title dropdown-button">{{ column.id }}</div>
     *          <div class="grid-header-type">{{ column.type }}</div>
     *      </div>
     *      <ul class="dropdown-menu">
     *          <li role="presentation"><a role="menuitem" href="#">Hide Column {{ column.id | uppercase }}</a></li>
     *          <li class="divider"></li>
     *          <li role="presentation"><a role="menuitem"href="#">Split first Space</a></li>
     *          <li role="presentation"><a role="menuitem" href="#">Uppercase</a></li>
     *      </ul>
     * </div>
     * </talend-dropdown>
     *
     * Attribute close-on-select : default true. If set to false, dropdown will not close on inner item click
     * Attribute on-open : function to execute on dropdown open
     * Class 'dropdown-action' : action zone that trigger menu toggle
     * Class 'dropdown-button' : add a caret at the end off element
     * Class 'dropdown-menu' : menu
     * Class 'dorpdown-menu > li' : menu items
     * Class 'dorpdown-menu > li.divider' : menu items divider
     *
     * @returns directive
     */
    function TalendDropdown($window) {
        return {
            restrict: 'EA',
            transclude: true,
            templateUrl: 'components/widgets/dropdown/dropdown.html',
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
                    var action = iElement.find('.dropdown-action');
                    var menu = iElement.find('.dropdown-menu');

                    var hideAllDropDowns = function () {
                        angular.element('.dropdown-menu').removeClass('show-menu');
                    };

                    var hideMenu = function () {
                        menu.removeClass('show-menu');
                        windowElement.off('scroll', positionMenu);
                    };

                    var showMenu = function() {
                        menu.addClass('show-menu');
                        positionMenu();
                        ctrl.onOpen();
                        windowElement.on('scroll', positionMenu);
                    };

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

                    // Show or hide menu on action zone click
                    iElement.find('.dropdown-action').on('click', function (event) {
                        event.stopPropagation();

                        var hasClass = menu.hasClass('show-menu');
                        hideAllDropDowns();

                        if (hasClass) {
                            hideMenu();
                        }
                        else {
                            showMenu();
                        }
                    });

                    //hide menu on menu item select if 'closeOnSelect' is not false
                    iElement.find('.dropdown-menu').click(function (event) {
                        event.stopPropagation();
                        if (ctrl.closeOnSelect !== false) {
                            hideMenu();
                        }
                    });

                    //stop propagation on element mousedown not to hide dropdown
                    iElement.mousedown(function(event) {
                        event.stopPropagation();
                    });

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
        .directive('talendDropdown', TalendDropdown);
})();
