(function() {
    'use strict';

    var playgroundState = {};

    function PlaygroundStateService(RecipeStateService, recipeState) {
        playgroundState.recipe = recipeState;

        return {
            //playground
            show: show,
            hide: hide,
            setDataset: setDataset,
            setPreparation: setPreparation,
            setNameEditionMode: setNameEditionMode,
            reset: reset,

            //recipe
            showRecipe: RecipeStateService.show,
            hideRecipe: RecipeStateService.hide,

            //datagrid
            setGridSelection: setGridSelection
        };

        //--------------------------------------------------------------------------------------------------------------
        //--------------------------------------------------PLAYGROUND--------------------------------------------------
        //--------------------------------------------------------------------------------------------------------------
        function setGridSelection(column, line) {
            playgroundState.column = column;
            playgroundState.line = line;
        }

        function setDataset(dataset) {
            playgroundState.dataset = dataset;
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
            playgroundState.dataset = null;
            playgroundState.preparation = null;
            playgroundState.nameEditionMode = false;
        }
    }

    angular.module('data-prep.services.state')
        .service('PlaygroundStateService', PlaygroundStateService)
        .constant('playgroundState', playgroundState);
})();