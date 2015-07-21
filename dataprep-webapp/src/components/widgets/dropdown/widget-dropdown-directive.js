(function () {
    'use strict';

    /**
     * @ngdoc directive
     * @name talend.widget.directive:TalendDropdown
     * @description This directive create a dropdown element.<br/>
     * Key action :
     * <ul>
     *     <li>ESC : close the dropdown</li>
     * </ul>
     * @restrict EA
     * @usage
     <talend-dropdown close-on-select="false" on-open="onOpen()">
      <div class="dropdown-container">
           <div class="dropdown-action">
               <div class="dropdown-button">{{ column.id }}</div>
               <div>{{ column.type }}</div>
           </div>
           <ul class="dropdown-menu">
               <li><a href="#">Hide Column {{ column.id | uppercase }}</a></li>
               <li class="divider"></li>
               <li<a href="#">Split first Space</a></li>
               <li><a href="#">Uppercase</a></li>
           </ul>
      </div>
      </talend-dropdown>
     * @param {boolean} closeOnSelect Default `true`. If set to false, dropdown will not close on inner item click
     * @param {function} onOpen The callback to execute on dropdown open
     * @param {class} dropdown-action Action zone that trigger menu toggle
     * @param {class} dropdown-button Add a caret at the end off element
     * @param {class} dropdown-menu The menu to open
     * @param {class} divider `dropdown-menu > li.divider` : menu items divider
     */
    function TalendDropdown($window, PlaygroundService) {
        return {
            restrict: 'EA',
            replace: true,
            transclude: true,
            templateUrl: 'components/widgets/dropdown/dropdown.html',
            scope: {
                closeOnSelect: '=',
                onOpen: '&',
                column: '=',
                menuItems: '='

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

                    //These variables are used to separate click and double click action
                    var DELAY = 300, clicks = 0, timer = null;
                    var RENAME_ACTION = 'rename_column';
                    //Call ctrl.onOpen() to get transformations
                    ctrl.onOpen();
                    var renameTransformation = null;

                    //Transform text to input for rename
                    var updateVal = function (currentEle, value) {

                        action.off('click');

                        $(currentEle).html('<input class="thVal" type="text" value="' + value + '" />');
                        $('.thVal').focus();
                        $('.thVal').select();

                        $('.thVal').keyup(function (event) {
                            if (event.keyCode === 13) {

                                renameTransformation = _.filter(ctrl.menuItems, function(menu) {
                                    return menu.name === RENAME_ACTION;
                                })[0];

                                var params = {};
                                if (renameTransformation.parameters) {
                                    _.forEach(renameTransformation.parameters, function (paramItem) {
                                        paramItem.value = $('.thVal').val().trim();
                                        params[paramItem.name] = paramItem.value;
                                    });
                                }

                                PlaygroundService.appendStep(RENAME_ACTION, ctrl.column, params)
                                    .then(function() {
                                        $(currentEle).html($('.thVal').val().trim());
                                        action.on('click', detectClickAction);
                                    });
                            }
                        });

                        $('.thVal').on('blur', function () {

                            renameTransformation = _.filter(ctrl.menuItems, function(menu) {
                                return menu.name === RENAME_ACTION;
                            })[0];

                            var params = {};
                            if (renameTransformation.parameters) {
                                _.forEach(renameTransformation.parameters, function (paramItem) {
                                    paramItem.value = $('.thVal').val().trim();
                                    params[paramItem.name] = paramItem.value;
                                });
                            }

                            PlaygroundService.appendStep(RENAME_ACTION, ctrl.column, params)
                                .then(function() {
                                    $(currentEle).html($('.thVal').val().trim());
                                    //setTimeout prevent the "click" event from being fired on blur
                                    setTimeout(function() {
                                        action.on('click', detectClickAction);
                                    }, 200);
                                });
                        });
                    };

                    //Click : Show/focus or hide menu on action zone click
                    var singleClickAction = function () {
                        var isVisible = menu.hasClass('show-menu');
                        hideAllDropDowns();
                        if (isVisible) {
                            hideMenu();
                        }
                        else {
                            showMenu();
                        }
                    };

                    //DleClick : Show an input to rename the column
                    var doubleClickAction = function () {

                        var isVisible = menu.hasClass('show-menu');
                        hideAllDropDowns();
                        if (isVisible) {
                            hideMenu();
                        }
                        var currentEle = iElement.find('.grid-header-title');
                        var value = iElement.find('.grid-header-title').html();
                        updateVal(currentEle, value);
                    };

                    //Detect the double click
                    var detectClickAction = function () {
                        clicks++;  //count clicks
                        if(clicks === 1) {
                            timer = setTimeout(function() {
                                singleClickAction();
                                clicks = 0;  //after action performed, reset counter
                            }, DELAY);
                        } else {
                            clearTimeout(timer);  //prevent single-click action
                            doubleClickAction();
                            clicks = 0;  //after action performed, reset counter
                        }
                    };

                    //Bind click and dblclick event to 'action'
                    action
                        .on('click',detectClickAction)
                        .on('mousedown', function(event){
                            event.stopPropagation();  //stopPropagation mousedown of body
                        })
                        .on('dblclick', function(e){
                            e.preventDefault();  //cancel system double-click event
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
        .directive('talendDropdown', TalendDropdown);
})();
