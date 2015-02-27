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
            link: {
                post: function (scope, iElement) {
                    var menu = iElement.find('.dropdown-menu');

                    var hideAllDropDowns = function () {
                        angular.element('.dropdown-menu').removeClass('show-menu');
                    };

                    var hideMenu = function () {
                        menu.removeClass('show-menu');
                    };

                    var showMenu = function() {
                        menu.addClass('show-menu');
                        if(! menu.hasClass('right')) {
                            var position = menu[0].getBoundingClientRect();
                            if(position.right > $window.innerWidth) {
                                menu.addClass('right');
                            }
                        }
                        scope.onOpen();
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
                        if (scope.closeOnSelect !== false) {
                            hideMenu();
                        }
                    });

                    //hide menu on body click
                    angular.element('body').click(hideMenu);
                }
            }
        };
    }

    angular.module('talend.widget')
        .directive('talendDropdown', TalendDropdown);
})();
