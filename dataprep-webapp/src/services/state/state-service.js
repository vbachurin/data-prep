(function() {
    'use strict';

    var state = {};

    function StateService(PlaygroundStateService, playgroundState, DatasetStateService, datasetState) {
        state.playground = playgroundState;
        state.dataset = datasetState;

        return {
            //playground
            hidePlayground: PlaygroundStateService.hide,
            resetPlayground: PlaygroundStateService.reset,
            showPlayground: PlaygroundStateService.show,
            setCurrentDataset: PlaygroundStateService.setDataset,
            setCurrentData: PlaygroundStateService.setData,
            setCurrentPreparation: PlaygroundStateService.setPreparation,
            setNameEditionMode: PlaygroundStateService.setNameEditionMode,

            //playground - recipe
            showRecipe: PlaygroundStateService.showRecipe,
            hideRecipe: PlaygroundStateService.hideRecipe,

            //playground - grid
            setGridSelection: PlaygroundStateService.setGridSelection,
            setLookupVisibility: PlaygroundStateService.setLookupVisibility,
            updateShownLinesLength: PlaygroundStateService.updateShownLinesLength,
            setDataView: PlaygroundStateService.setDataView,

            //playground - filters
            addGridFilter: PlaygroundStateService.addGridFilter,
            updateGridFilter: PlaygroundStateService.updateGridFilter,
            removeGridFilter: PlaygroundStateService.removeGridFilter,
            removeAllGridFilters: PlaygroundStateService.removeAllGridFilters,
            
            //dataset
            startUploadingDataset: DatasetStateService.startUploadingDataset,
            finishUploadingDataset: DatasetStateService.finishUploadingDataset
        };
    }

    angular.module('data-prep.services.state')
        .service('StateService', StateService)
        .constant('state', state);
})();