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

        function show() {
            playgroundState.visible = true;
        }

        function hide() {
            playgroundState.visible = false;
        }
    }

    angular.module('data-prep.services.state')
        .service('PlaygroundStateService', PlaygroundStateService)
        .constant('playgroundState', playgroundState);
})();