(function() {
    'use strict';

    var state = {};

    function StateService() {
        return {
            setColumn: setColumn,
            setLine: setLine,
            resetPlayground: resetPlayground
        };

        function setColumn(column) {
            state.column = column;
        }

        function setLine(line) {
            state.line = line;
        }

        function resetPlayground() {
            state.column = null;
            state.line = null;
        }
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