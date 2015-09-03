(function() {
    'use strict';

    var state = {};

    function StateService(PlaygroundStateService, playgroundState) {
        state.playground = playgroundState;

        return {
            //playground
            hidePlayground: PlaygroundStateService.hide,
            showPlayground: PlaygroundStateService.show,
            setCurrentDataset: PlaygroundStateService.setDataset,
            setCurrentPreparation: PlaygroundStateService.setPreparation,
            setNameEditionMode: PlaygroundStateService.setNameEditionMode,
            resetPlayground: PlaygroundStateService.reset,

            //playground - recipe
            showRecipe: PlaygroundStateService.showRecipe,
            hideRecipe: PlaygroundStateService.hideRecipe,

            //playground - grid
            setGridSelection: PlaygroundStateService.setGridSelection
        };
    }

    angular.module('data-prep.services.state')
        .service('StateService', StateService)
        .constant('state', state);
})();