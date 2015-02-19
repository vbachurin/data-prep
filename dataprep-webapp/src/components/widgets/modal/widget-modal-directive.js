(function () {
    'use strict';

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
    function TalendModal($rootScope, $timeout) {
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
            controller: function ($scope) {
                var vm = this;

                //hide modal
                vm.hide = function () {
                    vm.state = false;
                };

                //enable/disable scoll on main body depending on modal display
                $scope.$watch(function() {return vm.state;}, function(newValue) {
                    if (newValue) {
                        angular.element('body').addClass('modal-open');
                    } else {
                        angular.element('body').removeClass('modal-open');
                    }
                });
            },
            link: {
                post: function (scope, iElement, iAttrs, ctrl) {
                    var safeDigest = function() {
                        if(! $rootScope.$$phase) {
                            $rootScope.$apply();
                        }
                    };

                    $timeout(function() {
                        // Close action on all 'talend-modal-close' elements
                        iElement.find('.talend-modal-close').on('click', function() {
                            ctrl.hide();
                            safeDigest();
                        });

                        // stop propagation on click on inner modal to prevent modal close
                        iElement.find('.modal-inner').on('click', function (e) {
                            e.stopPropagation();
                        });

                        // attach element to body directly to avoid parent styling
                        iElement.detach();
                        angular.element('body').append(iElement);
                    });
                }
            }
        };
    }

    angular.module('talend.widget')
        .directive('talendModal', TalendModal);
})();
