(function() {
    'use strict';

    var state = {};

    function StateService(PlaygroundStateService, playgroundState) {
        state.playground = playgroundState;

        return {
            //playground
            hidePlayground: PlaygroundStateService.hide,
            resetPlayground: PlaygroundStateService.reset,
            showPlayground: PlaygroundStateService.show,
            setCurrentDataset: PlaygroundStateService.setDataset,
            setCurrentData: PlaygroundStateService.setData,
            setCurrentPreparation: PlaygroundStateService.setPreparation,
            setNameEditionMode: PlaygroundStateService.setNameEditionMode,
            setSampleSize: PlaygroundStateService.setSampleSize,

            //playground - recipe
            showRecipe: PlaygroundStateService.showRecipe,
            hideRecipe: PlaygroundStateService.hideRecipe,

            //playground - grid
            setGridSelection: PlaygroundStateService.setGridSelection,
            setLookupVisibility: PlaygroundStateService.setLookupVisibility
        };
    }

    angular.module('data-prep.services.state')
        .service('StateService', StateService)
        .constant('state', state);
})();