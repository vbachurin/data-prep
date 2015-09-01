(function() {
    'use strict';

    var state = {};

    function StateService(PlaygroundStateService, playgroundState) {
        state.playground = playgroundState;

        return {
            //playground
            showPlayground: PlaygroundStateService.show,
            hidePlayground: PlaygroundStateService.hide,
            setGridSelection: PlaygroundStateService.setGridSelection,

            //playground - recipe
            showRecipe: PlaygroundStateService.showRecipe,
            hideRecipe: PlaygroundStateService.hideRecipe
        };
    }

    angular.module('data-prep.services.state')
        .service('StateService', StateService)
        .provider('State', function () {
            return {
                $get: function () {
                    return state;
                }
            };
        });
})();