(function() {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.preparation-list.controller:PreparationListCtrl
     * @description Preparation list controller.
     * On creation, it fetch the user's preparations and load the requested one if a `prepid` is present as query param
     * @requires data-prep.services.preparation.service:PreparationService
     * @requires data-prep.services.playground.service:PlaygroundService
     * @requires data-prep.services.utils.service:MessageService
     * @requires data-prep.services.state.service:StateService
     * @requires talend.widget.service:TalendConfirmService
     */
    function PreparationListCtrl($rootScope, $stateParams, PlaygroundService, PreparationService, TalendConfirmService, MessageService, StateService) {
        var vm = this;
        vm.preparationService = PreparationService;

        /**
         * @ngdoc method
         * @name load
         * @methodOf data-prep.preparation-list.controller:PreparationListCtrl
         * @param {object} preparation - the preparation to load
         * @description Load a preparation in the playground
         */
        vm.load = function(preparation) {
            PlaygroundService
                .load(preparation)
                .then(StateService.showPlayground);
        };

        /**
         * @ngdoc method
         * @name delete
         * @methodOf data-prep.preparation-list.controller:PreparationListCtrl
         * @param {object} preparation - the preparation to delete
         * @description Delete a preparation
         */
        vm.delete = function(preparation) {
            TalendConfirmService.confirm({disableEnter: true}, ['DELETE_PERMANENTLY', 'NO_UNDONE_CONFIRM'], {type:'preparation', name: preparation.name})
                .then(function() {
                    return PreparationService.delete(preparation);
                })
                .then(function() {
                    MessageService.success('REMOVE_SUCCESS_TITLE', 'REMOVE_SUCCESS', {type:'preparation', name: preparation.name});
                });
        };

        /**
         * @ngdoc method
         * @name showRenameInput
         * @methodOf data-prep.preparation-list.controller:PreparationListCtrl
         * @param {object} preparation - the preparation to show rename input
         * @description change a flag value to trigger input display
         */
        vm.showRenameInput = function(preparation){
          preparation.originalName = preparation.name;
          preparation.showChangeName = true;
        };

        /**
         * @ngdoc method
         * @name cancelRename
         * @methodOf data-prep.preparation-list.controller:PreparationListCtrl
         * @param {object} preparation - the preparation to cancel rename input
         * @description change back a flag value to trigger input display and restore previous name
         */
        vm.cancelRename = function(preparation){
            preparation.name = preparation.originalName;
            preparation.showChangeName = false;
        };

        /**
         * @ngdoc method
         * @name rename
         * @methodOf data-prep.preparation-list.controller:PreparationListCtrl
         * @param {object} preparation - the preparation to rename
         * @description trigger backend call to update preparation name
         */
        vm.rename = function(preparation){
            // do not trigger backend call if the name didn't change
            if (preparation.name === preparation.originalName){
                preparation.showChangeName = false;
                return;
            }
            $rootScope.$emit('talend.loading.start');
            return PreparationService.setName(preparation.id, preparation.name)
                .then(function(){
                  preparation.showChangeName = false;
                })
                .then(function() {
                  MessageService.success('RENAME_SUCCESS_TITLE', 'RENAME_SUCCESS');
                })
                //hide loading screen
                .finally(function () {
                  $rootScope.$emit('talend.loading.stop');
                });
        };

        vm.clone = function(preparation){
          console.log('clone:');
        };

        vm.overPreparationEntry = function ( preparation ) {
            angular.element('#edit_btn_'+preparation.id).show();
            angular.element('#clone_btn_'+preparation.id).show();
        };

        vm.leavePreparationEntry = function ( preparation ) {
            angular.element('#edit_btn_'+preparation.id).hide();
            angular.element('#clone_btn_'+preparation.id).hide();
        };

        /**
         * @ngdoc method
         * @name loadUrlSelectedPreparation
         * @methodOf data-prep.preparation-list.controller:PreparationListCtrl
         * @param {object} preparations - list of all user's preparation
         * @description [PRIVATE] Load playground with provided preparation id, if present in route param
         */
        var loadUrlSelectedPreparation = function(preparations) {
            if($stateParams.prepid) {
                var selectedPrep = _.find(preparations, function(preparation) {
                    return preparation.id === $stateParams.prepid;
                });
                
                if(selectedPrep) {
                    vm.load(selectedPrep);
                }
                else {
                    MessageService.error('PLAYGROUND_FILE_NOT_FOUND_TITLE', 'PLAYGROUND_FILE_NOT_FOUND', {type: 'preparation'});
                }
            }
        };

        /**
         * Load preparations list if needed, and load playground if route has preparation id
         */
        PreparationService.getPreparations()
            .then(loadUrlSelectedPreparation);
    }

    /**
     * @ngdoc property
     * @name preparations
     * @propertyOf data-prep.preparation-list.controller:PreparationListCtrl
     * @description The preparations list.
     * It is bound to {@link data-prep.services.preparation.service:PreparationService PreparationService}.preparationsList()
     */
    Object.defineProperty(PreparationListCtrl.prototype,
        'preparations', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.preparationService.preparationsList();
            }
        });

    angular.module('data-prep.preparation-list')
        .controller('PreparationListCtrl', PreparationListCtrl);
})();