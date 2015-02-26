(function () {
    'use strict';

    /**
     * Array of all modal inner element that is visible
     * @type {Array}
     */
    var shownModalsInnerElements = [];

    /**
     * Add an element to the list of visible modals
     * @param innerElement
     */
    var registerShownElement = function(innerElement) {
        shownModalsInnerElements.push(innerElement);
    };

    /**
     * Remove an element from list of visible modals
     * @param innerElement
     */
    var deregisterShownElement = function(innerElement) {
        var index = shownModalsInnerElements.indexOf(innerElement);
        if(index > -1) {
            shownModalsInnerElements = shownModalsInnerElements.slice(0, index);
        }
    };

    /**
     * Return last visible modal (the one the most in front on the screen)
     * @returns {*}
     */
    var getLastRegisteredInnerElement = function() {
        if(shownModalsInnerElements.length) {
            return shownModalsInnerElements[shownModalsInnerElements.length - 1];
        }
    };

    /**
     * Modal window with 2 modes : normal (default) | fullscreen
     *
     * <talend-modal fullscreen="false"
     *              state="homeCtrl.dataModalSmall"
     *              close-button="true">
     *      Modal content
     * </talend-modal>
     *
     * <talend-modal fullscreen="true"
     *              state="homeCtrl.dataModal"
     *              close-button="true">
     *      <div class="modal-header">
     *          <ul>
     *              <li>header 1</li>
     *              <li>header 2</li>
     *          </ul>
     *      </div>
     *
     *      <div class="modal-body">
     *          Body content
     *      </div>
     * </talend-modal>
     *
     * All mode :
     * Element 'talend-modal' > Class 'fullscreen' : false (default)
     * Element 'talend-modal' > Class 'state' : variable binding that represents the state (true = opened, false = closed)
     * Element 'talend-modal' > Class 'close-button' : close button on top right
     * Element 'talend-modal' > Class 'talend-modal-close' : close action on click
     *
     * Fullscreen mode :
     * Element 'modal-header' : header content
     * Element 'modal-header' > ul > li : header item
     * Element 'modal-body' : body content
     *
     * @returns directive
     */
    function TalendModal($timeout) {
        return {
            restrict: 'EA',
            transclude: true,
            templateUrl: 'components/widgets/modal/modal.html',
            scope: {
                state: '=',
                closeButton: '=',
                fullscreen: '='
            },
            bindToController: true,
            controllerAs: 'talendModalCtrl',
            controller: function () {
                var vm = this;

                vm.hide = function () {
                    vm.state = false;
                };
            },
            link: {
                post: function (scope, iElement, iAttrs, ctrl) {
                    var body = angular.element('body');

                    /**
                     * Hide modal action
                     */
                    var hideModal = function() {
                        $timeout(function() {
                            ctrl.hide();
                        });
                    };

                    /**
                     * Deregister modal from list of shown modal and focus on the last shown modal
                     */
                    var deregisterAndFocusOnLastModal = function(innerElement) {
                        deregisterShownElement(innerElement);
                        var mostAdvancedModal = getLastRegisteredInnerElement();
                        if(mostAdvancedModal) {
                            mostAdvancedModal.focus();
                        }
                        else {
                            body.removeClass('modal-open');
                        }
                    };

                    /**
                     * Initialisation
                     */
                    $timeout(function() {
                        var innerElement = iElement.find('.modal-inner');

                        // Close action on all 'talend-modal-close' elements
                        iElement.find('.talend-modal-close').on('click', hideModal);

                        // stop propagation on click on inner modal to prevent modal close
                        innerElement.on('click', function (e) {
                            e.stopPropagation();
                        });

                        // hide modal on escape keydown
                        innerElement.bind('keydown', function (e) {
                            if(e.keyCode === 27) { //escape
                                hideModal();
                            }
                        });

                        // attach element to body directly to avoid parent styling
                        iElement.detach();
                        body.append(iElement);

                        // detach element on destroy
                        scope.$on('$destroy', function() {
                            deregisterAndFocusOnLastModal(innerElement);
                            iElement.remove();
                        });

                        //enable/disable scroll on main body depending on modal display
                        //popup focus on show
                        scope.$watch(function() {return ctrl.state;}, function(newValue) {
                            if (newValue) {
                                //register modal in shown modal list and focus on inner element
                                body.addClass('modal-open');
                                registerShownElement(innerElement);
                                innerElement.focus();

                                //focus on first input (ignore first because it's the state checkbox)
                                var inputs = iElement.find('input');
                                if(inputs.length > 1) {
                                    inputs.eq(1).focus();
                                }
                            } else {
                                deregisterAndFocusOnLastModal(innerElement);
                            }
                        });
                    });
                }
            }
        };
    }

    angular.module('talend.widget')
        .directive('talendModal', TalendModal);
})();
