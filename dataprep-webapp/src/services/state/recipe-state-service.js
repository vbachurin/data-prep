(function() {
    'use strict';

    var playgroundState = {};

    function PlaygroundStateService() {
        return {
            setGridSelection: setGridSelection
        };

        function setGridSelection(column, line) {
            playgroundState.column = column;
            playgroundState.line = line;
        }
    }

    angular.module('data-prep.services.state')
        .service('PlaygroundStateService', PlaygroundStateService)
        .constant('playgroundState', playgroundState);
})();