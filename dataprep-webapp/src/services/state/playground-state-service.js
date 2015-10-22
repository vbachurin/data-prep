(function() {
    'use strict';

    var playgroundState = {};

    function PlaygroundStateService(RecipeStateService, recipeState, FilterStateService, filterState) {
        playgroundState.recipe = recipeState;
        playgroundState.filter = filterState;

        return {
            //playground
            show: show,
            hide: hide,
            setDataset: setDataset,
            setPreparation: setPreparation,
            setNameEditionMode: setNameEditionMode,
            reset: reset,
            setData: setData,

            //recipe
            showRecipe: RecipeStateService.show,
            hideRecipe: RecipeStateService.hide,

            //datagrid
            setGridSelection: setGridSelection,
            setLookupVisibility: setLookupVisibility,
            updateShownLinesLength: updateShownLinesLength,
            setDataView: setDataView,

            //filters
            addGridFilter: FilterStateService.addGridFilter,
            updateGridFilter: FilterStateService.updateGridFilter,
            removeGridFilter: FilterStateService.removeGridFilter,
            removeAllGridFilters: FilterStateService.removeAllGridFilters
        };

        //--------------------------------------------------------------------------------------------------------------
        //-----------------------------------------------------GRID-----------------------------------------------------
        //--------------------------------------------------------------------------------------------------------------
        function setGridSelection(column, line) {
            playgroundState.column = column;
            playgroundState.line = line;
        }

        function setLookupVisibility(visibility) {
            playgroundState.lookupVisibility = visibility;
        }

        //--------------------------------------------------------------------------------------------------------------
        //--------------------------------------------------PLAYGROUND--------------------------------------------------
        //--------------------------------------------------------------------------------------------------------------
        function setDataset(dataset) {
            playgroundState.dataset = dataset;
        }

        function setData(data) {
            playgroundState.data = data;
            playgroundState.dataView.beginUpdate();
            playgroundState.dataView.setItems(data.records, 'tdpId');
            playgroundState.dataView.endUpdate();
            playgroundState.allLinesLength = playgroundState.dataView.getItems().length;
            //When we change the sample size
            updateShownLinesLength();
        }

        function setDataView(dataView) {
            playgroundState.dataView = dataView;
        }

        function updateShownLinesLength() {
            playgroundState.shownLinesLength = playgroundState.dataView ? playgroundState.dataView.getLength() : 0;
        }

        function setPreparation(preparation) {
            playgroundState.preparation = preparation;
        }

        function setNameEditionMode(editionMode) {
            playgroundState.nameEditionMode = editionMode;
        }

        function show() {
            playgroundState.visible = true;
        }

        function hide() {
            playgroundState.visible = false;
        }

        function reset() {
            playgroundState.column = null;
            playgroundState.line = null;
            playgroundState.data = null;
            playgroundState.dataset = null;
            playgroundState.preparation = null;
            playgroundState.nameEditionMode = false;
            playgroundState.lookupVisibility = false;

            FilterStateService.reset();
        }
    }

    angular.module('data-prep.services.state')
        .service('PlaygroundStateService', PlaygroundStateService)
        .constant('playgroundState', playgroundState);
})();