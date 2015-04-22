(function() {
    'use strict';

    /**
     * @ngdoc controller
     * @name talend.widget.controller:TalendConfirmCtrl
     * @description Talend confirm controller.<br/>
     * Watchers :
     * <ul>
     *     <li>On modal hide, the controller reject the confirm promise if the valid button is not clicked</li>
     * </ul>
     * @requires talend.widget.service:TalendConfirmService
     */
    function TalendConfirmCtrl($scope, TalendConfirmService) {
        var vm = this;

        /**
         * @ngdoc property
         * @name modalState
         * @propertyOf talend.widget.controller:TalendConfirmCtrl
         * @description Flag that controls the display of the confirm modal
         */
        vm.modalState = true;

        /**
         * @ngdoc property
         * @name buttonClicked
         * @propertyOf talend.widget.controller:TalendConfirmCtrl
         * @description Flag that keep the state of the validation button click
         */
        vm.buttonClicked = false;

        /**
         * Watch the modal state to reject confirm promise on modal dismiss
         */
        $scope.$watch(
            function() {
                return vm.modalState;
            },
            function(newValue) {
                if(! newValue && ! vm.buttonClicked) {
                    TalendConfirmService.reject('dismiss');
                }
            }
        );

        /**
         * @ngdoc method
         * @name valid
         * @methodOf talend.widget.controller:TalendConfirmCtrl
         * @description Validate the confirm choice and resolve the confirm promise
         */
        vm.valid = function() {
            vm.buttonClicked = true;
            TalendConfirmService.resolve();
        };

        /**
         * @ngdoc method
         * @name cancel
         * @methodOf talend.widget.controller:TalendConfirmCtrl
         * @description Cancel the confirm choice and reject the confirm promise
         */
        vm.cancel = function() {
            vm.buttonClicked = true;
            TalendConfirmService.reject(null);
        };
    }

    angular.module('talend.widget')
        .controller('TalendConfirmCtrl', TalendConfirmCtrl);
})();