(function() {
    'use strict';

    var playgroundState = {};

    function PlaygroundStateService(RecipeStateService, recipeState) {
        playgroundState.recipe = recipeState;

        return {
            //playground
            setGridSelection: setGridSelection,
            show: show,
            hide: hide,
            setDataset: setDataset,
            setPreparation: setPreparation,
            reset: reset,

            //recipe
            showRecipe: RecipeStateService.show,
            hideRecipe: RecipeStateService.hide
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
        }
    }

    angular.module('data-prep.services.state')
        .service('PlaygroundStateService', PlaygroundStateService)
        .constant('playgroundState', playgroundState);
})();