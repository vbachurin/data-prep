(function() {
    'use strict';

    var state = {};

    /**
     * @ngdoc service
     * @name data-prep.services.state.service:StateService
     * @description Entry level for State services
     */
    function StateService(PlaygroundStateService, playgroundState, //
                          DatasetStateService, datasetState, //
                          FolderStateService, folderState,  //
                          EasterEggsStateService, easterEggsState,FeedbackStateService, feedbackState) {
        state.playground = playgroundState;
        state.dataset = datasetState;
        state.folder = folderState;
        state.easterEggsState = easterEggsState;
        state.feedbackState = feedbackState;

        return {
            //playground
            hidePlayground: PlaygroundStateService.hide,
            showPlayground: PlaygroundStateService.show,
            resetPlayground: PlaygroundStateService.reset,
            setCurrentDataset: PlaygroundStateService.setDataset,
            setCurrentData: PlaygroundStateService.setData,
            setCurrentLookupData: PlaygroundStateService.setLookupData,
            setCurrentPreparation: PlaygroundStateService.setPreparation,
            setNameEditionMode: PlaygroundStateService.setNameEditionMode,
            updateColumnsStatistics: PlaygroundStateService.updateColumnsStatistics,

            //playground - recipe
            hideRecipe: PlaygroundStateService.hideRecipe,
            showRecipe: PlaygroundStateService.showRecipe,

            //playground - grid
            setColumnFocus: PlaygroundStateService.setColumnFocus,
            setGridSelection: PlaygroundStateService.setGridSelection,

            //playground - lookup
            setLookupActions: PlaygroundStateService.setLookupActions,
            setLookupDataset: PlaygroundStateService.setLookupDataset,
            setLookupSelectedColumn: PlaygroundStateService.setLookupSelectedColumn,
            setLookupVisibility: PlaygroundStateService.setLookupVisibility,
            updateLookupColumnsToAdd: PlaygroundStateService.updateLookupColumnsToAdd,

            //playground - filters
            addGridFilter: PlaygroundStateService.addGridFilter,
            removeGridFilter: PlaygroundStateService.removeGridFilter,
            removeAllGridFilters: PlaygroundStateService.removeAllGridFilters,
            updateGridFilter: PlaygroundStateService.updateGridFilter,

            //playground - Suggestions
            setColumnTransformations: PlaygroundStateService.setColumnTransformations,
            setLineTransformations: PlaygroundStateService.setLineTransformations,
            setSuggestionsLoading: PlaygroundStateService.setSuggestionsLoading,

            //dataset
            startUploadingDataset: DatasetStateService.startUploadingDataset,
            finishUploadingDataset: DatasetStateService.finishUploadingDataset,

            //folder
            setCurrentFolder: FolderStateService.setCurrentFolder,
            setCurrentFolderContent: FolderStateService.setCurrentFolderContent,
            setFoldersStack: FolderStateService.setFoldersStack,
            setMenuChildren: FolderStateService.setMenuChildren,

            //easter eggs
            enableEasterEgg: EasterEggsStateService.enableEasterEgg,
            disableEasterEgg: EasterEggsStateService.disableEasterEgg,

            //easter eggs
            enableFeedback: FeedbackStateService.enableFeedback,
            disableFeedback: FeedbackStateService.disableFeedback
        };
    }

    angular.module('data-prep.services.state')
        .service('StateService', StateService)
        .constant('state', state);
})();